package de.cosmohdx.griefergames.feature.plotborder;

import java.util.List;
import net.labymod.api.Laby;
import net.labymod.api.client.gfx.pipeline.renderer.mesh.MeshRenderer;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.laby3d.pipeline.RenderStates;
import net.labymod.api.laby3d.vertex.VertexDescriptions;
import net.labymod.api.util.math.vector.DoubleVector3;
import net.labymod.laby3d.api.buffers.BufferBuilder;
import net.labymod.laby3d.api.mesh.GeometryData;
import net.labymod.laby3d.api.pipeline.ComparisonStrategy;
import net.labymod.laby3d.api.pipeline.DrawingMode;
import net.labymod.laby3d.api.pipeline.RenderState;
import net.labymod.laby3d.api.pipeline.blend.DefaultBlendFunctions;
import net.labymod.laby3d.api.pipeline.shader.ShaderProgramDescription;
import net.labymod.laby3d.api.resource.AssetId;
import org.joml.Matrix4f;

/**
 * Draws plot-border lines through the version-independent world renderer.
 */
final class PlotBorderRenderer {

  private static RenderState lines;

  private PlotBorderRenderer() {
  }

  static void draw(Stack stack, DoubleVector3 camera, List<Line3d> segments, int argb) {
    if (segments.isEmpty() || camera == null || stack == null) {
      return;
    }
    BufferBuilder builder = Laby.references().laby3D().begin(DrawingMode.LINES, VertexDescriptions.POSITION_COLOR);
    Matrix4f pose = stack.getProvider().getPose();
    double cameraX = camera.getX();
    double cameraY = camera.getY();
    double cameraZ = camera.getZ();
    for (Line3d segment : segments) {
      builder.addVertex(pose, (float) (segment.x1() - cameraX), (float) (segment.y1() - cameraY), (float) (segment.z1() - cameraZ))
          .setColor(argb);
      builder.addVertex(pose, (float) (segment.x2() - cameraX), (float) (segment.y2() - cameraY), (float) (segment.z2() - cameraZ))
          .setColor(argb);
    }
    GeometryData geometry = builder.build();
    try {
      MeshRenderer.drawImmediate(geometry, lineState());
    } finally {
      geometry.close();
    }
  }

  private static RenderState lineState() {
    RenderState current = lines;
    if (current != null) {
      return current;
    }
    AssetId programId = AssetId.of("griefergames", "plot_border_lines");
    ShaderProgramDescription program = ShaderProgramDescription.builder(RenderStates.DEFAULT_SHADER_SNIPPET)
        .setId(programId)
        .setVertexShader(RenderStates.SHADER_RESOLVER.apply("core/position_color.vsh"))
        .setFragmentShader(RenderStates.SHADER_RESOLVER.apply("core/position_color.fsh"))
        .build();
    current = RenderState.builder()
        .setId(programId)
        .setVertexDescription(VertexDescriptions.POSITION_COLOR)
        .setDrawingMode(DrawingMode.LINES)
        .setBlendFunction(DefaultBlendFunctions.TRANSLUCENT)
        .setDepthTestStrategy(ComparisonStrategy.ALWAYS)
        .setWriteDepth(false)
        .setCull(false)
        .setShaderProgramDescription(program)
        .build();
    lines = current;
    return current;
  }
}
