package net.metacraft.mod.client.gui.core;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.metacraft.mod.client.texture.ITexture;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public abstract class AbstractGuiScrollbar extends GuiBase {
    private static final int SCROLL_STEP = 18;

    private boolean isDragged = false;

    private boolean wasClicking = false;

    private boolean usesWheel = true;

    private float contentRatio = 1;

    private float scrollRatio = 0;

    protected ITexture texture;

    protected int textureWidth;

    protected int textureHeight;

    protected int capLength;

    protected int textureBodyLength;

    protected int anchorPos = 0;

    protected int anchorSize;

    protected boolean visible = false;

    protected int scrollPos = 0;

    protected final GuiViewport viewport;

    protected AbstractGuiScrollbar(GuiViewport viewport) {
        this.viewport = viewport;
        name = "AbstractGuiScrollbar";
    }

    public void setTexture(ITexture texture, int capLength) {
        this.texture = texture;
        this.textureWidth = texture.getWidth();
        this.textureHeight = texture.getHeight();
        this.capLength = capLength;
        this.textureBodyLength = getTextureLength() - capLength * 2;
        setScrollbarWidth(texture.getWidth(), texture.getHeight());
    }

    public void setUsesWheel(boolean value) {
        this.usesWheel = value;
    }

    public void updateContent() {
        this.contentRatio = (float) getViewportSize() / (float) getContentSize();
        this.visible = contentRatio < 1;
        updateAnchorSize();
        updateAnchorPos();
    }

    public void setScrollPos(int scrollPos) {
        viewport.content.validateSize();
        viewport.validateSize();
        doSetScrollPos(scrollPos);
    }

    private void doSetScrollPos(int scrollPos) {
        scrollPos = Math.max(0, Math.min(scrollPos, getContentSize() - getViewportSize()));
        this.scrollPos = scrollPos;
        scrollRatio = (float) scrollPos / (float) (getContentSize() - getViewportSize());
        updateAnchorPos();
    }

    public void setScrollRatio(float scrollRatio) {
        viewport.content.validateSize();
        viewport.validateSize();
        doSetScrollRatio(scrollRatio);
    }

    private void doSetScrollRatio(float scrollRatio) {
        if (scrollRatio < 0) scrollRatio = 0;
        if (scrollRatio > 1) scrollRatio = 1;
        this.scrollRatio = scrollRatio;
        scrollPos = Math.round(scrollRatio * (float) (getContentSize() - getViewportSize()));
        updateAnchorPos();
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double wheelMove) {
        if (usesWheel && getParent().isMouseOver) {
            if (wheelMove != 0 && this.visible) {
                wheelMove = wheelMove > 0 ? -1 : 1;
                doSetScrollPos((int) (scrollPos + wheelMove * SCROLL_STEP));
                return true;
            }
        }

        return super.mouseScrolled(mx, my, wheelMove);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isMouseOver) {
                if (!wasClicking) {
                    isDragged = true;
                }
                wasClicking = true;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (wasClicking) {
                isDragged = false;
                wasClicking = false;
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        if (!visible) {
            isDragged = false;
            return;
        }

        if (texture == null) {
            return;
        }

        if (isDragged) {
            doSetScrollRatio((float) (getMousePos(mouseX, mouseY) - anchorSize / 2)
                    / (float) (getScrollbarLength() - anchorSize));
        }

        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, texture.getTexture());
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawAnchor(matrices);
        RenderSystem.disableBlend();
    }

    private void updateAnchorSize() {
        anchorSize = Math.max(capLength * 2, Math.round(Math.min(1, contentRatio) * getScrollbarLength()));
    }

    private void updateAnchorPos() {
        anchorPos = Math.round(scrollRatio * (float) (getViewportSize() - anchorSize));
        updateContentPos();
    }

    protected abstract int getTextureLength();

    protected abstract int getScrollbarLength();

    protected abstract int getViewportSize();

    protected abstract int getContentSize();

    protected abstract int getMousePos(int mouseX, int mouseY);

    protected abstract void drawAnchor(MatrixStack matrices);

    protected abstract void updateContentPos();

    protected abstract void setScrollbarWidth(int textureWidth, int textureHeight);
}
