package de.cosmohdx.griefergames.feature.itemlist;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.itemlist.ItemCatalog.Entry;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.types.SimpleActivity;
import net.labymod.api.client.gui.screen.key.InputType;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.attributes.ObjectFitType;
import net.labymod.api.client.gui.screen.widget.attributes.WidgetAlignment;
import net.labymod.api.client.gui.screen.widget.size.SizeType;
import net.labymod.api.client.gui.screen.widget.size.WidgetSide;
import net.labymod.api.client.gui.screen.widget.size.WidgetSize;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.TilesGridWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.render.font.FontSize;
import net.labymod.api.models.OperatingSystem;
import net.labymod.api.util.I18n;

@AutoActivity
@Link("item-list.lss")
public class ItemListActivity extends SimpleActivity {

  private static final int PAGE = 24;
  private static final String I18N = "griefergames.itemlist.";
  private static final int SCREEN = 0xE6101010;
  private static final int PANEL = 0xF0202020;
  private static final int PANEL_BORDER = 0xFF393939;
  private static final int CARD = 0xF0222222;
  private static final int LABEL = 0xFF4B3324;
  private static final int LABEL_BORDER = 0xFF875024;
  private static final int TITLE = 0xFF181818;
  private static final int TITLE_BORDER = 0xFF303030;
  private static final int DIMMER = 0xBD000000;
  private static final int DETAIL = 0xF5121212;
  private static final int DETAIL_BORDER = 0xFF3B3B3B;

  private final ItemImages images = ItemImages.get();
  private final Set<String> expanded = new HashSet<>();
  private final Map<String, Integer> loaded = new HashMap<>();

  private TextFieldWidget search;
  private DropdownWidget<String> category;
  private ComponentWidget status;
  private ScrollWidget scroll;
  private RoundedPanel dimmer;
  private boolean initialized;
  private boolean active = true;

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.active = true;
    this.expanded.clear();
    this.expanded.add(this.preferredNetwork());

    int width = this.screenWidth();
    int height = this.screenHeight();
    RoundedPanel background = new RoundedPanel(SCREEN, 0, 0);
    background.addId("catalog-background");
    this.size(background, width, height);

    ComponentWidget title = ComponentWidget.i18n(I18N + "title");
    title.addId("catalog-title");

    this.search = new TextFieldWidget();
    this.search.addId("catalog-search");
    this.search.placeholder(Component.text(I18n.translate(I18N + "search")));
    this.search.updateListener(value -> this.rebuild(false));

    this.category = new DropdownWidget<>();
    this.category.addId("catalog-category");
    this.category.setChangeListener(value -> this.rebuild(false));

    this.status = ComponentWidget.i18n(I18N + "loading");
    this.status.addId("catalog-status");

    this.document().addChild(background);
    this.document().addChild(title);
    this.document().addChild(this.search);
    this.document().addChild(this.category);
    this.document().addChild(this.status);

