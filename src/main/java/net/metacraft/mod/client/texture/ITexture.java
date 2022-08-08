package net.metacraft.mod.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface ITexture {

    Identifier getTexture();

    int getWidth();

    int getHeight();

    void draw(MatrixStack matrices, int x, int y);

    void draw(MatrixStack matrices, int x, int y, int width, int height);

    void draw(MatrixStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight);

    void draw(MatrixStack matrices, int x, int y, int u, int v, int regionWidth, int regionHeight);

    void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrices, int x, int y, int width, int height, int light);

    void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int light);
}
