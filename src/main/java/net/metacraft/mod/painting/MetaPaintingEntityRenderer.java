package net.metacraft.mod.painting;

import com.google.gson.Gson;
import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.network.data.NftEntity;
import net.metacraft.mod.utils.Constants;
import net.metacraft.mod.utils.McMapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.PaintingManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MetaPaintingEntityRenderer extends EntityRenderer<MetaPaintingEntity> {

    private final MinecraftClient client = MinecraftClient.getInstance();

    private Map<String, Identifier> identifierMap = new HashMap<>();

    private Map<String, String> paintingIdMap = new HashMap<>();

    private Map<String, NativeImageBackedTexture> nativeImageBackedTextureMap = new HashMap<>();

    private final Gson gson = new Gson();

    public MetaPaintingEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    private void updateTexture(byte[] colors, NativeImageBackedTexture texture) {
        for(int i = 0; i < Constants.TEXTURE_WIDTH; ++i) {
            for(int j = 0; j < Constants.TEXTURE_HEIGHT; ++j) {
                int k = j + i * Constants.TEXTURE_HEIGHT;
                int l = colors[k] & 255;
                if (l / 4 == 0) {
                    texture.getImage().setColor(j, i, 0);
                } else {
                    texture.getImage().setColor(j, i, McMapColor.COLORS[l / 4].getRenderColor(l & 3));
                }
            }
        }
        texture.upload();
    }

    @Override
    public void render(MetaPaintingEntity paintingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (paintingEntity == null || paintingEntity.getImageInfoJson() == null) {
            return;
        }

        matrixStack.push();
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - f));
        PaintingMotive paintingMotive = paintingEntity.motive;
        if (paintingMotive == null) {
            return;
        }
        matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(this.getTexture(paintingEntity)));
        PaintingManager paintingManager = MinecraftClient.getInstance().getPaintingManager();
        this.renderPainting(matrixStack, vertexConsumer, paintingEntity, paintingMotive.getWidth(), paintingMotive.getHeight(), paintingManager.getBackSprite(), paintingManager.getBackSprite());
        matrixStack.pop();

        matrixStack.push();
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
        Direction direction = paintingEntity.getHorizontalFacing();

        if (direction == Direction.EAST || direction == Direction.WEST) {
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90 * (paintingEntity.getHorizontalFacing().getHorizontal())));
        }
        if (direction == Direction.SOUTH) {
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-180));
        } else if (direction == Direction.NORTH) {
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180 * (paintingEntity.getHorizontalFacing().getHorizontal())));
        }
        float factor = 1 / 132f;
        float weightScale = (paintingMotive.getWidth() / 16f);
        float heightScale = (paintingMotive.getHeight() / 16f);
        matrixStack.translate(-0.5 * weightScale * (factor) * 128, -0.5 * heightScale * (factor) * 128, -0.04);
        matrixStack.scale(factor * weightScale, factor * heightScale, factor * weightScale);
        int light = 15728850;

        // painting image
        NftEntity nftEntity = gson.fromJson(paintingEntity.getImageInfoJson(), NftEntity.class);
        byte[] colors = null;
        if (nftEntity != null && DataHandler.INSTANCE.getImagesColorMap().containsKey(nftEntity.getImageUrl())) {
            colors = DataHandler.INSTANCE.getImagesColorMap().get(nftEntity.getImageUrl()).getColors();
        }
        if (colors != null) {
            String key = nftEntity.getImageUrl();
            final String uuid = paintingIdMap.computeIfAbsent(key, k -> UUID.randomUUID().toString());
            NativeImageBackedTexture texture = nativeImageBackedTextureMap.computeIfAbsent(key, k -> new NativeImageBackedTexture(Constants.TEXTURE_WIDTH, Constants.TEXTURE_HEIGHT, true));
            Identifier identifier = identifierMap.computeIfAbsent(key,
                    address -> client.getTextureManager().registerDynamicTexture("painting/" + uuid, texture));
            RenderLayer renderLayer = RenderLayer.getText(identifier);
            updateTexture(colors, texture);
            paintingTexture(matrixStack, vertexConsumerProvider, light, renderLayer);
        }
        matrixStack.pop();
        super.render(paintingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private void paintingTexture(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, RenderLayer renderLayer) {
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
        vertexConsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(light).next();
        vertexConsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(light).next();
        vertexConsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(light).next();
        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(light).next();
    }

    public Identifier getTexture(MetaPaintingEntity paintingEntity) {
        return MinecraftClient.getInstance().getPaintingManager().getBackSprite().getAtlas().getId();
    }

    private void renderPainting(MatrixStack matrices, VertexConsumer vertexConsumer, MetaPaintingEntity entity, int width, int height, Sprite paintingSprite, Sprite backSprite) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        float f = (float)(-width) / 2.0F;
        float g = (float)(-height) / 2.0F;
        float h = 0.5F;
        float i = backSprite.getMinU();
        float j = backSprite.getMaxU();
        float k = backSprite.getMinV();
        float l = backSprite.getMaxV();
        float m = backSprite.getMinU();
        float n = backSprite.getMaxU();
        float o = backSprite.getMinV();
        float p = backSprite.getFrameV(1.0D);
        float q = backSprite.getMinU();
        float r = backSprite.getFrameU(1.0D);
        float s = backSprite.getMinV();
        float t = backSprite.getMaxV();
        int u = width / 16;
        int v = height / 16;
        double d = 16.0D / (double)u;
        double e = 16.0D / (double)v;

        for(int w = 0; w < u; ++w) {
            for(int x = 0; x < v; ++x) {
                float y = f + (float)((w + 1) * 16);
                float z = f + (float)(w * 16);
                float aa = g + (float)((x + 1) * 16);
                float ab = g + (float)(x * 16);
                int ac = entity.getBlockX();
                int ad = MathHelper.floor(entity.getY() + (double)((aa + ab) / 2.0F / 16.0F));
                int ae = entity.getBlockZ();
                Direction direction = entity.getHorizontalFacing();
                if (direction == Direction.NORTH) {
                    ac = MathHelper.floor(entity.getX() + (double)((y + z) / 2.0F / 16.0F));
                }

                if (direction == Direction.WEST) {
                    ae = MathHelper.floor(entity.getZ() - (double)((y + z) / 2.0F / 16.0F));
                }

                if (direction == Direction.SOUTH) {
                    ac = MathHelper.floor(entity.getX() - (double)((y + z) / 2.0F / 16.0F));
                }

                if (direction == Direction.EAST) {
                    ae = MathHelper.floor(entity.getZ() + (double)((y + z) / 2.0F / 16.0F));
                }

                int af = WorldRenderer.getLightmapCoordinates(entity.world, new BlockPos(ac, ad, ae));
                float ag = paintingSprite.getFrameU(d * (double)(u - w));
                float ah = paintingSprite.getFrameU(d * (double)(u - (w + 1)));
                float ai = paintingSprite.getFrameV(e * (double)(v - x));
                float aj = paintingSprite.getFrameV(e * (double)(v - (x + 1)));
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, ah, ai, -0.5F, 0, 0, -1, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, ag, ai, -0.5F, 0, 0, -1, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, ag, aj, -0.5F, 0, 0, -1, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, ah, aj, -0.5F, 0, 0, -1, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, i, k, 0.5F, 0, 0, 1, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, j, k, 0.5F, 0, 0, 1, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, j, l, 0.5F, 0, 0, 1, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, i, l, 0.5F, 0, 0, 1, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, m, o, -0.5F, 0, 1, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, n, o, -0.5F, 0, 1, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, n, p, 0.5F, 0, 1, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, m, p, 0.5F, 0, 1, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, m, o, 0.5F, 0, -1, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, n, o, 0.5F, 0, -1, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, n, p, -0.5F, 0, -1, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, m, p, -0.5F, 0, -1, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, r, s, 0.5F, -1, 0, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, r, t, 0.5F, -1, 0, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, ab, q, t, -0.5F, -1, 0, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, y, aa, q, s, -0.5F, -1, 0, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, r, s, -0.5F, 1, 0, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, r, t, -0.5F, 1, 0, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, ab, q, t, 0.5F, 1, 0, 0, af);
                this.vertex(matrix4f, matrix3f, vertexConsumer, z, aa, q, s, 0.5F, 1, 0, 0, af);
            }
        }

    }

    private void vertex(Matrix4f modelMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light) {
        vertexConsumer.vertex(modelMatrix, x, y, z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, (float) normalX, (float) normalY, (float) normalZ).next();
    }
}