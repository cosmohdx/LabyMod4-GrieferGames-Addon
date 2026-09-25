package de.cosmohdx.griefergames.feature.booster;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.util.I18n;

public class FlyBooster extends Booster {
	public FlyBooster() {
		super(I18n.translate(GrieferGames.get().namespace()+".hudWidget.gg_booster.fly"), "fly-booster", 3, false);
	}
}