    this.images.prepare(() -> {
      if (this.active) {
        this.fillCategories();
        this.rebuild(this.scroll == null);
      }
    });
  }

  @Override
  protected void postInitialize() {
    super.postInitialize();
    this.initialized = true;
  }

  @Override
  public void onCloseScreen() {
    this.active = false;
    super.onCloseScreen();
  }

  @Override
  public boolean keyPressed(Key key, InputType type) {
    if (this.dimmer != null && key.equals(Key.ESCAPE)) {
      this.closeDetail();
      return true;
    }
    return super.keyPressed(key, type);
  }

  private void fillCategories() {
    List<String> names = new ArrayList<>(this.images.catalog().categoryNames());
    if (names.isEmpty()) {
      names.add(I18n.translate(I18N + "all"));
    }
    String selected = this.category.getSelected();
    this.category.clear();
    this.category.addAll(names);
    if (selected != null && names.contains(selected)) {
      this.category.setSelected(selected, false);
    } else {
      this.category.setSelected(names.get(0), false);
    }
  }

  private void rebuild(boolean resetScroll) {
    float scrollY = 0;
    if (!resetScroll && this.scroll != null) {
      scrollY = this.scroll.session().getScrollPositionY();
    }
    if (this.scroll != null) {
      this.document().removeChild(this.scroll);
      this.scroll = null;
    }

    ItemCatalog catalog = this.images.catalog();
    VerticalListWidget<Widget> sections = new VerticalListWidget<>();
    sections.addId("catalog-sections");
    if (catalog.isEmpty()) {
      this.status.setText(I18n.translate(I18N + "loading"));
    } else {
      String query = this.search.getText();
      String selectedCategory = this.category.getSelected();
      int shown = 0;
      for (String network : this.networkOrder(catalog)) {
        List<Entry> matches = catalog.filter(network, selectedCategory, query);
        shown += matches.size();
        this.addSection(sections, network, matches);
      }
      this.status.setText(I18n.translate(I18N + "count").replace("{count}", Integer.toString(shown)));
    }

    this.scroll = new ScrollWidget(sections);
    this.scroll.addId("catalog-scroll");
    if (this.initialized) {
      this.document().addChildInitialized(this.scroll);
    } else {
      this.document().addChild(this.scroll);
    }
    this.scroll.session().setScrollPositionY(scrollY);
  }

  private void addSection(VerticalListWidget<Widget> sections, String network, List<Entry> matches) {
    boolean open = this.expanded.contains(network);

    RoundedPanel header = new RoundedPanel(PANEL, PANEL_BORDER, 8);
    header.addId("network-header");
    header.setPressable(() -> this.toggle(network));

    ComponentWidget name = ComponentWidget.text(I18n.translate(I18N + "network").replace("{network}", network));
    name.addId("network-name");
    header.addChild(name);

    ComponentWidget arrow = ComponentWidget.text(open ? "▼" : "▶");
    arrow.addId("network-arrow");
    header.addChild(arrow);
    sections.addChild(header);

    if (!open) {
      return;
    }
    if (matches.isEmpty()) {
      ComponentWidget empty = ComponentWidget.i18n(I18N + "empty");
      empty.addId("network-empty");
      sections.addChild(empty);
      return;
    }

    int columns = this.columns(this.screenWidth());
    float gap = 8;
    float inner = this.screenWidth() * 0.90F - 14F;
    float cardWidth = Math.max(88, (inner - gap * (columns - 1)) / columns);
    float cardHeight = Math.max(150, Math.min(172, cardWidth + 16));
    TilesGridWidget<Widget> grid = new TilesGridWidget<>();
    grid.addId("item-grid");
    grid.tilesPerLine().set(columns);
    grid.tileHeight().set(cardHeight);
    grid.spaceBetweenEntries().set(gap);
    sections.addChild(grid);

    int limit = Math.min(this.loaded.getOrDefault(network, PAGE), matches.size());
    for (int index = 0; index < limit; index++) {
      grid.addTile(this.card(matches.get(index), cardWidth, cardHeight));
    }
    this.loaded.put(network, limit);
    if (limit < matches.size()) {
      ButtonWidget more = ButtonWidget.i18n(I18N + "more", () -> {
        this.loaded.put(network, this.loaded.getOrDefault(network, PAGE) + PAGE);
        this.rebuild(false);
      });
      more.addId("load-more");
      sections.addChild(more);
    }
  }

  private Widget card(Entry entry, float width, float height) {
    RoundedPanel card = new RoundedPanel(CARD, PANEL_BORDER, 8);
    card.addId("item-card");
    this.size(card, width, height);
    card.setPressable(() -> this.openDetail(entry));

    if (!entry.label().isBlank()) {
      RoundedPanel pill = new RoundedPanel(LABEL, LABEL_BORDER, 8);
      pill.addId("card-label-pill");
      float textWidth = Math.max(1, Laby.labyAPI().renderPipeline().componentRenderer().width(Component.text(entry.label())));
      float pillWidth = Math.min(width * 0.88F, Math.max(width * 0.48F, textWidth * 0.68F + 10F));
      this.size(pill, pillWidth, 18);
      ComponentWidget label = ComponentWidget.text(entry.label());
      label.addId("card-label");
      label.fontSize().set(FontSize.custom(Math.min(0.68F, (pillWidth - 10F) / textWidth)));
      pill.addChild(label);
      card.addChild(pill);
    }

    IconWidget icon = new IconWidget(this.images.icon(entry));
    icon.addId("card-image");
    icon.objectFit().set(ObjectFitType.CONTAIN);
    this.size(icon, width * 0.84F, Math.max(48, height - 64));
    card.addChild(icon);

    RoundedPanel titlePill = new RoundedPanel(TITLE, TITLE_BORDER, 7);
    titlePill.addId("card-title-pill");
    this.size(titlePill, width * 0.90F, 26);
    ComponentWidget title = ComponentWidget.text(entry.title());
    title.addId("card-title");
    titlePill.addChild(title);
    card.addChild(titlePill);
    return card;
  }

  private void openDetail(Entry entry) {
    if (this.dimmer != null) {
      return;
    }
    this.dimmer = new RoundedPanel(DIMMER, 0, 0);
    this.dimmer.addId("detail-dimmer");
    this.size(this.dimmer, this.screenWidth(), this.screenHeight());
    this.dimmer.setPressable(this::closeDetail);

    float panelWidth = this.screenWidth() * 0.48F;
    float panelHeight = this.screenHeight() * 0.72F;
    RoundedPanel panel = new RoundedPanel(DETAIL, DETAIL_BORDER, 10);
    panel.addId("detail-panel");
    this.size(panel, panelWidth, panelHeight);
    panel.alignmentX().set(WidgetAlignment.CENTER);
    panel.alignmentY().set(WidgetAlignment.CENTER);
    panel.left().set(this.screenWidth() / 2F);
    panel.top().set(this.screenHeight() / 2F);
    panel.setPressable(() -> {
    });
    this.dimmer.addChild(panel);

    VerticalListWidget<Widget> content = new VerticalListWidget<>();
    content.addId("modal-content");

    IconWidget icon = new IconWidget(this.images.icon(entry));
    icon.addId("modal-image");
    icon.objectFit().set(ObjectFitType.CONTAIN);
    icon.setPressable(() -> OperatingSystem.getPlatform().openUrl(this.itemPageUrl(entry)));
    content.addChild(icon);

    RoundedPanel titlePill = new RoundedPanel(PANEL, PANEL_BORDER, 8);
    titlePill.addId("modal-title-pill");
    ComponentWidget title = ComponentWidget.text(entry.title());
    title.addId("modal-title");
    titlePill.addChild(title);
    content.addChild(titlePill);

    String description = entry.description()
        .replace("<credits>", entry.credits())
        .replace("<creditsIM>", entry.credits())
        .replace("<creditsT>", entry.credits());
    ComponentWidget text = ComponentWidget.text(description);
    text.addId("modal-description");
    content.addChild(text);

    ScrollWidget modalScroll = new ScrollWidget(content);
    modalScroll.addId("modal-scroll");
    panel.addChild(modalScroll);

    ButtonWidget close = ButtonWidget.i18n(I18N + "close", this::closeDetail);
    close.addId("modal-close");
    panel.addChild(close);

    this.document().addChildInitialized(this.dimmer);
  }

  private void closeDetail() {
    if (this.dimmer == null) {
      return;
    }
    this.document().removeChild(this.dimmer);
    this.dimmer = null;
  }

  private void toggle(String network) {
    if (!this.expanded.add(network)) {
      this.expanded.remove(network);
    }
    this.rebuild(false);
  }

  private String itemPageUrl(Entry entry) {
    String slug = entry.title().replaceAll("\\s+", "_");
    String encoded = URLEncoder.encode(slug, StandardCharsets.UTF_8).replace("+", "%20").replace("%2A", "*");
    return "https://items.griefergames.net/#" + encoded;
  }

  private int columns(int width) {
    if (width < 500) {
      return 2;
    }
    if (width < 640) {
      return 3;
    }
    if (width < 700) {
      return 4;
    }
    return 5;
  }

  private List<String> networkOrder(ItemCatalog catalog) {
    List<String> networks = new ArrayList<>(catalog.networks());
    String preferred = this.preferredNetwork();
    if (networks.remove(preferred)) {
      networks.add(0, preferred);
    }
    return networks;
  }

  private String preferredNetwork() {
    return GrieferGames.get().state().isSubServerType(SubServerType.CLOUD) ? "Cloud" : "1.8";
  }

  private int screenWidth() {
    return Laby.labyAPI().minecraft().minecraftWindow().getScaledWidth();
  }

  private int screenHeight() {
    return Laby.labyAPI().minecraft().minecraftWindow().getScaledHeight();
  }

  private void size(Widget widget, float width, float height) {
    widget.setSize(SizeType.ACTUAL, WidgetSide.WIDTH, WidgetSize.fixed(width));
    widget.setSize(SizeType.ACTUAL, WidgetSide.HEIGHT, WidgetSize.fixed(height));
  }
}
