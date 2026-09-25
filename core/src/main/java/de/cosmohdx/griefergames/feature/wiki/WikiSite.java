package de.cosmohdx.griefergames.feature.wiki;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import net.labymod.api.Laby;

/** Reads the current server-rendered wiki, including its three sections and navigation. */
final class WikiSite {
  private static final URI ORIGIN = URI.create("https://wiki.griefergames.net/");
  private static final HttpClient HTTP = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(8)).followRedirects(HttpClient.Redirect.NORMAL).build();
  private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(task -> {
    Thread thread = new Thread(task, "griefergames-wiki");
    thread.setDaemon(true);
    return thread;
  });

  enum Tab {
    GENERAL("Allgemein", "/allgemein"), LEGACY("1.8", "/1-8"), CLOUD("Cloud", "/cloud");
    final String label;
    final String route;
    Tab(String label, String route) { this.label = label; this.route = route; }
    static Tab fromRoute(String route) {
      if (route.startsWith("/1-8")) return LEGACY;
      if (route.startsWith("/cloud")) return CLOUD;
      return GENERAL;
    }
  }

  enum Kind { HEADING, PARAGRAPH, IMAGE, HINT, CODE, LINK, TABLE_ROW, DIVIDER }
  record Link(String title, String href) {}
  record Block(Kind kind, String text, String target, List<Link> links, int level) {}
  record NavEntry(String title, String route, String section, String parentRoute, int depth, boolean children) {}
  record Page(String title, String description, Tab tab, List<NavEntry> navigation, List<Block> blocks) {}
  record Result(Page page, String error) {}

  private WikiSite() {}

  static void load(String route, Consumer<Result> callback) {
    String safe = safeRoute(route);
    if (safe == null) {
      deliver(callback, new Result(null, "Ungültiger Wiki-Link"));
      return;
    }
    WORKER.execute(() -> {
      Path cache = cachePath(safe);
      Page cached = null;
      try {
        if (Files.isRegularFile(cache)) {
          cached = parse(Files.readString(cache, StandardCharsets.UTF_8));
          deliver(callback, new Result(cached, ""));
        }
      } catch (Exception ignored) {
        // An invalid cache is replaced by the live page.
      }
      try {
        String html = fetch(safe);
        Page fresh = parse(html);
        Files.createDirectories(cache.getParent());
        Path temporary = cache.resolveSibling(cache.getFileName() + ".tmp");
        Files.writeString(temporary, html, StandardCharsets.UTF_8);
        try {
          Files.move(temporary, cache, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
          Files.move(temporary, cache, StandardCopyOption.REPLACE_EXISTING);
        }
        deliver(callback, new Result(fresh, ""));
      } catch (Exception error) {
        if (cached == null) {
          String message = error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
          deliver(callback, new Result(null, message));
        }
      }
    });
  }

  private static String fetch(String route) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(ORIGIN.resolve(route)).timeout(Duration.ofSeconds(15))
        .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Chrome/125.0 Safari/537.36")
        .header("Accept", "text/html,application/xhtml+xml").GET().build();
    HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    if (response.statusCode() != 200) throw new IOException("Wiki HTTP " + response.statusCode());
    return response.body();
  }

  private static Path cachePath(String route) {
    return Laby.labyAPI().labyModLoader().getGameDirectory().resolve("labymod-neo")
        .resolve("cache").resolve("griefergames-wiki-live")
        .resolve(route.substring(1).replace('/', '_') + ".html");
  }

  static String safeRoute(String route) {
    if (route == null || !route.startsWith("/") || route.contains("..") || route.contains("\\")
        || route.contains("?") || route.contains("#") || route.length() > 240) return null;
    if (route.equals("/allgemein") || route.startsWith("/allgemein/")
        || route.equals("/1-8") || route.startsWith("/1-8/")
        || route.equals("/cloud") || route.startsWith("/cloud/")) return route;
    return null;
  }

  static String resolveLink(String href) {
    if (href == null || href.isBlank() || href.startsWith("#")) return null;
    URI target;
    try { target = ORIGIN.resolve(href); } catch (RuntimeException ignored) { return null; }
    if (!"https".equals(target.getScheme())) return null;
    if ("wiki.griefergames.net".equalsIgnoreCase(target.getHost())) return safeRoute(target.getPath());
    return target.toString();
  }

  static String imageUrl(String src) {
    if (src == null || src.isBlank()) return null;
    try {
      URI target = ORIGIN.resolve(src);
      return "https".equals(target.getScheme()) ? target.toString() : null;
    } catch (RuntimeException ignored) { return null; }
  }

  static Page parse(String html) throws IOException {
    WikiHtml.Node root = WikiHtml.parse(html);
    WikiHtml.Node sidebar = root.firstClass("wiki-sidebar");
    WikiHtml.Node main = root.firstClass("wiki-content");
    if (sidebar == null || main == null) throw new IOException("Wiki-Seite enthält keine Navigation oder Inhalte");
    Tab tab = switch (sidebar.attr("data-tab-id")) {
      case "1-8" -> Tab.LEGACY; case "cloud" -> Tab.CLOUD; default -> Tab.GENERAL;
    };
    WikiHtml.Node title = main.firstClass("wiki-page-title");
    WikiHtml.Node description = main.firstClass("wiki-page-description");
    WikiHtml.Node body = main.firstClass("wiki-markdown-body");
    if (title == null || body == null) throw new IOException("Wiki-Artikel konnte nicht gelesen werden");
    List<NavEntry> navigation = new ArrayList<>();
    WikiHtml.Node nav = sidebar.firstClass("wiki-nav");
    WikiHtml.Node list = nav == null ? null : nav.firstClass("wiki-nav-root");
    if (list != null) parseNav(list, 0, "Startseite", "", navigation);
    List<Block> blocks = new ArrayList<>();
    for (WikiHtml.Node child : body.children()) readBlock(child, blocks);
    return new Page(title.text(), description == null ? "" : description.text(), tab,
        List.copyOf(navigation), List.copyOf(blocks));
  }

  private static void parseNav(WikiHtml.Node list, int depth, String initialSection,
                               String parentRoute, List<NavEntry> entries) {
    String section = initialSection;
    for (WikiHtml.Node item : list.children()) {
      if (!item.tag.equals("li")) continue;
      WikiHtml.Node header = item.firstClass("wiki-nav-item-header");
      if (header == null) continue;
      WikiHtml.Node group = header.firstClass("wiki-nav-group-title");
      if (group != null) section = group.text();
      WikiHtml.Node link = header.firstClass("wiki-nav-link");
      WikiHtml.Node children = item.firstClass("wiki-nav-children");
      WikiHtml.Node childList = children == null ? null : children.first("ul");
      String currentParent = parentRoute;
      if (link != null) {
        String route = safeRoute(link.attr("href"));
        if (route != null) {
          entries.add(new NavEntry(link.text(), route, section, parentRoute, depth, childList != null));
          currentParent = route;
        }
      }
      if (childList != null) parseNav(childList, depth + (link == null ? 0 : 1), section, currentParent, entries);
    }
  }

  private static void readBlock(WikiHtml.Node node, List<Block> blocks) {
    String tag = node.tag;
    if (tag.matches("h[1-6]")) {
      blocks.add(new Block(Kind.HEADING, node.text().replaceFirst("^#\\s*", ""), "", List.of(),
          Integer.parseInt(tag.substring(1))));
      return;
    }
    if (tag.equals("img")) {
      String url = imageUrl(node.attr("src"));
      if (url != null) blocks.add(new Block(Kind.IMAGE, node.attr("alt"), url, List.of(), 0));
      return;
    }
    if (tag.equals("p") || tag.equals("blockquote")) {
      String text = node.text();
      if (!text.isBlank()) {
        boolean emphasizedTitle = tag.equals("p") && node.children().size() == 1
            && (node.children().get(0).tag.equals("strong") || node.children().get(0).tag.equals("b"))
            && text.equals(node.children().get(0).text());
        blocks.add(new Block(tag.equals("blockquote") ? Kind.HINT
            : emphasizedTitle ? Kind.HEADING : Kind.PARAGRAPH,
            text, "", links(node), emphasizedTitle ? 3 : 0));
      }
      return;
    }
    if (tag.equals("pre")) {
      blocks.add(new Block(Kind.CODE, node.text(), "", List.of(), 0));
      return;
    }
    if (tag.equals("hr")) {
      blocks.add(new Block(Kind.DIVIDER, "", "", List.of(), 0));
      return;
    }
    if (tag.equals("ul") || tag.equals("ol")) {
      readList(node, blocks, 0);
      return;
    }
    if (tag.equals("table")) {
      int rowsBefore = blocks.size();
      List<String> headings = new ArrayList<>();
      for (WikiHtml.Node row : node.descendants("tr")) {
        List<String> cells = new ArrayList<>();
        boolean header = false;
        for (WikiHtml.Node cell : row.children()) {
          if (cell.tag.equals("th")) header = true;
          if (cell.tag.equals("th") || cell.tag.equals("td")) cells.add(cell.text());
        }
        if (cells.isEmpty()) continue;
        if (header) { headings = cells; continue; }
        List<String> details = new ArrayList<>();
        for (int i = 0; i < cells.size(); i++) {
          if (cells.get(i).isBlank()) continue;
          details.add(i < headings.size() && !headings.get(i).isBlank()
              ? headings.get(i) + ": " + cells.get(i) : cells.get(i));
        }
        blocks.add(new Block(Kind.TABLE_ROW, String.join("  •  ", details), "", links(row), 0));
      }
      if (blocks.size() == rowsBefore && !headings.isEmpty())
        blocks.add(new Block(Kind.TABLE_ROW, String.join("  •  ", headings), "", List.of(), 0));
      return;
    }
    if (tag.equals("a")) {
      String target = resolveLink(node.attr("href"));
      if (target != null && !node.text().isBlank())
        blocks.add(new Block(Kind.LINK, node.text(), target, List.of(), 0));
      return;
    }
    if (node.hasClass("wiki-hint")) {
      WikiHtml.Node text = node.firstClass("wiki-hint-content");
      if (text != null && !text.text().isBlank())
        blocks.add(new Block(Kind.HINT, text.text(), "", links(text), 0));
      return;
    }
    for (WikiHtml.Node child : node.children()) readBlock(child, blocks);
  }

  private static void readList(WikiHtml.Node list, List<Block> blocks, int depth) {
    int number = 1;
    for (WikiHtml.Node item : list.children()) {
      if (!item.tag.equals("li")) continue;
      String text = item.textWithoutNestedLists();
      if (!text.isBlank()) {
        String prefix = "  ".repeat(Math.min(depth, 3))
            + (list.tag.equals("ol") ? (number++) + ". " : "• ");
        blocks.add(new Block(Kind.PARAGRAPH, prefix + text, "", links(item), 0));
      }
      for (WikiHtml.Node child : item.children()) {
        if (child.tag.equals("ul") || child.tag.equals("ol")) readList(child, blocks, depth + 1);
      }
    }
  }

  private static List<Link> links(WikiHtml.Node node) {
    List<Link> found = new ArrayList<>();
    for (WikiHtml.Node anchor : node.descendants("a")) {
      String target = resolveLink(anchor.attr("href"));
      if (target != null && !anchor.text().isBlank()) found.add(new Link(anchor.text(), target));
    }
    return List.copyOf(found);
  }

  private static void deliver(Consumer<Result> callback, Result result) {
    Laby.labyAPI().minecraft().executeNextTick(() -> callback.accept(result));
  }
}
