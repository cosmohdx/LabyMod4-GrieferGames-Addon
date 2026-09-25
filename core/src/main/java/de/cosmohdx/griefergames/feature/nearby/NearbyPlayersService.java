package de.cosmohdx.griefergames.feature.nearby;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Filters, sorts and limits loaded players. The world is read at most every
 * {@link #RECOMPUTE_INTERVAL_TICKS} ticks.
 */
public final class NearbyPlayersService {

  public static final int RECOMPUTE_INTERVAL_TICKS = 10;
  public static final int MIN_RADIUS = 4;
  public static final int MAX_RADIUS = 32;
  public static final int MIN_LIMIT = 1;
  public static final int MAX_LIMIT = 20;

  private List<NearbyPlayer> players = List.of();
  private int tickCounter;
  private boolean active;

  public List<NearbyPlayer> players() {
    return this.players;
  }

  public boolean isActive() {
    return this.active;
  }

  public void clear() {
    this.players = List.of();
    this.active = false;
    this.tickCounter = 0;
  }

  /**
   * @param loader read only on a recompute tick, so a hidden list does not scan the world
   */
  public void tick(boolean active, NearbyQuery query, Supplier<List<NearbyCandidate>> loader) {
    if (!active) {
      clear();
      return;
    }
    this.active = true;
    if (this.tickCounter % RECOMPUTE_INTERVAL_TICKS != 0) {
      this.tickCounter++;
      return;
    }
    this.tickCounter++;
    List<NearbyCandidate> loaded = loader == null ? List.of() : loader.get();
    this.players = List.copyOf(select(query, loaded));
  }

  public static double clampRadius(double radius) {
    if (Double.isNaN(radius)) {
      return MIN_RADIUS;
    }
    return Math.max(MIN_RADIUS, Math.min(MAX_RADIUS, radius));
  }

  public static int clampLimit(int limit) {
    return Math.max(MIN_LIMIT, Math.min(MAX_LIMIT, limit));
  }

  public static List<NearbyPlayer> select(NearbyQuery query, List<NearbyCandidate> loaded) {
    if (query == null || !query.enabled() || loaded == null || loaded.isEmpty()) {
      return List.of();
    }
    if (!NearbyServerGate.allows(query.onlyOnCitybuild(), query.network(), query.subServer(), query.cloudRegion())) {
      return List.of();
    }
    double radius = clampRadius(query.radius());
    int limit = clampLimit(query.limit());
    List<NearbyCandidate> matched = new ArrayList<>();
    for (NearbyCandidate candidate : loaded) {
      if (!included(query, candidate, radius)) {
        continue;
      }
      matched.add(candidate);
    }
    matched.sort(Comparator
        .comparingDouble(NearbyCandidate::distance)
        .thenComparing(candidate -> candidate.name() == null ? "" : candidate.name()));
    List<NearbyPlayer> result = new ArrayList<>();
    Set<UUID> seen = new HashSet<>();
    for (NearbyCandidate candidate : matched) {
      if (!seen.add(candidate.id())) {
        continue;
      }
      String name = candidate.name() == null ? "" : candidate.name();
      result.add(new NearbyPlayer(candidate.id(), name, candidate.displayName(), candidate.distance()));
      if (result.size() >= limit) {
        break;
      }
    }
    return List.copyOf(result);
  }

  private static boolean included(NearbyQuery query, NearbyCandidate candidate, double radius) {
    if (candidate == null || candidate.id() == null) {
      return false;
    }
    if (candidate.self() || candidate.invisible() || candidate.spectator() || !candidate.listed()) {
      return false;
    }
    if (query.onlyLineOfSight() && !candidate.lineOfSight()) {
      return false;
    }
    if (Double.isNaN(candidate.distance()) || candidate.distance() < 0.0D || candidate.distance() > radius) {
      return false;
    }
    return true;
  }
}
