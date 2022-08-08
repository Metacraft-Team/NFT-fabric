package net.metacraft.mod.client.gui.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.metacraft.mod.client.texture.ITexture;
import net.metacraft.mod.client.texture.Textures;
import net.metacraft.mod.utils.Constants;
import net.metacraft.mod.utils.McMapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageBase extends GuiBase {
    protected static final int INTERVAL_FRAME = 15;

    private NativeImageBackedTexture texture;

    protected Identifier identifier;

    protected volatile byte[] colors;

    protected volatile int count;

    protected int backgroundWidth;

    protected int backgroundHeight;

    protected int deltaX, deltaY;

    protected int actualWidth;

    protected int actualHeight;

    protected List<Text> tips = new ArrayList<>();

    public ImageBase(int x, int y, int width, int height) {
        backgroundWidth = width;
        backgroundHeight = height;
        setSize(width, height);
        setGuiPosition(x, y);
        texture = new NativeImageBackedTexture(Constants.TEXTURE_WIDTH, Constants.TEXTURE_HEIGHT, true);
        identifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("painting/" + new Random().nextInt(Integer.MAX_VALUE), this.texture);
        name = "ImageBase";
    }

    protected void updateTexture(byte[] colors) {
        if (colors == null) {
            return;
        }
        for (int i = 0; i < Constants.TEXTURE_WIDTH; ++i) {
            for (int j = 0; j < Constants.TEXTURE_HEIGHT; ++j) {
                int k = j + i * Constants.TEXTURE_HEIGHT;
                int l = colors[k] & 255;
                if (l / 4 == 0) {
                    this.texture.getImage().setColor(j, i, 0);
                } else {
                    this.texture.getImage().setColor(j, i, McMapColor.COLORS[l / 4].getRenderColor(l & 3));
                }
            }
        }
        this.texture.upload();
    }

    public void clear() {
        colors = null;
    }

    @Override
    public void init() {

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        super.render(matrices, mouseX, mouseY, partialTick);
        if (colors == null) {
            return;
        }

        fillGradient(matrices, getPositionX(), getPositionY(), getPositionX() + backgroundWidth,
                getPositionY() + backgroundHeight, 0x88101010, 0x99101010);

        // 每10帧刷一次
        if (count % INTERVAL_FRAME == 0) {
            updateTexture(colors);
            count = count++ % INTERVAL_FRAME;
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, identifier);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(
                GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
        );
        if (isMouseOver) {
            drawTooltip(tips);
        }
        drawTexture(matrices, getPositionX() + deltaX, getPositionY() + deltaY, 0, 0, actualWidth, actualHeight, actualWidth, actualHeight);
    }

    public int getBackgroundWidth() {
        return backgroundWidth;
    }

    public void setBackgroundWidth(int backgroundWidth) {
        this.backgroundWidth = backgroundWidth;
    }

    public int getBackgroundHeight() {
        return backgroundHeight;
    }

    public void setBackgroundHeight(int backgroundHeight) {
        this.backgroundHeight = backgroundHeight;
    }
}
