package net.metacraft.mod.client.gui.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class GuiHorizontalScrollbar extends AbstractGuiScrollbar {

    public GuiHorizontalScrollbar(GuiViewport viewport) {
        super(viewport);
        name = "GuiHorizontalScrollbar";
    }

    @Override
    protected void drawAnchor(MatrixStack matrices) {
        texture.draw(matrices, getPositionX() + anchorPos, getPositionY(), capLength, textureHeight, 0, 0, capLength, textureHeight);

        texture.draw(matrices, getPositionX() + anchorPos + capLength, getPositionY(), anchorSize, textureHeight, capLength, 0, textureBodyLength, textureHeight);

        texture.draw(matrices, getPositionX() + anchorPos + capLength + anchorSize, getPositionY(), textureWidth - capLength, 0, capLength, textureHeight);
    }

    @Override
    protected int getTextureLength() {
        return textureWidth;
    }

    @Override
    protected int getScrollbarLength() {
        return getContentWidth();
    }

    @Override
    protected int getViewportSize() {
        return viewport.getContentWidth();
    }

    @Override
    protected int getContentSize() {
        return viewport.contentWidth;
    }

    @Override
    protected int getMousePos(int mouseX, int mouseY) {
        return mouseX - getPositionX();
    }

    @Override
    protected void updateContentPos() {
        viewport.content.setRelativePosition(-scrollPos, viewport.content.getRelativeY());
    }

    @Override
    protected void setScrollbarWidth(int textureWidth, int textureHeight) {
        setSize(getContentWidth(), textureHeight);
    }

}
