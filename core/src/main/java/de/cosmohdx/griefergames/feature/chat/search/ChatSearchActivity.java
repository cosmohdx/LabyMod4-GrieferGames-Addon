package de.cosmohdx.griefergames.feature.chat.search;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.itemlist.RoundedPanel;
import java.util.ArrayList;
import java.util.List;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.serializer.gson.GsonComponentSerializer;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.types.SimpleActivity;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.CheckBoxWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.renderer.EntryRenderer;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.util.I18n;

@AutoActivity
@Link("chat-search.lss")
public class ChatSearchActivity extends SimpleActivity {

  private static final String I18N = "griefergames.chatsearch.";
  private static final int SCREEN = 0xF0101010;

  private TextFieldWidget search;
  private DropdownWidget<ChatSearchTabFilter> tabFilter;
  private DropdownWidget<ChatSearchTimeRange> timeRange;
  private CheckBoxWidget regex;
  private ComponentWidget status;
  private ScrollWidget scroll;
  private boolean initialized;
  private boolean updating;
  private boolean stickToBottom = true;
  private ChatSearchEntry focus;
  private int knownSize;

  public static void open() {
    GrieferGames addon = GrieferGames.get();
    if (addon != null && addon.chatSearch() != null) {
      addon.chatSearch().maintainNow();
    }
    Laby.labyAPI().minecraft().executeNextTick(() ->
        Laby.labyAPI().minecraft().minecraftWindow().displayScreen(new ChatSearchActivity())
    );
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    RoundedPanel background = new RoundedPanel(SCREEN, 0, 0);
    background.addId("chat-search-background");
    this.document().addChild(background);

    ComponentWidget title = ComponentWidget.i18n(I18N + "title");
    title.addId("chat-search-title");
    this.document().addChild(title);

    this.search = new TextFieldWidget();
    this.search.addId("chat-search-input");
    this.search.placeholder(Component.text(I18n.translate(I18N + "search")));
    this.search.updateListener(value -> {
      if (!this.updating) {
        this.stickToBottom = value == null || value.isBlank();
        this.focus = null;
        this.rebuild(this.stickToBottom);
      }
    });
    this.document().addChild(this.search);

    this.tabFilter = this.dropdown(ChatSearchTabFilter.values(), I18N + "tab.");
    this.tabFilter.addId("chat-search-tab");
    this.tabFilter.setSelected(ChatSearchTabFilter.ALL, false);
    this.tabFilter.setChangeListener(value -> this.rebuild(false));
    this.document().addChild(this.tabFilter);

    this.timeRange = this.dropdown(ChatSearchTimeRange.values(), I18N + "time.");
    this.timeRange.addId("chat-search-time");
    this.timeRange.setSelected(ChatSearchTimeRange.ALL, false);
    this.timeRange.setChangeListener(value -> this.rebuild(false));
    this.document().addChild(this.timeRange);

    this.regex = new CheckBoxWidget();
    this.regex.addId("chat-search-regex");
    this.regex.setPressable(() -> this.rebuild(false));
    this.document().addChild(this.regex);

    ComponentWidget regexLabel = ComponentWidget.i18n(I18N + "regex");
    regexLabel.addId("chat-search-regex-label");
    this.document().addChild(regexLabel);

    this.status = ComponentWidget.text("");
    this.status.addId("chat-search-status");
    this.document().addChild(this.status);

    this.rebuild(true);
  }

  @Override
  protected void postInitialize() {
    super.postInitialize();
    this.initialized = true;
    if (this.stickToBottom) {
      this.scrollTo(-1);
    }
  }

  @Override
  public void tick() {
    super.tick();
    GrieferGames addon = GrieferGames.get();
    if (addon == null || addon.chatSearch() == null || this.search == null) {
      return;
    }
    int size = addon.chatSearch().entries().size();
    if (size != this.knownSize && (this.search.getText() == null || this.search.getText().isBlank()) && this.focus == null) {
      boolean follow = this.scroll == null || this.scroll.session().isScrolledToBottom() || this.stickToBottom;
      this.rebuild(follow);
    }
  }

