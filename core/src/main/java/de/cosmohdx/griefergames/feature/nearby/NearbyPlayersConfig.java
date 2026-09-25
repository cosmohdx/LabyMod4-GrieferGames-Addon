package de.cosmohdx.griefergames.feature.nearby;

import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class NearbyPlayersConfig extends FeatureConfig {

  @SliderSetting(min = NearbyPlayersService.MIN_RADIUS, max = NearbyPlayersService.MAX_RADIUS)
  private final ConfigProperty<Integer> radius = new ConfigProperty<>(16);

  @SliderSetting(min = NearbyPlayersService.MIN_LIMIT, max = NearbyPlayersService.MAX_LIMIT)
  private final ConfigProperty<Integer> limit = new ConfigProperty<>(8);

  @SwitchSetting
  private final ConfigProperty<Boolean> onlyLineOfSight = new ConfigProperty<>(true);

  @DropdownSetting
  private final ConfigProperty<DistanceDisplay> distanceDisplay = new ConfigProperty<>(DistanceDisplay.COARSE);

  @SwitchSetting
  private final ConfigProperty<Boolean> onlyOnCitybuild = new ConfigProperty<>(true);

  public NearbyPlayersConfig() {
    super(false);
  }

  public int radius() {
    Integer value = this.radius.get();
    return (int) NearbyPlayersService.clampRadius(value == null ? NearbyPlayersService.MIN_RADIUS : value);
  }

  public int limit() {
    Integer value = this.limit.get();
    return NearbyPlayersService.clampLimit(value == null ? NearbyPlayersService.MIN_LIMIT : value);
  }

  public boolean onlyLineOfSight() {
    return !Boolean.FALSE.equals(this.onlyLineOfSight.get());
  }

  public DistanceDisplay distanceDisplay() {
    DistanceDisplay value = this.distanceDisplay.get();
    return value == null ? DistanceDisplay.COARSE : value;
  }

  public boolean onlyOnCitybuild() {
    return !Boolean.FALSE.equals(this.onlyOnCitybuild.get());
  }
}
