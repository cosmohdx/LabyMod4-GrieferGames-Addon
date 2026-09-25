package de.cosmohdx.griefergames.feature.booster;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.util.I18n;

public class ExperienceBooster extends Booster {
	public ExperienceBooster() {
		super(I18n.translate(GrieferGames.get().namespace()+".hudWidget.gg_booster.experience"), "erfahrung-booster", 1, true);
	}
}
