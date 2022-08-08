package net.metacraft.mod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.client.gui.core.*;
import net.metacraft.mod.client.texture.ITexture;
import net.metacraft.mod.client.texture.Textures;
import net.metacraft.mod.network.core.HttpsUtils;
import net.metacraft.mod.network.data.NftEntity;
import net.metacraft.mod.utils.Constants;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;

public class ImagesRightPanel extends GuiBase {
    private static List<ImageBase> rightNftImages = new ArrayList<>();

    private static final float INPUT_TEXT_SCALE = 0.7f;

    public ImagesRightPanel() {
        name = "ImagesRightPanel";
    }

    private TextFieldWidget textField;

    private ITexture searchTexture = Textures.SEARCH_BG;

    private ITexture searchIcoTexture = Textures.SEARCH_ICON;

    @Override
    public void init() {
        rightNftImages = new ArrayList<>();
        setGuiPosition((width - Constants.RIGHT_PANEL_WIDTH -
                Constants.LEFT_COLLECTION_WIDTH - Constants.LEFT_RIGHT_SPACING) / 2
                + Constants.LEFT_COLLECTION_WIDTH
                + Constants.LEFT_RIGHT_SPACING, (height - Constants.PANEL_HEIGHT) / 2);
        setSize(Constants.RIGHT_PANEL_WIDTH, Constants.PANEL_HEIGHT);

        int imageWidth = 72;
        int imageHeight = 72;

        int imageWidthDelta = (Constants.RIGHT_PANEL_WIDTH - 4 * imageWidth) / 5;
        int imageHeightDelta = 30;
        int currentX = imageWidthDelta + getPositionX();

        textField = new TextFieldWidget(textRenderer, (int) ((getPositionX() + 32) / INPUT_TEXT_SCALE),
                (int) ((getPositionY() + 15) / INPUT_TEXT_SCALE), 190, 20,
                new TranslatableText(""));
        textField.setDrawsBackground(false);
        textField.setEditable(true);
        textField.setMaxLength(256);

        for (int i = 0; i < 4; i++) {
            ImageBase upChild = new NftImage(currentX, getPositionY() + imageHeightDelta, imageWidth, imageHeight);
            this.addChildWithoutFixPos(upChild);
            rightNftImages.add(upChild);
            currentX = currentX + imageWidthDelta + imageWidth;
        }
        currentX = imageWidthDelta + getPositionX();
        for (int i = 0; i < 4; i++) {
            ImageBase downChild = new NftImage(currentX, getPositionY() + imageHeightDelta + imageHeight + 24,
                    imageWidth, imageHeight);
            this.addChildWithoutFixPos(downChild);
            rightNftImages.add(downChild);
            currentX = currentX + imageWidthDelta + imageWidth;
        }

        currentX = currentX - imageWidth + imageWidthDelta * 2;
        this.addChildWithoutFixPos(new GuiArrowButton(currentX + 21, getPositionY() + 222, Textures.BTN_PRE_UP, Textures.BTN_PRE_DOWN, () -> {
            int currentPage = DataHandler.INSTANCE.getCurrentPage();
            if (currentPage == 1) {
                return;
            }
            DataHandler.INSTANCE.getNftEntities().clear();
            clearPanelImages();
            currentPage--;
            DataHandler.INSTANCE.setCurrentPage(currentPage);
            HttpsUtils.queryNftImages(currentPage, null, ImagesRightPanel::loadRightPanel);
        }));

        this.addChildWithoutFixPos(new GuiArrowButton(currentX, getPositionY() + 222, Textures.BTN_NEXT_UP, Textures.BTN_NEXT_DOWN, () -> {
            int currentPage = DataHandler.INSTANCE.getCurrentPage();
            if (currentPage == DataHandler.INSTANCE.getTotalPage()) {
                return;
            }
            DataHandler.INSTANCE.getNftEntities().clear();
            clearPanelImages();
            currentPage++;
            DataHandler.INSTANCE.setCurrentPage(currentPage);
            HttpsUtils.queryNftImages(currentPage, null, ImagesRightPanel::loadRightPanel);
        }));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) || textField.mouseClicked(mouseX / INPUT_TEXT_SCALE, mouseY / INPUT_TEXT_SCALE, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 && textField.isFocused()) {
            textField.changeFocus(false);
            ImagesRightPanel.clearRightPanel();
            DataHandler.INSTANCE.setCurrentCollection("");
            DataHandler.INSTANCE.setCurrentPage(1);
            HttpsUtils.queryNftImages(1, textField.getText(), ImagesRightPanel::loadRightPanel);
            CollectionImage historySelected = DataHandler.INSTANCE.getCollectionImage();
            if (historySelected != null) {
                historySelected.setSelected(false);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers) || textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char aa, int bb) {
        return super.charTyped(aa, bb) || textField.charTyped(aa, bb);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, searchTexture.getTexture());

        drawTexture(matrices, getPositionX() + 14, getPositionY() + 7, 0, 0, searchTexture.getWidth(), searchTexture.getHeight(),
                searchTexture.getWidth(), searchTexture.getHeight());
        RenderSystem.setShaderTexture(0, searchIcoTexture.getTexture());
        drawTexture(matrices, getPositionX() + 21, getPositionY() + 13, 0, 0, searchIcoTexture.getWidth(), searchIcoTexture.getHeight(),
                searchIcoTexture.getWidth(), searchIcoTexture.getHeight());
        matrices.push();
        matrices.scale(INPUT_TEXT_SCALE, INPUT_TEXT_SCALE, INPUT_TEXT_SCALE);
        textField.render(matrices, mouseX, mouseY, partialTick);
        matrices.pop();
        super.render(matrices, mouseX, mouseY, partialTick);
    }

    public static void clearRightPanel() {
        System.out.println("clearRightPanel");
        clearPanelImages();
        DataHandler.INSTANCE.getNftEntities().clear();
    }

    public static void loadRightPanel() {
        clearPanelImages();
        List<NftEntity> nftEntities = DataHandler.INSTANCE.getNftEntities();
        int index = 0;
        for (NftEntity nftEntity : nftEntities) {
            ((NftImage) rightNftImages.get(index++)).loadImage(nftEntity);
        }
    }

    private static void clearPanelImages() {
        rightNftImages.forEach(imageBase -> imageBase.clear());
    }
}
