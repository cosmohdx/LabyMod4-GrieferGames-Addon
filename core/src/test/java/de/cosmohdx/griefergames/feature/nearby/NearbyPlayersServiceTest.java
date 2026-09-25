package de.cosmohdx.griefergames.feature.nearby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.core.CloudRegionType;
import de.cosmohdx.griefergames.core.SubServerType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class NearbyPlayersServiceTest {

  @Test
  void coarseStepsNeverExposeTheExactDistance() {
    assertEquals("<5 m", NearbyDistance.coarse(0.0D));
    assertEquals("<5 m", NearbyDistance.coarse(4.9D));
    assertEquals("<10 m", NearbyDistance.coarse(5.0D));
    assertEquals("<10 m", NearbyDistance.coarse(9.9D));
    assertEquals("<20 m", NearbyDistance.coarse(10.0D));
    assertEquals("<20 m", NearbyDistance.coarse(19.9D));
    assertEquals("<32 m", NearbyDistance.coarse(20.0D));
    assertEquals("<32 m", NearbyDistance.coarse(32.0D));
  }

  @Test
  void radiusLimitAndSortKeepTheClosestPlayers() {
    List<NearbyPlayer> selected = NearbyPlayersService.select(query(16, 2, true), List.of(
        player("far", 15.0D, true),
        player("mid", 9.0D, true),
        player("near", 4.0D, true),
        player("edge", 16.0D, true),
        player("outside", 16.01D, true)));

    assertEquals(List.of("near", "mid"), names(selected));
    assertEquals(4.0D, selected.get(0).distance());
    assertEquals(9.0D, selected.get(1).distance());
  }

  @Test
  void equalDistancesAreSortedByName() {
    List<NearbyPlayer> selected = NearbyPlayersService.select(query(16, 8, true), List.of(
        player("Zeta", 6.0D, true),
        player("Anna", 6.0D, true)));

    assertEquals(List.of("Anna", "Zeta"), names(selected));
  }

  @Test
  void radiusIsClampedToThirtyTwo() {
    List<NearbyPlayer> selected = NearbyPlayersService.select(query(100, 8, true), List.of(
        player("inside", 32.0D, true),
        player("beyond", 32.01D, true)));

    assertEquals(List.of("inside"), names(selected));
    assertEquals(32.0D, NearbyPlayersService.clampRadius(100));
    assertEquals(4.0D, NearbyPlayersService.clampRadius(1));
  }

  @Test
  void exclusionsDropSelfInvisibleSpectatorsAndNpcs() {
    NearbyCandidate visible = player("ok", 5.0D, true);
    List<NearbyPlayer> selected = NearbyPlayersService.select(query(16, 8, true), List.of(
        copy(player("self", 1.0D, true), true, false, false, true),
        copy(player("hidden", 2.0D, true), false, true, false, true),
        copy(player("ghost", 3.0D, true), false, false, true, true),
        copy(player("npc", 4.0D, true), false, false, false, false),
        visible));

    assertEquals(List.of("ok"), names(selected));
  }

  @Test
  void lineOfSightCanBeRequiredOrIgnored() {
    NearbyCandidate behindWall = player("wall", 6.0D, false);
    NearbyCandidate visible = player("open", 8.0D, true);

    assertEquals(List.of("open"), names(NearbyPlayersService.select(query(16, 8, true), List.of(behindWall, visible))));
    assertEquals(List.of("wall", "open"), names(NearbyPlayersService.select(query(16, 8, false), List.of(behindWall, visible))));
  }

  @Test
  void duplicateIdsKeepTheCloserEntry() {
    UUID id = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    List<NearbyPlayer> selected = NearbyPlayersService.select(query(16, 8, true), List.of(
        new NearbyCandidate(id, "Far", null, 12.0D, false, false, false, true, true),
        new NearbyCandidate(id, "Near", null, 3.0D, false, false, false, true, true)));

    assertEquals(1, selected.size());
    assertEquals(3.0D, selected.get(0).distance());
  }

  @Test
  void disabledFeatureAndUnknownNetworkShowNobody() {
    NearbyCandidate nearby = player("ok", 2.0D, true);
    NearbyQuery disabled = new NearbyQuery(false, 16, 8, true, true, SubServerType.REGULAR, "nature", null);
    NearbyQuery unknown = new NearbyQuery(true, 16, 8, true, true, SubServerType.UNKNOWN, "nature", null);

    assertTrue(NearbyPlayersService.select(disabled, List.of(nearby)).isEmpty());
    assertTrue(NearbyPlayersService.select(unknown, List.of(nearby)).isEmpty());
    assertTrue(NearbyPlayersService.select(query(16, 8, true), null).isEmpty());
    assertTrue(NearbyPlayersService.select(query(16, 8, true), List.of()).isEmpty());
  }

  @Test
  void citybuildGateBlocksFarmsEventsAndMinigames() {
    NearbyCandidate nearby = player("ok", 2.0D, true);

    assertEquals(1, NearbyPlayersService.select(on(SubServerType.REGULAR, "nature", null, true), List.of(nearby)).size());
    assertEquals(1, NearbyPlayersService.select(on(SubServerType.REGULAR, "cb12", null, true), List.of(nearby)).size());
    assertEquals(1, NearbyPlayersService.select(on(SubServerType.REGULAR, "extreme", null, true), List.of(nearby)).size());
    assertTrue(NearbyPlayersService.select(on(SubServerType.REGULAR, "lava", null, true), List.of(nearby)).isEmpty());
    assertTrue(NearbyPlayersService.select(on(SubServerType.REGULAR, "wasser", null, true), List.of(nearby)).isEmpty());
    assertTrue(NearbyPlayersService.select(on(SubServerType.REGULAR, "event", null, true), List.of(nearby)).isEmpty());
    assertTrue(NearbyPlayersService.select(on(SubServerType.REGULAR, "Farmworld", null, true), List.of(nearby)).isEmpty());

    assertEquals(1, NearbyPlayersService.select(
        on(SubServerType.CLOUD, "Citybuild 1", CloudRegionType.CITYBUILD, true), List.of(nearby)).size());
    assertTrue(NearbyPlayersService.select(
        on(SubServerType.CLOUD, "Farmwelt 1", CloudRegionType.FARM, true), List.of(nearby)).isEmpty());
    assertTrue(NearbyPlayersService.select(
        on(SubServerType.CLOUD, "Event", CloudRegionType.EVENT, true), List.of(nearby)).isEmpty());
    assertTrue(NearbyPlayersService.select(
        on(SubServerType.CLOUD, "Bedwars", CloudRegionType.MINIGAME, true), List.of(nearby)).isEmpty());
    assertTrue(NearbyPlayersService.select(
        on(SubServerType.CLOUD, "Citybuild 1", null, true), List.of(nearby)).isEmpty());

    assertEquals(1, NearbyPlayersService.select(on(SubServerType.REGULAR, "lava", null, false), List.of(nearby)).size());
    assertEquals(1, NearbyPlayersService.select(
        on(SubServerType.CLOUD, "Farmwelt 1", CloudRegionType.FARM, false), List.of(nearby)).size());
  }

  @Test
  void invalidCandidatesAreSkipped() {
    assertTrue(NearbyPlayersService.select(query(16, 8, true), List.of(
        new NearbyCandidate(null, "none", null, 1.0D, false, false, false, true, true),
        new NearbyCandidate(UUID.randomUUID(), "nan", null, Double.NaN, false, false, false, true, true),
        new NearbyCandidate(UUID.randomUUID(), "negative", null, -1.0D, false, false, false, true, true))).isEmpty());

    List<NearbyPlayer> selected = NearbyPlayersService.select(query(16, 8, false), List.of(
        new NearbyCandidate(UUID.randomUUID(), null, null, 2.0D, false, false, false, true, false)));
    assertEquals(1, selected.size());
    assertEquals("", selected.get(0).name());
  }

  @Test
  void recomputeRunsAtMostEveryTenTicksAndSkipsTheWorldWhileIdle() {
    NearbyPlayersService service = new NearbyPlayersService();
    AtomicInteger reads = new AtomicInteger();
    NearbyQuery query = query(16, 8, true);
    List<NearbyCandidate> loaded = List.of(player("ok", 2.0D, true));

    service.tick(true, query, () -> {
      reads.incrementAndGet();
      return loaded;
    });
    assertEquals(1, reads.get());
    assertEquals(1, service.players().size());
    assertTrue(service.isActive());

    for (int tick = 0; tick < 9; tick++) {
      service.tick(true, query, () -> {
        reads.incrementAndGet();
        return loaded;
      });
    }
    assertEquals(1, reads.get());

    service.tick(true, query, () -> {
      reads.incrementAndGet();
      return List.of();
    });
    assertEquals(2, reads.get());
    assertTrue(service.players().isEmpty());

    service.tick(false, query, () -> {
      reads.incrementAndGet();
      return loaded;
    });
    assertEquals(2, reads.get());
    assertFalse(service.isActive());

    service.tick(true, query, () -> {
      reads.incrementAndGet();
      return loaded;
    });
    assertEquals(3, reads.get());
    assertEquals(List.of("ok"), names(service.players()));
  }

  private static NearbyQuery query(double radius, int limit, boolean lineOfSight) {
    return on(SubServerType.REGULAR, "nature", null, true, radius, limit, lineOfSight);
  }

  private static NearbyQuery on(SubServerType network, String subServer, CloudRegionType region, boolean onlyOnCitybuild) {
    return on(network, subServer, region, onlyOnCitybuild, 16, 8, true);
  }

  private static NearbyQuery on(
      SubServerType network,
      String subServer,
      CloudRegionType region,
      boolean onlyOnCitybuild,
      double radius,
      int limit,
      boolean lineOfSight) {
    return new NearbyQuery(true, radius, limit, lineOfSight, onlyOnCitybuild, network, subServer, region);
  }

  private static NearbyCandidate player(String name, double distance, boolean lineOfSight) {
    return new NearbyCandidate(UUID.randomUUID(), name, null, distance, false, false, false, true, lineOfSight);
  }

  private static NearbyCandidate copy(
      NearbyCandidate candidate,
      boolean self,
      boolean invisible,
      boolean spectator,
      boolean listed) {
    return new NearbyCandidate(
        candidate.id(),
        candidate.name(),
        candidate.displayName(),
        candidate.distance(),
        self,
        invisible,
        spectator,
        listed,
        candidate.lineOfSight());
  }

  private static List<String> names(List<NearbyPlayer> players) {
    List<String> names = new ArrayList<>();
    for (NearbyPlayer player : players) {
      names.add(player.name());
    }
    return names;
  }
}
