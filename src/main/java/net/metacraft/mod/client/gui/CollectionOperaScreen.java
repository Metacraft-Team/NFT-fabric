package net.metacraft.mod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.client.gui.core.CollectionImage;
import net.metacraft.mod.client.gui.core.GuiBase;
import net.metacraft.mod.client.gui.core.GuiImageButton;
import net.metacraft.mod.client.texture.ITexture;
import net.metacraft.mod.client.texture.Textures;
import net.metacraft.mod.network.data.NftCollection;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CollectionOperaScreen extends GuiBase {
    private final static int SCREEN_WIDTH = 250;

    private final static int SCREEN_HEIGHT = 200;

    private final CollectionsLeftPanel leftPanel;

    private final int x, y;

    private ITexture collectionOperaPanel = Textures.COLLECTION_OPERA_PANEL;

    private TranslatableText titleText;

    public CollectionOperaScreen(int x, int y, int width, int height, CollectionsLeftPanel leftPanel) {
        setGuiPosition(x, y);
        setSize(width, height);
        this.x = width / 2 - SCREEN_WIDTH / 2;
        this.y = height / 2 - SCREEN_HEIGHT / 2;
        this.leftPanel = leftPanel;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 点击事件拦截不传递到下层UI，但是需要分发到各子对象处理
        if (button == 0 && isMouseOver) {
            getChildren().forEach(child -> child.mouseClicked(mouseX, mouseY, button));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double wheelMove) {
        return true;
    }

    @Override
    public void init() {
        final int collectionWidth = 24;
        final int collectionHeight = 24;
        final int deltaX = (SCREEN_WIDTH - collectionWidth * 7) / 8;
        int relationX = deltaX;
        int relationY = deltaX + 25;

        List<NftCollection> nftCollections = DataHandler.INSTANCE.getNftCollections();
        if (nftCollections == null || nftCollections.isEmpty()) {
            close();
            return;
        }
        titleText = new TranslatableText("MY COLLECTIONS");
        titleText.setStyle(Style.EMPTY.withBold(true).withColor(Formatting.BLACK).withObfuscated(false));
        this.addChildWithoutFixPos(new GuiImageButton(x + SCREEN_WIDTH - 20,
                y + 10, 10, 10, Textures.BTN_CLOSE, imageButton -> {
            leftPanel.updateCollections();
            close();
        }));

        Set<NftCollection> currentCollections = DataHandler.INSTANCE.getCurrentCollections();

        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                if (index == nftCollections.size()) {
                    return;
                }
                NftCollection nftCollection = nftCollections.get(index++);
                CollectionImage collectionImage = new CollectionImage(x + relationX, y + relationY, collectionWidth, collectionHeight);
                collectionImage.loadImage(nftCollection);
                collectionImage.setClickable(false);
                this.addChildWithoutFixPos(collectionImage);
                int btnWidth = Textures.BTN_ADD.getWidth();
                ITexture icon = currentCollections.contains(nftCollection) ? Textures.BTN_DEC : Textures.BTN_ADD;
                this.addChildWithoutFixPos(new GuiImageButton(x + relationX + collectionWidth / 2 - btnWidth / 2,
                        y + relationY + collectionHeight + 4, icon, btn -> {
                    boolean isAdd = btn.getTexture() == Textures.BTN_ADD;
                    btn.setTexture(isAdd ? Textures.BTN_DEC : Textures.BTN_ADD);
                    if (isAdd) {
                        currentCollections.add(nftCollection);
                    } else {
                        currentCollections.remove(nftCollection);
                    }
                }));
                relationX = relationX + collectionWidth + deltaX;
            }
            relationX = deltaX;
            relationY = relationY + collectionHeight + deltaX + 10;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        renderBackground(matrices);
        RenderSystem.setShaderTexture(0, collectionOperaPanel.getTexture());
        drawTexture(matrices, x, y, 0, 0, collectionOperaPanel.getWidth(), collectionOperaPanel.getHeight(),
                collectionOperaPanel.getWidth(), collectionOperaPanel.getHeight());
        if (isMouseOver) {
            drawTooltip(Collections.emptyList());
        }
        textRenderer.draw(matrices, titleText, x + 12, (y + 15), 0x000000);
        super.render(matrices, mouseX, mouseY, partialTick);
    }
}
