package de.cosmohdx.griefergames.feature.itempreview;

import de.cosmohdx.griefergames.GrieferGames;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.client.resources.CompletableResourceLocation;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.resources.texture.DynamicTexture;
import net.labymod.api.client.resources.texture.GameImage;
import net.labymod.api.client.session.MinecraftServices.SkinVariant;
import net.labymod.api.client.world.item.ItemStack;
import net.labymod.api.component.data.BuiltinDataComponents;
import net.labymod.api.component.data.DataComponentContainer;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.ScreenRenderEvent;
import net.labymod.api.event.client.world.ItemStackTooltipEvent;
import net.labymod.api.mojang.texture.MojangTextureType;
import net.labymod.api.nbt.NBTTagType;
import net.labymod.api.nbt.tags.NBTTagCompound;

public class ItemPreviewListener {

  private final GrieferGames griefergames;
  private final Map<Integer, CachedMap> maps = new HashMap<>();
  private final Set<String> requestedSkins = new HashSet<>();

  private ItemStack pending;
  private ItemStack drawing;
  private boolean tooltipThisFrame;

  public ItemPreviewListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onTooltip(ItemStackTooltipEvent event) {
    if (!this.addonEnabled() || event.itemStack() == null) {
      this.pending = null;
      return;
    }
    ItemStack stack = event.itemStack();
    ItemPreviewConfig config = this.griefergames.configuration().itemPreview();
    boolean preview = (config.showMap() && ItemPreviewKinds.isFilledMap(stack))
        || (config.showHead() && ItemPreviewKinds.isPlayerHead(stack));
    if (!preview) {
      this.pending = null;
      return;
    }
    this.pending = stack.copy();
    this.tooltipThisFrame = true;
  }

  @Subscribe
  public void onScreen(ScreenRenderEvent event) {
    if (event.phase() == Phase.PRE) {
      if (this.tooltipThisFrame) {
        this.drawing = this.pending;
      } else {
        this.pending = null;
        this.drawing = null;
      }
      this.tooltipThisFrame = false;
      return;
    }
    if (event.phase() != Phase.POST || !this.addonEnabled()) {
      return;
    }
    ItemStack stack = this.tooltipThisFrame ? this.pending : this.drawing;
    if (stack == null) {
      return;
    }
    this.render(event.screenContext(), stack);
  }

  private void render(ScreenContext context, ItemStack stack) {
    ItemPreviewConfig config = this.griefergames.configuration().itemPreview();
    if (config.showMap() && ItemPreviewKinds.isFilledMap(stack)) {
      this.renderMap(context, stack, config.mapSize());
      return;
    }
    if (config.showHead() && ItemPreviewKinds.isPlayerHead(stack)) {
      this.renderHead(context, stack, config.headSize());
    }
  }

  private void renderMap(ScreenContext context, ItemStack stack, int size) {
    int mapId = this.mapId(stack);
    if (mapId < 0 || this.griefergames.controller() == null) {
      return;
    }
    int[] pixels = this.griefergames.controller().filledMapPixels(mapId);
    if (pixels == null || pixels.length < MapPixels.AREA) {
      return;
    }
    ResourceLocation location = this.texture(mapId, pixels);
    if (location == null) {
      return;
    }
    int x = TooltipPreviewPlacement.x(context.mouse().getX(), size, this.screenWidth());
    int y = TooltipPreviewPlacement.y(context.mouse().getY(), size, this.screenHeight());
    Stack pose = context.stack();
    Laby.labyAPI().renderPipeline().rectangleRenderer()
        .renderOutline(pose, x - 1, y - 1, size + 2, size + 2, 0xFF000000, 1);
    Laby.labyAPI().renderPipeline().resourceRenderer()
        .texture(location)
        .pos(x, y)
        .size(size, size)
        .render(pose);
  }

