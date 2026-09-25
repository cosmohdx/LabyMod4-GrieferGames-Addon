package de.cosmohdx.griefergames.feature.booster;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.util.I18n;

public class BreakBooster extends Booster {
	public BreakBooster() {
		super(I18n.translate(GrieferGames.get().namespace()+".hudWidget.gg_booster.break"), "break-booster", 4, false);
	}
}
