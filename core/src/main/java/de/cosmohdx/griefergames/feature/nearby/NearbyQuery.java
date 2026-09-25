package de.cosmohdx.griefergames.feature.nearby;

import de.cosmohdx.griefergames.core.CloudRegionType;
import de.cosmohdx.griefergames.core.SubServerType;
import org.jetbrains.annotations.Nullable;

public record NearbyQuery(
    boolean enabled,
    double radius,
    int limit,
    boolean onlyLineOfSight,
    boolean onlyOnCitybuild,
    @Nullable SubServerType network,
    @Nullable String subServer,
    @Nullable CloudRegionType cloudRegion) {
}
