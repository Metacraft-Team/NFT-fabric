package net.metacraft.mod.client.gui.core;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class GuiViewport extends GuiBase {
    public final GuiBase content = new GuiBase();

    private double screenScale;

    public GuiViewport() {
        this.addChild(content);
        name = "GuiViewport";
        content.name = "GuiViewport/content";
    }

    public GuiBase addContent(GuiBase child) {
        return content.addChild(child);
    }

    public GuiBase removeContent(GuiBase child) {
        return content.removeChild(child);
    }

    public void removeAllContent() {
        content.removeAllChildren();
    }

    @Override
    public void init() {
        super.init();
        screenScale = client.getWindow().getScaleFactor();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float par3) {
        int renderWidth = properWidth;
        int renderHeight = properHeight + 2;

        RenderSystem.enableScissor((int) ((getPositionX() - 1) * screenScale),
                (int) (MinecraftClient.getInstance().getWindow().getFramebufferHeight() - (getPositionY() + renderHeight) * screenScale),
                (int) (renderWidth * screenScale), (int) (renderHeight * screenScale));

        super.render(matrices, mouseX, mouseY, par3);

        RenderSystem.disableScissor();
    }

    @Override
    public int getContentWidth() {
        return properWidth;
    }

    @Override
    public int getContentHeight() {
        return properHeight;
    }

    @Override
    protected void validateSize() {
        super.validateSize();
        // Update the clipping flag on content's child components:
        for (GuiBase child : this.getChildren()) {
            if (child.getPositionY() > getPositionY() + properHeight ||
                    child.getPositionY() + child.getContentHeight() < getPositionY() ||
                    child.getPositionX() > getPositionX() + properWidth ||
                    child.getPositionX() + child.getContentWidth() < getPositionX()) {
                child.setClipped(true);
            } else {
                child.setClipped(false);
            }
        }
    }
}
