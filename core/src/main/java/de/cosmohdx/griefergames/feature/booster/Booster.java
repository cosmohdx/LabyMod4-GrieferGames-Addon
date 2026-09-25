package de.cosmohdx.griefergames.feature.booster;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.texture.ThemeTextureLocation;
import net.labymod.api.util.I18n;
import net.labymod.api.util.time.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Booster {
	private final String nameKey;
	private String type;
	private boolean highlightDuration = false;
	private boolean stackable;
	private int count;
	private long nextDurationBlink = 0;
	private List<Long> endTimes = new ArrayList<>();
  private Icon icon;
  private boolean dummy;

	Booster(String nameKey, String type, int iconIndex, boolean stackable) {
		this.nameKey = nameKey;
		this.type = type;
		this.count = 0;
		this.stackable = stackable;
    this.icon = Icon.sprite(ThemeTextureLocation.of(GrieferGames.get().namespace(), "hud/booster_widget", 512, 64), iconIndex, 0, 64);
  }

	public String getDurationString() {
    if(dummy) return "20:00";

		if(this.endTimes.size() == 0) return I18n.translate(GrieferGames.get().namespace()+".messages.on");

		long remainingTime = this.endTimes.get(0) - System.currentTimeMillis();
		long displayTime = Math.abs(remainingTime);

		String displayString = TimeUtil.formatTickDuration((int)displayTime / 1000 * 20);

		return (remainingTime < 0 ? "+" : "") + displayString;
	}

	public boolean shouldHighlightDuration() {
    if(dummy) return false;

		if(this.endTimes.size() == 0) return false;

		long remainingTime = this.endTimes.get(0) - System.currentTimeMillis();

		if(remainingTime < 0 || remainingTime > TimeUnit.SECONDS.toMillis(10)) return false;

		if (System.currentTimeMillis() > this.nextDurationBlink) {
			this.nextDurationBlink = System.currentTimeMillis() + 500L;
			this.highlightDuration = !this.highlightDuration;
		}

		return highlightDuration;
	}

	public void addBooster(long duration) {
		this.count++;
		if(this.stackable) {
			endTimes.add(System.currentTimeMillis() + duration);
		} else {
			if(endTimes.size() == 0) {
				endTimes.add(System.currentTimeMillis() + duration);
			} else if(endTimes.get(0) < System.currentTimeMillis()) {
				endTimes.set(0, System.currentTimeMillis() + duration);
			} else {
				endTimes.set(0, endTimes.get(0) + duration);
			}
		}
	}

	public void setBooster(int count, List<Long> durations) {
		this.count = count;
		this.endTimes.clear();
		if(this.stackable) {
			for(Long duration : durations) {
				this.endTimes.add(System.currentTimeMillis() + duration);
			}
		} else {
			this.endTimes.add(System.currentTimeMillis() + durations.get(0));
		}
	}

	public void removeBooster() {
		if(this.count <= 0) return;
		this.count--;
		if(this.stackable) {
			endTimes.remove(0);
		}
	}

	public String getName() {
		return I18n.translate(GrieferGames.get().namespace() + ".hudWidget.gg_booster." + this.nameKey);
	}

	public int getCount() {
    if(dummy) return 2;
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<Long> getEndTimes() {
		return endTimes;
	}

	public void setStackable(boolean stackable) {
		this.stackable = stackable;
	}

	public boolean isStackable() {
		return stackable;
	}

	public String getType() {
		return type;
	}

  public Icon getIcon() {
    return icon;
  }

  public void setDummy(boolean dummy) {
    this.dummy = dummy;
  }
}
