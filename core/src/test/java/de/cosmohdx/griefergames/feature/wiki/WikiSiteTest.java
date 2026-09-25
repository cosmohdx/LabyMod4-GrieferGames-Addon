package de.cosmohdx.griefergames.feature.wiki;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class WikiSiteTest {
  @Test
  void parsesSectionsAndBlocks() throws Exception {
    String html = """
      <aside class="wiki-sidebar" data-tab-id="cloud"><nav class="wiki-nav"><ul class="wiki-nav-root">
      <li><div class="wiki-nav-item-header"><a class="wiki-nav-link" href="/cloud">Startseite</a></div></li>
      <li><div class="wiki-nav-item-header"><span class="wiki-nav-group-title">Funktionen</span></div></li>
      <li><div class="wiki-nav-item-header"><a class="wiki-nav-link" href="/cloud/funktionen">Funktionen</a></div>
      <div class="wiki-nav-children"><ul><li><div class="wiki-nav-item-header"><a class="wiki-nav-link" href="/cloud/funktionen/items">Items</a></div></li></ul></div></li>
      </ul></nav></aside><main class="wiki-content"><h1 class="wiki-page-title">Startseite</h1>
      <p class="wiki-page-description">Überblick</p><div class="wiki-markdown-body">
      <p>Ein <strong>wichtiges</strong> Thema mit <a href="/allgemein">Allgemein</a>.</p>
      <div class="wiki-hint"><div class="wiki-hint-content">Ein Hinweis.</div></div>
      <table><tr><th>Name</th><th>Wert</th></tr></table>
      <ul><li>Elternpunkt<ul><li>Unterpunkt</li></ul></li></ul>
      <img src="/img/bild.png"></div></main>
      """;
    var page = WikiSite.parse(html);
    assertEquals(WikiSite.Tab.CLOUD, page.tab());
    assertEquals(3, page.navigation().size());
    assertEquals("Funktionen", page.navigation().get(2).section());
    assertEquals("/cloud/funktionen", page.navigation().get(2).parentRoute());
    assertEquals("Ein wichtiges Thema mit Allgemein.", page.blocks().get(0).text());
    assertEquals("/allgemein", page.blocks().get(0).links().get(0).href());
    assertTrue(page.blocks().stream().anyMatch(block -> block.kind() == WikiSite.Kind.HINT));
    assertTrue(page.blocks().stream().anyMatch(block -> block.kind() == WikiSite.Kind.TABLE_ROW));
    assertTrue(page.blocks().stream().anyMatch(block -> block.kind() == WikiSite.Kind.IMAGE));
    assertTrue(page.blocks().stream().anyMatch(block -> block.text().equals("• Elternpunkt")));
    assertTrue(page.blocks().stream().anyMatch(block -> block.text().equals("  • Unterpunkt")));
  }
  @Test
  void blocksUnsafeRoutes() {
    assertNull(WikiSite.safeRoute("/admin"));
    assertNull(WikiSite.safeRoute("/cloud/../secret"));
    assertNull(WikiSite.resolveLink("javascript:alert(1)"));
    assertEquals("/1-8", WikiSite.resolveLink("https://wiki.griefergames.net/1-8"));
  }
}