  private void rebuild(boolean toBottom) {
    float scrollY = 0;
    if (!toBottom && this.scroll != null && this.focus == null) {
      scrollY = this.scroll.session().getScrollPositionY();
    }
    if (this.scroll != null) {
      this.document().removeChild(this.scroll);
      this.scroll = null;
    }

    ChatSearchQuery query = ChatSearchQuery.compile(
        this.search == null ? "" : this.search.getText(),
        this.regex != null && this.regex.state() == CheckBoxWidget.State.CHECKED
    );
    ChatSearchTabFilter tab = this.tabFilter == null || this.tabFilter.getSelected() == null
        ? ChatSearchTabFilter.ALL
        : this.tabFilter.getSelected();
    ChatSearchTimeRange range = this.timeRange == null || this.timeRange.getSelected() == null
        ? ChatSearchTimeRange.ALL
        : this.timeRange.getSelected();
    long minTimestamp = range.minTimestamp(System.currentTimeMillis());

    List<ChatSearchEntry> shown = new ArrayList<>();
    GrieferGames addon = GrieferGames.get();
    List<ChatSearchEntry> history = addon == null || addon.chatSearch() == null
        ? List.of()
        : addon.chatSearch().entries();
    this.knownSize = history.size();
    for (ChatSearchEntry entry : history) {
      if (ChatSearchMatcher.accepts(entry, tab, minTimestamp, query)) {
        shown.add(entry);
      }
    }

    if (query.isInvalid()) {
      this.status.setText(I18n.translate(I18N + "invalidRegex"));
    } else {
      this.status.setText(I18n.translate(I18N + "count").replace("{count}", Integer.toString(shown.size())));
    }

    VerticalListWidget<Widget> lines = new VerticalListWidget<>();
    lines.addId("chat-search-lines");
    if (shown.isEmpty()) {
      ComponentWidget empty = ComponentWidget.i18n(query.isEmpty() ? I18N + "empty" : I18N + "noResults");
      empty.addId("chat-search-empty");
      lines.addChild(empty);
    } else {
      for (ChatSearchEntry entry : shown) {
        lines.addChild(this.line(entry, query));
      }
    }

    this.scroll = new ScrollWidget(lines);
    this.scroll.addId("chat-search-scroll");
    if (this.initialized) {
      this.document().addChildInitialized(this.scroll);
    } else {
      this.document().addChild(this.scroll);
    }
    if (toBottom) {
      this.stickToBottom = true;
      this.scrollTo(-1);
    } else if (this.focus != null) {
      this.scrollTo(this.indexOf(shown, this.focus));
      this.focus = null;
    } else if (this.initialized) {
      this.scroll.session().setScrollPositionY(scrollY);
    }
  }

  private Widget line(ChatSearchEntry entry, ChatSearchQuery query) {
    Component component = this.component(entry);
    if (!query.isEmpty() && !query.isInvalid()) {
      component = ChatSearchHighlighter.highlight(component, query);
    }
    ComponentWidget widget = ComponentWidget.component(component);
    widget.addId("chat-line");
    widget.setPressable(() -> this.focus(entry));
    return widget;
  }

  private void focus(ChatSearchEntry entry) {
    this.focus = entry;
    this.stickToBottom = false;
    this.updating = true;
    this.search.setText("");
    this.updating = false;
    this.tabFilter.setSelected(ChatSearchTabFilter.ALL, false);
    this.timeRange.setSelected(ChatSearchTimeRange.ALL, false);
    if (this.regex.state() == CheckBoxWidget.State.CHECKED) {
      this.regex.setState(CheckBoxWidget.State.UNCHECKED);
    }
    this.rebuild(false);
  }

  private Component component(ChatSearchEntry entry) {
    if (entry.componentJson() != null && !entry.componentJson().isBlank()) {
      try {
        Component component = GsonComponentSerializer.gson().deserialize(entry.componentJson());
        if (component != null) {
          return component;
        }
      } catch (RuntimeException ignored) {
        // Fall back to the stored plain text.
      }
    }
    return Component.text(entry.plainText());
  }

  private void scrollTo(int index) {
    if (this.scroll == null) {
      return;
    }
    if (index < 0) {
      this.scroll.scrollToBottom();
      this.scroll.session().skipAnimation();
      return;
    }
    float line = 14F;
    float target = Math.max(0F, index * line);
    float max = this.scroll.session().maxScrollPositionY();
    this.scroll.session().setScrollPositionY(max <= 0F ? target : Math.min(target, max));
    this.scroll.session().skipAnimation();
  }

  private int indexOf(List<ChatSearchEntry> entries, ChatSearchEntry entry) {
    for (int index = 0; index < entries.size(); index++) {
      if (same(entries.get(index), entry)) {
        return index;
      }
    }
    return entries.size() - 1;
  }

  private static boolean same(ChatSearchEntry left, ChatSearchEntry right) {
    return left.timestamp() == right.timestamp()
        && left.tab() == right.tab()
        && left.plainText().equals(right.plainText())
        && left.componentJson().equals(right.componentJson());
  }

  private <T extends Enum<T>> DropdownWidget<T> dropdown(T[] values, String prefix) {
    DropdownWidget<T> dropdown = new DropdownWidget<>();
    dropdown.addAll(values);
    dropdown.setEntryRenderer(new EnumRenderer<>(prefix));
    return dropdown;
  }

  private static final class EnumRenderer<T extends Enum<T>> implements EntryRenderer<T> {
    private final String prefix;

    private EnumRenderer(String prefix) {
      this.prefix = prefix;
    }

    @Override
    public float getWidth(T entry, float maxWidth) {
      return maxWidth;
    }

    @Override
    public float getHeight(T entry, float maxHeight) {
      return 16F;
    }

    @Override
    public Widget createEntryWidget(T entry) {
      return ComponentWidget.text(I18n.translate(this.prefix + entry.name().toLowerCase()));
    }

    @Override
    public Widget createSelectedWidget(T entry) {
      return this.createEntryWidget(entry);
    }
  }
}
