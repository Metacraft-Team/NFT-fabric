package net.metacraft.mod.client.gui.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.client.gui.ImagesRightPanel;
import net.metacraft.mod.client.texture.ITexture;
import net.metacraft.mod.client.texture.Textures;
import net.metacraft.mod.network.core.HttpsUtils;
import net.metacraft.mod.network.data.ImageInfo;
import net.metacraft.mod.network.data.NftCollection;
import net.metacraft.mod.renderer.MapRenderer;
import net.metacraft.mod.utils.MetaCraftUtils;
import net.metacraft.mod.utils.ThreadPoolUtils;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.awt.image.BufferedImage;
import java.util.Collections;

public class CollectionImage extends ImageBase {
    private NftCollection nftCollection = new NftCollection();

    private boolean clickable = true;

    private ITexture textureAll;

    private ITexture frameTexture = Textures.COLLECTION_FRAME;

    private ITexture selectedTexture = Textures.COLLECTION_SELECTED;

    private boolean selected;

    public CollectionImage(int x, int y, int width, int height) {
        this(x, y, width, height, null);
    }

    public CollectionImage(int x, int y, int width, int height, ITexture texture) {
        super(x, y, width, height);
        name = "CollectionImage";
        this.textureAll = texture;
    }

    public void loadImage(NftCollection collection) {
        this.nftCollection = collection;
        refreshTips();
        ThreadPoolUtils.INSTANCE.execute(() -> {
            deltaX = 0;
            deltaY = 0;
            String url = collection.getImageUrl();
            BufferedImage image = null;
            ImageInfo imageInfo = null;
            if (!DataHandler.INSTANCE.getImagesColorMap().containsKey(url)) {
                // 从url下载图片
                image = MetaCraftUtils.getBufferedImageForUrl(url);
                if (image == null) {
                    return;
                }
                colors = MapRenderer.render(image);
                imageInfo = new ImageInfo(image.getWidth(), image.getHeight(), colors);
                DataHandler.INSTANCE.getImagesColorMap().put(url, imageInfo);
            } else {
                imageInfo = DataHandler.INSTANCE.getImagesColorMap().get(url);
                colors = imageInfo.getColors();
            }

            // 转换成二进制流
            updateTexture(colors);
            if (imageInfo.getWidth() > imageInfo.getHeight()) {
                int realHeight = (int) (backgroundHeight * (imageInfo.getHeight() / (float) imageInfo.getWidth()));
                actualWidth = backgroundWidth;
                actualHeight = realHeight;
                deltaY = (backgroundHeight - realHeight) / 2;
            } else {
                int realWidth = (int) (backgroundWidth * (imageInfo.getWidth() / (float) imageInfo.getHeight()));
                actualWidth = realWidth;
                actualHeight = backgroundHeight;
                deltaX = (backgroundWidth - realWidth) / 2;
            }
        });
    }

    private void refreshTips() {
        tips.clear();
        tips.add(new TranslatableText("Collection name:").formatted(Formatting.RED));
        tips.add(new TranslatableText(nftCollection.getName()).formatted(Formatting.YELLOW));
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        super.render(matrices, mouseX, mouseY, partialTick);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(
                GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
        );
        if (textureAll != null) {
            // 二级菜单
            if (isMouseOver) {
                drawTooltip(Collections.emptyList());
            }
            RenderSystem.setShaderTexture(0, textureAll.getTexture());
            drawTexture(matrices, getPositionX(), getPositionY(), 0, 0, getContentWidth(), getContentHeight(), getContentWidth(), getContentHeight());
        }
        if (colors != null || textureAll != null) {
            RenderSystem.setShaderTexture(0, frameTexture.getTexture());
            drawTexture(matrices, getPositionX(), getPositionY(), 0, 0, getContentWidth(), getContentHeight(), getContentWidth(), getContentHeight());
        }
        if (selected) {
            RenderSystem.setShaderTexture(0, selectedTexture.getTexture());
            drawTexture(matrices, getPositionX(), getPositionY(), 0, 0, getContentWidth(), getContentHeight(), getContentWidth(), getContentHeight());
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver && clickable) {
            ImagesRightPanel.clearRightPanel();
            DataHandler.INSTANCE.setCurrentCollection(nftCollection.getName());
            DataHandler.INSTANCE.setCurrentPage(1);
            HttpsUtils.queryNftImages(1, null, ImagesRightPanel::loadRightPanel);
            CollectionImage historySelected = DataHandler.INSTANCE.getCollectionImage();
            if (historySelected != null) {
                historySelected.setSelected(false);
            }
            DataHandler.INSTANCE.setCollectionImage(this);
            selected = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
