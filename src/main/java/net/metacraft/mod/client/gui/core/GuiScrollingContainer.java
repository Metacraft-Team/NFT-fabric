package net.metacraft.mod.client.gui.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.metacraft.mod.client.texture.Textures;

@Environment(EnvType.CLIENT)
public class GuiScrollingContainer extends GuiBase {
    private static final int ITEM_SPACING = 4;

    private static final int VIEWPORT_MAX_WIDTH = 240;

    private static final int VIEWPORT_HEIGHT = 70;

    private final GuiViewport viewport;

    private final GuiHorizontalScrollbar scrollbarHor;

    private final GuiVerticalScrollbar scrollbarVer;

    private int containerWidth;

    private boolean isShowScrollbar;

    private boolean isHorizontal;

    public GuiScrollingContainer(boolean isHorizontal, boolean isShowScrollbar) {
        name = "GuiScrollingContainer";
        this.isShowScrollbar = isShowScrollbar;
        this.isHorizontal = isHorizontal;
        viewport = new GuiViewport();
        this.addChild(viewport);
        scrollbarHor = new GuiHorizontalScrollbar(viewport);
        scrollbarVer = new GuiVerticalScrollbar(viewport);
        if (isShowScrollbar) {
            scrollbarHor.setTexture(Textures.SCROLLBAR_HOR, 2);
            scrollbarVer.setTexture(Textures.SCROLLBAR_VER, 2);
        }
        this.addChild(scrollbarHor);
        this.addChild(scrollbarVer);
        setWheelScrollsGravity();
    }

    public synchronized void addContent(GuiBase child) {
        viewport.addContent(child);
    }

    public synchronized void refreshContent() {
        int relative = 0;
        for (GuiBase child : viewport.content.getChildren()) {
            if (child.getContentWidth() == 0) {
                continue;
            }
            child.setRelativeX(relative);
            relative += child.getContentWidth() + ITEM_SPACING;
        }
        int viewportWidth = Math.min(relative, VIEWPORT_MAX_WIDTH);
        setViewportSize(viewportWidth, VIEWPORT_HEIGHT);
    }

    public synchronized void refreshVerticallyContent(int viewportWidth, int viewportMaxHeight) {
        int relative = 1;
        for (GuiBase child : viewport.content.getChildren()) {
            if (child.getContentHeight() == 0) {
                continue;
            }
            child.setRelativeY(relative);
            relative += child.getContentHeight() + ITEM_SPACING;
        }
        int viewportHeight = Math.min(relative, viewportMaxHeight);
        setViewportSize(viewportWidth, viewportHeight);
    }

    public GuiBase removeContent(GuiBase child) {
        return viewport.removeContent(child);
    }

    public void removeAllContent() {
        viewport.removeAllContent();
    }

    public void setViewportSize(int width, int height) {
        viewport.setSize(width, height);
        if (isShowScrollbar) {
            scrollbarVer.setRelativePosition(37, 0);
            scrollbarVer.setSize(scrollbarVer.getContentWidth(), height);
//            scrollbarHor.setRelativePosition(0, height);
//            scrollbarHor.setSize(width, scrollbarHor.getContentHeight());
        }

    }

    @Override
    protected void validateSize() {
        super.validateSize();
        scrollbarHor.updateContent();
        scrollbarVer.updateContent();
    }

    private void setWheelScrollsGravity() {
        scrollbarHor.setUsesWheel(isHorizontal);
        scrollbarVer.setUsesWheel(!isHorizontal);
    }

    public void scrollTo(int x, int y) {
        scrollbarHor.setScrollPos(x);
        scrollbarVer.setScrollPos(y);
    }

    public int getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(int containerWidth) {
        this.containerWidth = containerWidth;
    }

    @Override
    public int getRelativeX() {
        return viewport.getRelativeX();
    }

    @Override
    public int getContentWidth() {
        return super.getContentWidth();
    }

    @Override
    public int getContentHeight() {
        return super.getContentHeight() - (scrollbarHor.visible ? 0 : scrollbarHor.getContentHeight());
    }

    public GuiViewport getViewport() {
        return viewport;
    }
}
