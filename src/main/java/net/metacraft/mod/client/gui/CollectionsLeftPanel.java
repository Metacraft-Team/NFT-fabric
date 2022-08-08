package net.metacraft.mod.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.client.gui.core.CollectionImage;
import net.metacraft.mod.client.gui.core.GuiBase;
import net.metacraft.mod.client.gui.core.GuiImageButton;
import net.metacraft.mod.client.gui.core.GuiScrollingContainer;
import net.metacraft.mod.client.texture.ITexture;
import net.metacraft.mod.client.texture.Textures;
import net.metacraft.mod.network.data.NftCollection;
import net.metacraft.mod.utils.Constants;
import net.metacraft.mod.utils.MetaCraftUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class CollectionsLeftPanel extends GuiBase {
    private static final ITexture LEFT_BG = Textures.LEFT_PANEL_BG;

    private GuiScrollingContainer scroller;

    private CollectionImage imageAll;

    private int scrollerHeight;

    int padding = 18;

    private TranslatableText titleText;

    private float titleScale = 0.8f;

    public CollectionsLeftPanel() {
        name = "CollectionsLeftPanel";
    }

    @Override
    public void init() {
        int positionX = (width - Constants.RIGHT_PANEL_WIDTH -
                Constants.LEFT_COLLECTION_WIDTH - Constants.LEFT_RIGHT_SPACING) / 2 + 5;
        int positionY = (height - Constants.PANEL_HEIGHT) / 2 + 5;
        int panelWidth = 46;
        int panelHeight = Constants.PANEL_HEIGHT;
        padding = 18;
        titleText = new TranslatableText("MY NFTS");
        titleText.setStyle(Style.EMPTY.withBold(true).withColor(Formatting.BLACK).withObfuscated(false));
        scrollerHeight = panelHeight - 50;

        setGuiPosition(positionX, positionY);
        setSize(panelWidth, panelHeight);

        scroller = new GuiScrollingContainer(false, true);
        this.addChild(scroller);

        int imageWidth = getContentWidth() - padding;

        int x = getPositionX() + padding / 2;
        imageAll = new CollectionImage(x, x, imageWidth, imageWidth, Textures.IMAGE_ALL);
        scroller.addContent(imageAll);
        scroller.setGuiPosition(getPositionX() + padding / 2, getPositionY() + 18);
        scroller.setSize(getContentWidth(), panelHeight);
        scroller.refreshVerticallyContent(getContentWidth(), scrollerHeight);

        GuiImageButton imageButton = new GuiImageButton(getPositionX() + padding / 2 + 9, positionY + 213, 10, 10, Textures.BTN_ADD_COLLECTION, btn -> {
            this.getParent().addChildWithoutFixPos(new CollectionOperaScreen(0, 0, width, height, this));
        });
        this.addChildWithoutFixPos(imageButton);
    }

    private void addCollections(NftCollection nftCollection) {
        if (MetaCraftUtils.isEmpty(nftCollection.getImageUrl())) {
            return;
        }
        int x = getPositionX() + padding / 2;
        int imageWidth = getContentWidth() - padding;
        CollectionImage imageBase = new CollectionImage(x, x, imageWidth, imageWidth);
        imageBase.loadImage(nftCollection);
        scroller.addContent(imageBase);
        scroller.refreshVerticallyContent(getContentWidth(), scrollerHeight);
    }

    public void updateCollections() {
        scroller.removeAllContent();
        scroller.addContent(imageAll);
        scroller.scrollTo(0, 0);
        for (NftCollection nftCollection : DataHandler.INSTANCE.getCurrentCollections()) {
            addCollections(nftCollection);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, LEFT_BG.getTexture());
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(
                GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
        );
        drawTexture(matrices, getPositionX(), getPositionY(), 0, 0, LEFT_BG.getWidth(), LEFT_BG.getHeight(),
                LEFT_BG.getWidth(), LEFT_BG.getHeight());
        matrices.push();
        matrices.scale(titleScale, titleScale, titleScale);
        textRenderer.draw(matrices, titleText, (getPositionX() + padding / 2 - 4) / titleScale, (getPositionY() + 5) / titleScale, 0x000000);
        matrices.pop();
        super.render(matrices, mouseX, mouseY, partialTick);
    }
}
