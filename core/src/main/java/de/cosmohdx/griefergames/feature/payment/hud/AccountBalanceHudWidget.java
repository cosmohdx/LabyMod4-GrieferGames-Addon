package de.cosmohdx.griefergames.feature.payment.hud;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.payment.balance.MoneyFormat;
import java.math.BigDecimal;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;

abstract class AccountBalanceHudWidget extends TextHudWidget<MoneyHudWidgetConfig> {

  private final GrieferGames griefergames;
  private final String widgetId;
  private final BigDecimal editorAmount;
  private final BooleanSupplier known;
  private final Supplier<BigDecimal> amount;
  private TextLine line;

  AccountBalanceHudWidget(
      GrieferGames griefergames,
      String widgetId,
      String icon,
      BigDecimal editorAmount,
      BooleanSupplier known,
      Supplier<BigDecimal> amount
  ) {
    super(widgetId, MoneyHudWidgetConfig.class);
    this.griefergames = griefergames;
    this.widgetId = widgetId;
    this.editorAmount = editorAmount;
    this.known = known;
    this.amount = amount;
    bindCategory(griefergames.getHudWidgetCategory());
    setIcon(Icon.texture(ResourceLocation.create(griefergames.namespace(), icon)));
  }

  @Override
  public void load(MoneyHudWidgetConfig config) {
    super.load(config);
    this.line = createLine(
        I18n.translate(this.griefergames.namespace() + ".hudWidget." + this.widgetId + ".name"),
        this.render(this.editorAmount, config)
    );
  }

  @Override
  public void onTick(boolean isEditorContext) {
    if (!isEditorContext
        && (this.griefergames.balances() == null || !this.known.getAsBoolean())) {
      return;
    }
    BigDecimal shown = isEditorContext ? this.editorAmount : this.amount.get();
    this.line.updateAndFlush(this.render(shown, this.getConfig()));
  }

  @Override
  public boolean isVisibleInGame() {
    if (!this.griefergames.state().isOnGrieferGames()
        || !this.griefergames.configuration().enabled().get()
        || this.griefergames.balances() == null
        || !this.known.getAsBoolean()) {
      return false;
    }
    MoneyHudWidgetConfig config = this.getConfig();
    return config == null
        || !Boolean.TRUE.equals(config.hideWhenZero().get())
        || this.amount.get().signum() != 0;
  }

  private String render(BigDecimal value, MoneyHudWidgetConfig config) {
    boolean showCents = config == null || Boolean.TRUE.equals(config.showCents().get());
    boolean compact = config != null && Boolean.TRUE.equals(config.compact().get());
    String text = this.symbols().format(value, showCents, compact);
    if (value.signum() < 0) {
      return "§c" + text;
    }
    return text;
  }

  private MoneyFormat symbols() {
    String root = this.griefergames.namespace() + ".hudWidget.money.";
    return new MoneyFormat(
        character(I18n.translate(root + "decimal"), '.'),
        character(I18n.translate(root + "grouping"), ','),
        suffix(I18n.translate(root + "thousand"), "K"),
        suffix(I18n.translate(root + "million"), "M"),
        suffix(I18n.translate(root + "billion"), "B"),
        suffix(I18n.translate(root + "trillion"), "T")
    );
  }

  private static char character(String value, char fallback) {
    if (value == null || value.length() != 1) {
      return fallback;
    }
    return value.charAt(0);
  }

  private static String suffix(String value, String fallback) {
    if (value == null || value.isBlank() || value.contains("hudWidget")) {
      return fallback;
    }
    return value;
  }
}
