package de.cosmohdx.griefergames.feature.itemlist;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.itemlist.ItemCatalog.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.serializer.legacy.LegacyComponentSerializer;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.types.SimpleActivity;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.key.InputType;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.TilesGridWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.util.I18n;

@AutoActivity
@Link("item-list.lss")
public class ItemListActivity extends SimpleActivity {

  private static final int PAGE = 40;
  private static final String I18N = "griefergames.itemlist.";

  private final ItemImages images = ItemImages.get();
  private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();
  private final Map<String, Integer> visibleLimit = new HashMap<>();
  private final List<Widget> detailWidgets = new ArrayList<>();

  private TextFieldWidget search;
  private DropdownWidget<String> category;
  private ComponentWidget status;
  private VerticalListWidget<Widget> sections;
  private boolean initialized;
  private boolean active;

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.active = true;

    ComponentWidget title = ComponentWidget.i18n(I18N + "title");
    title.addId("item-list-title");

    this.search = new TextFieldWidget();
    this.search.addId("item-list-search");
    this.search.placeholder(Component.text(I18n.translate(I18N + "search")));
    this.search.updateListener(value -> {
      this.visibleLimit.clear();
      this.rebuild();
    });

    this.category = new DropdownWidget<>();
    this.category.addId("item-list-category");
    this.category.setChangeListener(value -> {
      this.visibleLimit.clear();
      this.rebuild();
    });

    this.status = ComponentWidget.text("");
    this.status.addId("item-list-status");

    this.sections = new VerticalListWidget<>();
    this.sections.addId("item-list-sections");
    ScrollWidget scroll = new ScrollWidget(this.sections);
    scroll.addId("item-list-scroll");

    this.document().addChild(title);
    this.document().addChild(this.search);
    this.document().addChild(this.category);
    this.document().addChild(this.status);
    this.document().addChild(scroll);

    this.images.prepare(this::onCatalogUpdated);
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
    if (!this.detailWidgets.isEmpty() && key == Key.ESCAPE) {
      this.closeDetail();
      return true;
    }
    return super.keyPressed(key, type);
  }

  private void onCatalogUpdated() {
    if (!this.active) {
      return;
    }
    this.fillCategories();
    this.rebuild();
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

  private void rebuild() {
    this.clear(this.sections);
    ItemCatalog catalog = this.images.catalog();
    if (catalog.isEmpty()) {
      this.status.setText(I18n.translate(I18N + "loading"));
      return;
    }

    String query = this.search.getText();
    String selectedCategory = this.category.getSelected();
    int shown = 0;
    for (String network : this.networkOrder(catalog)) {
      List<Entry> matches = catalog.filter(network, selectedCategory, query);
      shown += matches.size();
      this.addSection(network, matches);
    }
    this.status.setText(I18n.translate(I18N + "count").replace("{count}", Integer.toString(shown)));
  }

  private void addSection(String network, List<Entry> matches) {
    ComponentWidget header = ComponentWidget.text(network);
    header.addId("network-name");
    this.attach(this.sections, header);

    if (matches.isEmpty()) {
      ComponentWidget empty = ComponentWidget.i18n(I18N + "empty");
      empty.addId("network-empty");
      this.attach(this.sections, empty);
      return;
    }

    int limit = this.visibleLimit.getOrDefault(network, PAGE);
    TilesGridWidget<DivWidget> grid = new TilesGridWidget<>();
    grid.addId("item-grid");
    this.attach(this.sections, grid);
    int end = Math.min(limit, matches.size());
    for (int index = 0; index < end; index++) {
      this.addCard(grid, matches.get(index));
    }
    if (end < matches.size()) {
      ButtonWidget more = ButtonWidget.i18n(I18N + "more", () -> {
        this.visibleLimit.put(network, limit + PAGE);
        this.rebuild();
      });
      more.addId("load-more");
      this.attach(this.sections, more);
    }
  }

  private void addCard(TilesGridWidget<DivWidget> grid, Entry entry) {
    DivWidget card = new DivWidget();
    card.addId("item-card");
    card.setPressable(() -> this.openDetail(entry));

    if (!entry.label().isBlank()) {
      ComponentWidget label = ComponentWidget.component(this.text(entry.label()));
      label.addId("card-label");
      card.addChild(label);
    }

    IconWidget icon = new IconWidget(this.images.icon(entry));
    icon.addId("card-image");
    card.addChild(icon);

    ComponentWidget title = ComponentWidget.component(this.text(entry.title()));
    title.addId("card-title");
    card.addChild(title);

    if (this.initialized) {
      grid.addTileInitialized(card);
    } else {
      grid.addTile(card);
    }
  }

  private void openDetail(Entry entry) {
    this.closeDetail();

    DivWidget dimmer = new DivWidget();
    dimmer.addId("detail-dimmer");
    dimmer.setPressable(this::closeDetail);

    DivWidget panel = new DivWidget();
    panel.addId("detail-panel");
    panel.setPressable(() -> {
    });

    VerticalListWidget<Widget> content = new VerticalListWidget<>();
    content.addId("detail-content");
    IconWidget icon = new IconWidget(this.images.icon(entry));
    icon.addId("detail-image");
    content.addChild(icon);
    ComponentWidget title = ComponentWidget.component(this.text(entry.title()));
    title.addId("detail-title");
    content.addChild(title);
    if (!entry.description().isBlank()) {
      ComponentWidget description = ComponentWidget.component(this.text(entry.description()));
      description.addId("detail-description");
      content.addChild(description);
    }
    if (!entry.credits().isBlank()) {
      ComponentWidget credits = ComponentWidget.component(this.text(entry.credits()));
      credits.addId("detail-credits");
      content.addChild(credits);
    }
    ScrollWidget scroll = new ScrollWidget(content);
    scroll.addId("detail-scroll");
    panel.addChild(scroll);

    ButtonWidget close = ButtonWidget.i18n(I18N + "close", this::closeDetail);
    close.addId("detail-close");
    panel.addChild(close);

    this.detailWidgets.add(dimmer);
    this.detailWidgets.add(panel);
    this.document().addChildInitialized(dimmer);
    this.document().addChildInitialized(panel);
  }

  private void closeDetail() {
    for (Widget widget : this.detailWidgets) {
      this.document().removeChild(widget);
    }
    this.detailWidgets.clear();
  }

  private List<String> networkOrder(ItemCatalog catalog) {
    List<String> networks = new ArrayList<>(catalog.networks());
    String preferred = GrieferGames.get().state().isSubServerType(SubServerType.CLOUD) ? "Cloud" : "1.8";
    if (networks.remove(preferred)) {
      networks.add(0, preferred);
    }
    return networks;
  }

  private Component text(String value) {
    if (value == null || value.isEmpty()) {
      return Component.empty();
    }
    return this.legacy.deserialize(value);
  }

  private <T extends Widget> void attach(VerticalListWidget<T> parent, T child) {
    if (this.initialized) {
      parent.addChildInitialized(child);
    } else {
      parent.addChild(child);
    }
  }

  private <T extends Widget> void clear(VerticalListWidget<T> parent) {
    List<T> children = new ArrayList<>(parent.getChildren());
    for (T child : children) {
      parent.removeChild(child);
    }
  }
}
