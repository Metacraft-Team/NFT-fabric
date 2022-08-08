package net.metacraft.mod.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

/**
 * An abstract base class, which implements the ITexture interface using
 * the DrawHelper.drawTexture method provided by minecraft code.
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractTexture implements ITexture {
    final Identifier texture;

    private final RenderLayer LAYER;

    public AbstractTexture(Identifier texture) {
        this.texture = texture;
        this.LAYER = RenderLayer.getText(texture);
    }

    public Identifier getTexture() {
        return texture;
    }

    public void draw(MatrixStack matrices, int x, int y) {
        draw(matrices, x, y, getWidth(), getHeight());
    }

    public void draw(MatrixStack matrices, int x, int y, int width, int height) {
        draw(matrices, x, y, width, height, 0, 0, this.getWidth(), this.getHeight());
    }

    public void draw(MatrixStack matrices, int x, int y, int u, int v, int regionWidth, int regionHeight) {
        draw(matrices, x, y, regionWidth, regionHeight, u, v, regionWidth, regionHeight);
    }

    public void draw(MatrixStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight) {

        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, this.getWidth(), this.getHeight());
    }

    public void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrices, int x, int y, int width, int height, int light) {
        drawWithLight(consumer, matrices, x, y, width, height, 0, 0, this.getWidth(), this.getHeight(), light);
    }

    public void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int light) {
        drawTexturedQuadWithLight(consumer, matrices.peek().getPositionMatrix(), x, x + width, y, y + height, (u + 0.0F) / (float) this.getWidth(), (u + (float) regionWidth) / (float) this.getWidth(), (v + 0.0F) / (float) this.getHeight(), (v + (float) regionHeight) / (float) this.getHeight(), light);
    }

    private void drawTexturedQuadWithLight(VertexConsumerProvider vertexConsumer, Matrix4f matrices, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int light) {
        VertexConsumer consumer = vertexConsumer.getBuffer(this.LAYER);
        consumer.vertex(matrices, (float) x0, (float) y1, 0f).color(255, 255, 255, 255).texture(u0, v1).light(light).next();
        consumer.vertex(matrices, (float) x1, (float) y1, 0f).color(255, 255, 255, 255).texture(u1, v1).light(light).next();
        consumer.vertex(matrices, (float) x1, (float) y0, 0f).color(255, 255, 255, 255).texture(u1, v0).light(light).next();
        consumer.vertex(matrices, (float) x0, (float) y0, 0f).color(255, 255, 255, 255).texture(u0, v0).light(light).next();
    }
}
