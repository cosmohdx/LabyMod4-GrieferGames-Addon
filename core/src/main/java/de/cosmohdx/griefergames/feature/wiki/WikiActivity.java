package de.cosmohdx.griefergames.feature.wiki;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.types.SimpleActivity;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.gui.screen.widget.attributes.ObjectFitType;

/** Compact native reader for the live GrieferGames wiki. */
@AutoActivity
@Link("wiki.lss")
public final class WikiActivity extends SimpleActivity {
  private WikiSite.Tab tab = WikiSite.Tab.GENERAL;
  private WikiSite.Page page;
  private String route = WikiSite.Tab.GENERAL.route;
  private String searchText = "";
  private final Set<String> expanded = new HashSet<>();
  private int generation;
  private boolean closed;
  private ComponentWidget status;
  private ComponentWidget title;
  private ComponentWidget description;
  private final WikiPanelWidget[] tabWidgets = new WikiPanelWidget[3];
  private ScrollWidget navigation;
  private ScrollWidget article;

  @Override
  public void initialize(Parent parent) {
    this.closed = false;
    super.initialize(parent);
    ComponentWidget brand = ComponentWidget.text("GRIEFERGAMES  /  WIKI");
    brand.addId("wiki-brand");
    this.document().addChild(brand);
    this.showTabs();
    TextFieldWidget search = new TextFieldWidget();
    search.addId("wiki-search");
    search.placeholder(Component.text("Wiki durchsuchen…"));
    search.setText(this.searchText);
    search.updateListener(value -> { this.searchText = value; this.showNavigation(); });
    this.document().addChild(search);
    this.title = ComponentWidget.text("Wiki wird geladen…");
    this.title.addId("wiki-article-title");
    this.document().addChild(this.title);
    this.description = ComponentWidget.text("");
    this.description.addId("wiki-description");
    this.document().addChild(this.description);
    this.status = ComponentWidget.text("Verbinde mit dem Wiki…");
    this.status.addId("wiki-status");
    this.document().addChild(this.status);
    this.showRoute(this.route);
  }

  @Override
  public void onCloseScreen() {
    this.closed = true;
    this.generation++;
    this.clearNavigation();
    this.clearArticle();
    this.status = null;
    super.onCloseScreen();
  }

  private void showTabs() {
    for (WikiSite.Tab option : WikiSite.Tab.values()) {
      int index = option.ordinal();
      if (this.tabWidgets[index] != null) this.document().removeChild(this.tabWidgets[index]);
      boolean active = option == this.tab;
      WikiPanelWidget widget = new WikiPanelWidget(active ? 0xFFEC7900 : 0xFF292929, 0, 6);
      widget.addId(switch (option) { case GENERAL -> "wiki-general"; case LEGACY -> "wiki-legacy"; case CLOUD -> "wiki-cloud"; });
      widget.setPressable(() -> this.showRoute(option.route));
      ComponentWidget label = ComponentWidget.text(option.label);
      label.addId(active ? "wiki-tab-active" : "wiki-tab-label");
      widget.addChild(label);
      this.tabWidgets[index] = widget;
      this.document().addChildInitialized(widget);
    }
  }

  private void showRoute(String next) {
    if (WikiSite.safeRoute(next) == null) return;
    this.route = next;
    this.tab = WikiSite.Tab.fromRoute(next);
    this.showTabs();
    this.title.setText("Wiki wird geladen…");
    this.description.setText("");
    this.status.setText("Lade " + this.tab.label + "…");
    this.clearArticle();
    int requested = ++this.generation;
    WikiSite.load(next, result -> {
      if (this.closed || this.status == null || requested != this.generation) return;
      if (result.page() == null) {
        this.status.setText("Wiki nicht erreichbar: " + result.error());
        return;
      }
      this.page = result.page();
      this.tab = this.page.tab();
      this.showTabs();
      this.title.setText(this.page.title());
      this.description.setText(this.page.description());
      this.status.setText(this.tab.label + " · Live-Wiki");
      this.expandAncestors();
      this.showNavigation();
      this.renderArticle();
    });
  }

  private void expandAncestors() {
    if (this.page == null) return;
    String current = this.route;
    for (int i = 0; i < 16; i++) {
      String parent = null;
      for (WikiSite.NavEntry entry : this.page.navigation()) {
        if (entry.route().equals(current)) { parent = entry.parentRoute(); break; }
      }
      if (parent == null || parent.isBlank()) return;
      this.expanded.add(parent);
      current = parent;
    }
  }

  private void clearNavigation() {
    if (this.navigation != null) this.document().removeChild(this.navigation);
    this.navigation = null;
  }

