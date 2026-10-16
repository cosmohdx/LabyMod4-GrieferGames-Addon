package de.cosmohdx.griefergames.feature.chat.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Session log of chat lines. Disk writes run on a background thread. The file lives in the
 * temp directory and is removed on shutdown and on the next start if a previous session crashed.
 */
public final class ChatSearchLog {

  public static final String DIRECTORY_NAME = "labymod-griefergames";
  public static final String FILE_PREFIX = "chat-";
  public static final String FILE_SUFFIX = ".jsonl";

  private final Path directory;
  private final Path file;
  private final List<ChatSearchEntry> entries = new ArrayList<>();
  private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
  private final Object monitor = new Object();
  private volatile boolean closed;
  private Thread worker;

  public ChatSearchLog(Path directory) {
    this(directory, UUID.randomUUID().toString());
  }

  public ChatSearchLog(Path directory, String sessionId) {
    this.directory = directory;
    this.file = directory.resolve(FILE_PREFIX + sessionId + FILE_SUFFIX);
  }

  public static Path defaultDirectory() {
    return Path.of(System.getProperty("java.io.tmpdir"), DIRECTORY_NAME);
  }

  public Path file() {
    return this.file;
  }

  public void open() throws IOException {
    Files.createDirectories(this.directory);
    deleteStale(this.directory, this.file);
    if (Files.isRegularFile(this.file)) {
      this.load();
    } else {
      Files.createFile(this.file);
    }
    this.file.toFile().deleteOnExit();
    this.worker = new Thread(this::run, "griefergames-chat-search");
    this.worker.setDaemon(true);
    this.worker.start();
  }

  public void append(ChatSearchEntry entry) {
    if (entry == null || this.closed) {
      return;
    }
    synchronized (this.monitor) {
      if (this.closed) {
        return;
      }
      this.entries.add(entry);
    }
    this.submit(() -> this.writeLine(entry));
  }

  public List<ChatSearchEntry> snapshot() {
    synchronized (this.monitor) {
      return List.copyOf(this.entries);
    }
  }

  /**
   * Drops expired lines and rewrites the file. Safe to call from tests; the live log runs it
   * on the writer thread.
   */
  public List<ChatSearchEntry> applyRetention(long nowMillis, long retentionMillis, int maxLines) {
    synchronized (this.monitor) {
      List<ChatSearchEntry> kept = ChatSearchRetention.prune(
          this.entries, nowMillis, retentionMillis, maxLines);
      this.entries.clear();
      this.entries.addAll(kept);
      this.rewrite(kept);
      return List.copyOf(this.entries);
    }
  }

  public void submitRetention(long nowMillis, long retentionMillis, int maxLines) {
    this.submit(() -> this.applyRetention(nowMillis, retentionMillis, maxLines));
  }

  public void deleteNow() {
    this.submit(this::clear);
  }

  public void clear() {
    synchronized (this.monitor) {
      this.entries.clear();
      this.rewrite(List.of());
    }
  }

  public void shutdown() {
    this.closed = true;
    this.tasks.clear();
    Thread writer = this.worker;
    if (writer != null) {
      writer.interrupt();
      try {
        writer.join(1_000L);
      } catch (InterruptedException interrupted) {
        Thread.currentThread().interrupt();
      }
    }
    synchronized (this.monitor) {
      this.entries.clear();
      try {
        Files.deleteIfExists(this.file);
      } catch (IOException ignored) {
        this.file.toFile().delete();
      }
    }
  }

  public static int deleteStale(Path directory, Path current) throws IOException {
    if (directory == null || !Files.isDirectory(directory)) {
      return 0;
    }
    int removed = 0;
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, FILE_PREFIX + "*" + FILE_SUFFIX)) {
      for (Path path : stream) {
        if (current != null && path.equals(current)) {
          continue;
        }
        if (Files.deleteIfExists(path)) {
          removed++;
        }
      }
    }
    return removed;
  }

  private void writeLine(ChatSearchEntry entry) {
    synchronized (this.monitor) {
      if (this.closed) {
        return;
      }
      try {
        Files.writeString(
            this.file,
            ChatSearchLineCodec.encode(entry) + "\n",
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
      } catch (IOException ignored) {
        // The in-memory list still serves the open window for this session.
      }
    }
  }

  private void rewrite(List<ChatSearchEntry> kept) {
    try {
      Files.createDirectories(this.directory);
      StringBuilder builder = new StringBuilder();
      for (ChatSearchEntry entry : kept) {
        builder.append(ChatSearchLineCodec.encode(entry)).append('\n');
      }
      Files.writeString(
          this.file,
          builder.toString(),
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE
      );
    } catch (IOException ignored) {
      // Kept in memory until the next successful compact.
    }
  }

  private void load() throws IOException {
    List<String> lines = Files.readAllLines(this.file, StandardCharsets.UTF_8);
    synchronized (this.monitor) {
      this.entries.clear();
      for (String line : lines) {
        ChatSearchEntry entry = ChatSearchLineCodec.decode(line);
        if (entry != null) {
          this.entries.add(entry);
        }
      }
    }
  }

  private void submit(Runnable task) {
    if (this.closed || task == null) {
      return;
    }
    this.tasks.offer(task);
  }

  private void run() {
    while (!this.closed) {
      try {
        Runnable task = this.tasks.poll(1, TimeUnit.SECONDS);
        if (task != null && !this.closed) {
          task.run();
        }
      } catch (InterruptedException interrupted) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }
}
