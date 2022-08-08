package net.metacraft.mod.client.gui.core;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;

public class GuiButton extends GuiBase{

    private Runnable buttonClick;

    private int x, y;

    private int btnWidth, btnHeight;

    private boolean wasClicking;

    private String text = "";

    public GuiButton(int x, int y, int width, int height, String text, Runnable buttonClick) {
        setGuiPosition(x, y);
        setSize(width, height);
        this.x = x;
        this.y = y;
        this.btnWidth = width;
        this.btnHeight = height;
        this.buttonClick = buttonClick;
        this.text = text;
        name = "GuiButton";
    }

    @Override
    public void init() {
        addDrawable(new ButtonWidget(x, y, btnWidth, btnHeight, new TranslatableText(text), buttonWidget -> {}));
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
                buttonClick.run();
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
