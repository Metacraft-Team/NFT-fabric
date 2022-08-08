package net.metacraft.mod.client.gui.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.metacraft.mod.client.texture.ITexture;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class GuiImageButton extends GuiBase {
    private ITexture texture;

    private Clickable callback;

    private int buttonWidth, buttonHeight;

    private boolean wasClicking;

    public GuiImageButton(int x, int y, ITexture texture, Clickable callback) {
        this(x, y, texture.getWidth(), texture.getHeight(), texture, callback);
    }

    public GuiImageButton(int x, int y, int buttonWidth, int buttonHeight, ITexture texture, Clickable callback) {
        setGuiPosition(x, y);
        setSize(buttonWidth, buttonWidth);
        this.callback = callback;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        this.texture = texture;
        name = "GuiArrowButton";
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver) {
            wasClicking = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (wasClicking) {
                wasClicking = false;
                if (callback != null) {
                    callback.onClick(this);
                }
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        super.render(matrices, mouseX, mouseY, partialTick);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture.getTexture());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(
                GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
        );
        drawTexture(matrices, getPositionX(), getPositionY(), 0, 0, buttonWidth, buttonHeight, buttonWidth, buttonHeight);
    }

    public void setTexture(ITexture texture) {
        this.texture = texture;
    }

    public ITexture getTexture() {
        return texture;
    }

    public interface Clickable {
        void onClick(GuiImageButton imageButton);
    }
}
