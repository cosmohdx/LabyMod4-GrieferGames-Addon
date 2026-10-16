package de.cosmohdx.griefergames.feature.chat.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ChatSearchLogTest {

  @TempDir
  Path temp;

  @Test
  void deletesStaleSessionFilesOnOpenAndRemovesTheFileOnShutdown() throws Exception {
    Path stale = temp.resolve("chat-old.jsonl");
    Files.writeString(stale, "{\"timestamp\":1,\"tab\":\"MAIN\",\"subserver\":\"\",\"component\":{},\"text\":\"x\"}\n");

    ChatSearchLog log = new ChatSearchLog(temp, "session");
    log.open();
    assertFalse(Files.exists(stale));
    assertTrue(Files.exists(log.file()));

    log.append(new ChatSearchEntry(System.currentTimeMillis(), ChatSearchTab.MAIN, "cb1", "{\"text\":\"hi\"}", "hi"));
    Thread.sleep(200L);
    assertEquals(1, log.snapshot().size());

    log.shutdown();
    assertFalse(Files.exists(log.file()));
    assertTrue(log.snapshot().isEmpty());
  }

  @Test
  void pruneCompactsInMemoryAndOnDisk() throws Exception {
    ChatSearchLog log = new ChatSearchLog(temp, "prune");
    log.open();
    long now = System.currentTimeMillis();
    log.append(new ChatSearchEntry(now - 60_000L, ChatSearchTab.MAIN, "", "{\"text\":\"old\"}", "old"));
    log.append(new ChatSearchEntry(now, ChatSearchTab.MAIN, "", "{\"text\":\"new\"}", "new"));
    Thread.sleep(200L);

    log.applyRetention(now, 10_000L, 10_000);

    assertEquals(1, log.snapshot().size());
    assertEquals("new", log.snapshot().get(0).plainText());
    String disk = Files.readString(log.file());
    assertTrue(disk.contains("new"));
    assertFalse(disk.contains("\"text\":\"old\"") || disk.contains("\"old\"}"));
    log.shutdown();
  }
}
