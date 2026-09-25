package de.cosmohdx.griefergames.v1_8_9;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public final class LegacyItemGlint {

  private static final ResourceLocation GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

  private LegacyItemGlint() {
  }

  public static void drawCube() {
    GlStateManager.depthMask(false);
    GlStateManager.depthFunc(514);
    GlStateManager.disableLighting();
    GlStateManager.blendFunc(768, 1);
    Minecraft.getMinecraft().getTextureManager().bindTexture(GLINT);
    GlStateManager.matrixMode(5890);
    pass(3000L, 3000.0F, false, -50.0F);
    pass(4873L, 4873.0F, true, 10.0F);
    GlStateManager.matrixMode(5888);
    GlStateManager.blendFunc(770, 771);
    GlStateManager.enableLighting();
    GlStateManager.depthFunc(515);
    GlStateManager.depthMask(true);
  }

  private static void pass(long period, float divisor, boolean negative, float rotation) {
    GlStateManager.pushMatrix();
    GlStateManager.scale(8.0F, 8.0F, 8.0F);
    float offset = (float) (Minecraft.getSystemTime() % period) / divisor / 8.0F;
    GlStateManager.translate(negative ? -offset : offset, 0.0F, 0.0F);
    GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
    GlStateManager.color(0.5F, 0.25F, 0.8F, 1.0F);
    cube();
    GlStateManager.popMatrix();
  }

  private static void cube() {
    net.minecraft.client.renderer.WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
    renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    face(renderer, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1);
    face(renderer, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0);
    face(renderer, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0);
    face(renderer, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1);
    face(renderer, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0);
    face(renderer, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1);
    Tessellator.getInstance().draw();
  }

  private static void face(net.minecraft.client.renderer.WorldRenderer renderer, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4) {
    renderer.pos(x1, y1, z1).tex(0.0D, 0.0D).endVertex();
    renderer.pos(x2, y2, z2).tex(0.0D, 1.0D).endVertex();
    renderer.pos(x3, y3, z3).tex(1.0D, 1.0D).endVertex();
    renderer.pos(x4, y4, z4).tex(1.0D, 0.0D).endVertex();
  }
}
