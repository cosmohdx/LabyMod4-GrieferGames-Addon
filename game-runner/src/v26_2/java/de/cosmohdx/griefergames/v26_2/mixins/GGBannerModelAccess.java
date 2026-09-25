package de.cosmohdx.griefergames.v26_2.mixins;

import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.world.level.block.BannerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Accesses the two already-baked banner models without allocating any new model. */
@Mixin(BannerRenderer.class)
public interface GGBannerModelAccess {
  @Invoker("bannerModel")
  BannerModel ggaddon$bannerModel(BannerBlock.AttachmentType attachment);

  @Invoker("flagModel")
  BannerFlagModel ggaddon$flagModel(BannerBlock.AttachmentType attachment);
}
