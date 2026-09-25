package de.cosmohdx.griefergames.feature.booster;

import de.cosmohdx.griefergames.GrieferGames;
import java.util.ArrayList;
import java.util.List;

public class BoosterController {
  private final GrieferGames griefergames;
  private final List<Booster> boosters = new ArrayList<>();
  private final List<Booster> dummyBoosters = new ArrayList<>();

  public BoosterController(GrieferGames griefergames) {
    this.griefergames = griefergames;
    boosters.add(new BreakBooster());
    boosters.add(new DropBooster());
    boosters.add(new ExperienceBooster());
    boosters.add(new FlyBooster());
    boosters.add(new MobBooster());


    dummyBoosters.add(new BreakBooster());
    dummyBoosters.add(new DropBooster());
    dummyBoosters.add(new ExperienceBooster());
    dummyBoosters.add(new FlyBooster());
    dummyBoosters.add(new MobBooster());

    for(Booster dummy : dummyBoosters) {
      dummy.setDummy(true);
    }
  }

  public void addBooster(String type, long duration) {
    for(Booster booster : this.boosters) {
      if(booster.getType().equalsIgnoreCase(type)) {
        booster.addBooster(duration);
      }
    }
  }

  public void setBooster(String type, int count, List<Long> durations) {
    for(Booster booster : this.boosters) {
      if(booster.getType().equalsIgnoreCase(type)) {
        booster.setBooster(count, durations);
      }
    }
  }

  public void removeBooster(String type) {
    for(Booster booster : this.boosters) {
      if(booster.getType().equalsIgnoreCase(type)) {
        booster.removeBooster();
      }
    }
  }

  public void resetBooster(String type) {
    for(Booster booster : this.boosters) {
      if(booster.getType().equalsIgnoreCase(type)) {
        booster.setCount(0);
        booster.getEndTimes().clear();
      }
    }
  }

  public void resetBoosters() {
    for(Booster booster : this.boosters) {
      booster.setCount(0);
      booster.getEndTimes().clear();
    }
  }

  public List<Booster> getBoosters() {
    return boosters;
  }

  public List<Booster> getDummyBoosters() {
    return dummyBoosters;
  }

  public boolean isActiveBooster() {
    for(Booster booster : this.boosters) {
      if(booster.getCount() > 0) return true;
    }
    return false;
  }
}
