package de.cosmohdx.griefergames.feature.plotborder;

import java.util.List;
import net.labymod.api.Textures;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.laby3d.pipeline.RenderStates;
import net.labymod.api.laby3d.pipeline.material.LevelMaterial;
import net.labymod.api.laby3d.pipeline.material.Material;
import net.labymod.api.laby3d.render.queue.SubmissionCollector;
import net.labymod.laby3d.api.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Submits plot-border lines through the level snapshot pipeline ({@link SubmissionCollector}),
 * matching how LabyMod draws world debug overlays.
 */
final class PlotBorderRenderer {

  private static final float HALF_THICKNESS = 0.05F;

  private PlotBorderRenderer() {
  }

  static void submit(SubmissionCollector collector, Stack stack, PlotBorderSnapshot snapshot) {
    if (snapshot.lines().isEmpty()) {
      return;
    }
    Material material = LevelMaterial.builder(RenderStates.DEBUG_BOXES)
        .setTexture(0, Textures.WHITE)
        .build();
    collector.order(1000).submitCustomGeometry(
        stack,
        material,
        (pose, consumer) -> drawLines(consumer, pose, snapshot));
  }

  private static void drawLines(VertexConsumer consumer, Matrix4f pose, PlotBorderSnapshot snapshot) {
    float camX = (float) snapshot.camX();
    float camY = (float) snapshot.camY();
    float camZ = (float) snapshot.camZ();
    int color = snapshot.colorArgb();
    for (Line3d segment : snapshot.lines()) {
      addSegmentQuad(consumer, pose, camX, camY, camZ, segment, color);
    }
  }

  private static void addSegmentQuad(
      VertexConsumer consumer,
      Matrix4f pose,
      float camX,
      float camY,
      float camZ,
      Line3d segment,
      int argb
  ) {
    float x1 = (float) segment.x1() - camX;
    float y1 = (float) segment.y1() - camY;
    float z1 = (float) segment.z1() - camZ;
    float x2 = (float) segment.x2() - camX;
    float y2 = (float) segment.y2() - camY;
    float z2 = (float) segment.z2() - camZ;

    Vector3f dir = new Vector3f(x2 - x1, y2 - y1, z2 - z1);
    if (dir.lengthSquared() < 1.0E-8F) {
      return;
    }
    dir.normalize();

    Vector3f toCam = new Vector3f(-x1, -y1, -z1);
    if (toCam.lengthSquared() < 1.0E-8F) {
      toCam.set(0.0F, 1.0F, 0.0F);
    } else {
      toCam.normalize();
    }
    Vector3f side = dir.cross(toCam, new Vector3f());
    if (side.lengthSquared() < 1.0E-8F) {
      side = dir.cross(new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f());
      if (side.lengthSquared() < 1.0E-8F) {
        side = dir.cross(new Vector3f(1.0F, 0.0F, 0.0F), new Vector3f());
      }
    }
    side.normalize().mul(HALF_THICKNESS);

    vertex(consumer, pose, x1 - side.x, y1 - side.y, z1 - side.z, argb);
    vertex(consumer, pose, x1 + side.x, y1 + side.y, z1 + side.z, argb);
    vertex(consumer, pose, x2 + side.x, y2 + side.y, z2 + side.z, argb);
    vertex(consumer, pose, x2 - side.x, y2 - side.y, z2 - side.z, argb);
  }

  private static void vertex(
      VertexConsumer consumer,
      Matrix4f pose,
      float x,
      float y,
      float z,
      int argb
  ) {
    consumer.addVertex(pose, x, y, z).setColor(argb);
  }
}
