package net.metacraft.mod.client.gui.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.client.texture.ITexture;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Collections;

public class GuiArrowButton extends GuiBase {
    private ITexture up, down;

    private Runnable callback;

    private int buttonWidth, buttonHeight;

    private boolean wasClicking;

    public GuiArrowButton(int x, int y, ITexture up, ITexture down, Runnable callback) {
        setGuiPosition(x, y);
        setSize(up.getWidth(), up.getHeight());
        this.up = up;
        this.down = down;
        this.callback = callback;
        this.buttonWidth = up.getWidth();
        this.buttonHeight = up.getHeight();
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
                callback.run();
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        super.render(matrices, mouseX, mouseY, partialTick);
        Identifier identifier = wasClicking ? down.getTexture() : up.getTexture();
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
            int currentPage = DataHandler.INSTANCE.getCurrentPage();
            int totalPage = DataHandler.INSTANCE.getTotalPage();
            if (totalPage != 0) {
                drawTooltip(Collections.singletonList(new TranslatableText(currentPage + "/" + totalPage)));
            }
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else  {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.7F);
        }
        drawTexture(matrices, getPositionX(), getPositionY(), 0, 0, buttonWidth, buttonHeight, buttonWidth, buttonHeight);
        RenderSystem.disableBlend();
    }
}