  private void showNavigation() {
    if (this.closed || this.page == null) return;
    float scroll = this.navigation == null ? 0 : this.navigation.session().getScrollPositionY();
    VerticalListWidget<Widget> entries = new VerticalListWidget<>();
    entries.addId("wiki-nav-list");
    String query = this.searchText.strip().toLowerCase(java.util.Locale.ROOT);
    String section = "";
    int shown = 0;
    for (WikiSite.NavEntry entry : this.page.navigation()) {
      if (!query.isBlank()) {
        if (!entry.title().toLowerCase(java.util.Locale.ROOT).contains(query)) continue;
      } else if (entry.depth() > 0 && !this.expanded.contains(entry.parentRoute())) continue;
      if (!entry.section().isBlank() && !entry.section().equals(section)) {
        ComponentWidget heading = ComponentWidget.text(entry.section().toUpperCase(java.util.Locale.ROOT));
        heading.addId("wiki-section");
        entries.addChild(heading);
        section = entry.section();
      }
      boolean active = entry.route().equals(this.route);
      WikiPanelWidget row = new WikiPanelWidget(active ? 0xFF503522 : 0x00242424,
          active ? 0xFFED7900 : 0, 5);
      row.addId("wiki-row");
      row.setPressable(() -> this.showRoute(entry.route()));
      ComponentWidget label = ComponentWidget.text("  ".repeat(Math.min(entry.depth(), 4)) + entry.title());
      label.addId(active ? "wiki-row-active" : "wiki-row-label");
      row.addChild(label);
      if (entry.children()) {
        ComponentWidget arrow = ComponentWidget.text(this.expanded.contains(entry.route()) ? "⌄" : "›");
        arrow.addId("wiki-row-arrow");
        arrow.setPressable(() -> {
          if (!this.expanded.remove(entry.route())) this.expanded.add(entry.route());
          this.showNavigation();
        });
        row.addChild(arrow);
      }
      entries.addChild(row);
      shown++;
    }
    if (shown == 0) {
      ComponentWidget empty = ComponentWidget.text("Keine passenden Artikel");
      empty.addId("wiki-empty");
      entries.addChild(empty);
    }
    this.clearNavigation();
    this.navigation = new ScrollWidget(entries);
    this.navigation.addId("wiki-navigation");
    this.document().addChildInitialized(this.navigation);
    this.navigation.session().setScrollPositionY(scroll);
  }

  private void clearArticle() {
    if (this.article != null) this.document().removeChild(this.article);
    this.article = null;
  }

  private void renderArticle() {
    if (this.page == null) return;
    VerticalListWidget<Widget> content = new VerticalListWidget<>();
    content.addId("wiki-content");
    int images = 0;
    for (WikiSite.Block block : this.page.blocks()) {
      if (block.kind() == WikiSite.Kind.IMAGE) {
        if (images++ >= 24) continue;
        Icon icon = switch (block.target()) {
          case "https://wiki.griefergames.net/img/wiki-banners/banner-allgemein.webp" -> Icon.texture(ResourceLocation.create("griefergames", "textures/wiki/banner-allgemein.png"));
          case "https://wiki.griefergames.net/img/wiki-banners/banner-1-8.webp" -> Icon.texture(ResourceLocation.create("griefergames", "textures/wiki/banner-1-8.png"));
          case "https://wiki.griefergames.net/img/wiki-banners/banner-cloud.webp" -> Icon.texture(ResourceLocation.create("griefergames", "textures/wiki/banner-cloud.png"));
          default -> Icon.url(block.target());
        };
        IconWidget image = new IconWidget(icon);
        image.addId("wiki-image");
        image.objectFit().set(ObjectFitType.CONTAIN);
        content.addChild(image);
      } else if (block.kind() == WikiSite.Kind.DIVIDER) {
        ComponentWidget divider = ComponentWidget.text("────────────────────────────");
        divider.addId("wiki-divider");
        content.addChild(divider);
      } else if (!block.text().isBlank()) {
        ComponentWidget line = ComponentWidget.text(block.text());
        line.addId(switch (block.kind()) {
          case HEADING -> block.level() <= 2 ? "wiki-heading" : "wiki-subheading";
          case HINT -> "wiki-hint";
          case CODE -> "wiki-code";
          case TABLE_ROW -> "wiki-table-row";
          case LINK -> "wiki-article-link";
          default -> "wiki-paragraph";
        });
        if (block.kind() == WikiSite.Kind.LINK) line.setPressable(() -> this.follow(block.target()));
        content.addChild(line);
      }
      for (WikiSite.Link link : block.links()) {
        ComponentWidget button = ComponentWidget.text("› " + link.title());
        button.addId("wiki-article-link");
        button.setPressable(() -> this.follow(link.href()));
        content.addChild(button);
      }
    }
    this.clearArticle();
    this.article = new ScrollWidget(content);
    this.article.addId("wiki-article");
    this.document().addChildInitialized(this.article);
  }

  private void follow(String target) {
    if (target == null) return;
    if (target.startsWith("/")) this.showRoute(target);
    else if (target.startsWith("https://")) Laby.labyAPI().minecraft().chatExecutor().openUrl(target);
  }
}
