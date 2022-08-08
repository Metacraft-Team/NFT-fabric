package net.metacraft.mod.client.gui.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class GuiVerticalScrollbar extends AbstractGuiScrollbar {

    public GuiVerticalScrollbar(GuiViewport viewport) {
        super(viewport);
        name = "GuiVerticalScrollbar";
    }

    @Override
    protected void drawAnchor(MatrixStack matrices) {
        matrices.push();
        matrices.scale(0.5f, 1, 1);
        texture.draw(matrices, getPositionX() * 2 + 3, getPositionY() + anchorPos, textureWidth, capLength, 0, 0, textureWidth, capLength);

        texture.draw(matrices, getPositionX() * 2 + 3, getPositionY() + anchorPos + capLength, textureWidth, anchorSize, 0, capLength, textureWidth, textureBodyLength);

        texture.draw(matrices, getPositionX() * 2 + 3, getPositionY() + anchorPos + capLength + anchorSize, 0, textureHeight - capLength, textureWidth, capLength);

        matrices.pop();
    }

    @Override
    protected int getTextureLength() {
        return textureHeight;
    }

    @Override
    protected int getScrollbarLength() {
        return getContentHeight();
    }

    @Override
    protected int getViewportSize() {
        return viewport.getContentHeight();
    }

    @Override
    protected int getContentSize() {
        return viewport.contentHeight;
    }

    @Override
    protected int getMousePos(int mouseX, int mouseY) {
        return mouseY - getPositionY();
    }

    @Override
    protected void updateContentPos() {
        viewport.content.setRelativePosition(viewport.content.getRelativeX(), -scrollPos);
    }

    @Override
    protected void setScrollbarWidth(int textureWidth, int textureHeight) {
        setSize(textureWidth, getContentHeight());
    }

}