  private void renderHead(ScreenContext context, ItemStack stack, int size) {
    HeadTexture texture = HeadTextures.fromStack(stack);
    if (texture == null || !texture.canRender()) {
      return;
    }
    CompletableResourceLocation location = this.skin(texture);
    if (location == null || !location.hasResult() || location.isLoading() || location.getCompleted() == null) {
      return;
    }
    int x = TooltipPreviewPlacement.x(context.mouse().getX(), size, this.screenWidth());
    int y = TooltipPreviewPlacement.y(context.mouse().getY(), size, this.screenHeight());
    Laby.labyAPI().renderPipeline().resourceRenderer().head()
        .player(location.getCompleted())
        .wearingHat(true)
        .pos(x, y)
        .size(size, size)
        .render(context.stack());
  }

  private CompletableResourceLocation skin(HeadTexture texture) {
    var service = Laby.references().mojangTextureService();
    if (texture.url() != null) {
      UUID textureId = texture.textureId();
      if (textureId != null && this.requestedSkins.add(texture.url())) {
        service.applySkinTexture(
            textureId,
            texture.slim() ? SkinVariant.SLIM : SkinVariant.CLASSIC,
            texture.url()
        );
      }
      if (textureId == null) {
        return null;
      }
      return service.getTexture(textureId, MojangTextureType.SKIN);
    }
    if (texture.uuid() != null) {
      return service.getTexture(texture.uuid(), MojangTextureType.SKIN);
    }
    if (texture.name() != null) {
      return service.getTexture(texture.name(), MojangTextureType.SKIN);
    }
    return null;
  }

  private ResourceLocation texture(int mapId, int[] pixels) {
    int hash = Arrays.hashCode(pixels);
    CachedMap cached = this.maps.get(mapId);
    if (cached != null && cached.hash == hash) {
      return cached.location;
    }
    GameImage image = GameImage.IMAGE_PROVIDER.createImage(MapPixels.SIZE, MapPixels.SIZE);
    for (int y = 0; y < MapPixels.SIZE; y++) {
      for (int x = 0; x < MapPixels.SIZE; x++) {
        image.setARGB(x, y, pixels[x + y * MapPixels.SIZE]);
      }
    }
    ResourceLocation location = cached == null
        ? ResourceLocation.create("griefergames", "itempreview/map_" + mapId)
        : cached.location;
    if (cached == null) {
      DynamicTexture texture = new DynamicTexture(location, image);
      texture.upload();
      Laby.references().textureRepository().register(location, texture);
      this.maps.put(mapId, new CachedMap(hash, location));
      return location;
    }
    DynamicTexture existing = Laby.references().textureRepository().getTexture(location) instanceof DynamicTexture dynamic
        ? dynamic
        : null;
    if (existing != null) {
      existing.setImageAndUpload(image);
    } else {
      DynamicTexture texture = new DynamicTexture(location, image);
      texture.upload();
      Laby.references().textureRepository().register(location, texture);
    }
    cached.hash = hash;
    return location;
  }

  private int mapId(ItemStack stack) {
    boolean nbtPresent = false;
    int nbtMap = -1;
    if (stack.hasNBTTag()) {
      NBTTagCompound tag = stack.getNBTTag();
      if (tag != null && tag.contains("map", NBTTagType.ANY_NUMERIC)) {
        nbtPresent = true;
        nbtMap = tag.getInt("map");
      }
    }
    Object component = null;
    if (stack.hasDataComponentContainer()) {
      DataComponentContainer container = stack.getDataComponentContainer();
      if (container != null && container.has(BuiltinDataComponents.MAP_ID)) {
        component = container.get(BuiltinDataComponents.MAP_ID);
      }
    }
    return FilledMapIds.resolve(
        Laby.labyAPI().minecraft().getProtocolVersion(),
        stack.getLegacyItemData(),
        nbtPresent,
        nbtMap,
        component
    );
  }

  private boolean addonEnabled() {
    return this.griefergames.configuration().enabled().get()
        && this.griefergames.configuration().itemPreview().isEnabled();
  }

  private int screenWidth() {
    return Laby.labyAPI().minecraft().minecraftWindow().getScaledWidth();
  }

  private int screenHeight() {
    return Laby.labyAPI().minecraft().minecraftWindow().getScaledHeight();
  }

  private static final class CachedMap {
    private int hash;
    private final ResourceLocation location;

    private CachedMap(int hash, ResourceLocation location) {
      this.hash = hash;
      this.location = location;
    }
  }
}
