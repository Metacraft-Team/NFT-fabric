package net.metacraft.mod.client.gui.core;

import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;
import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.client.texture.ITexture;
import net.metacraft.mod.client.texture.Textures;
import net.metacraft.mod.network.NetworkManager;
import net.metacraft.mod.network.c2s.ScreenEntitySpawnC2SPacket;
import net.metacraft.mod.network.data.ImageInfo;
import net.metacraft.mod.network.data.NftEntity;
import net.metacraft.mod.renderer.MapRenderer;
import net.metacraft.mod.utils.MetaCraftUtils;
import net.metacraft.mod.utils.ThreadPoolUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.awt.image.BufferedImage;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class NftImage extends ImageBase {
    private ReentrantLock LOCK = new ReentrantLock();

    private Future<?> future;

    private NftEntity nftEntity;

    private ITexture nftFrame = Textures.NFT_FRAME;

    private TranslatableText nftName;

    private Style style;

    public NftImage(int x, int y, int width, int height) {
        super(x, y, width, height);
        name = "NftImage";
    }

    @Override
    public void init() {
        style = Style.EMPTY.withBold(true).withColor(Formatting.BLACK).withObfuscated(false);
    }

    public synchronized void loadImage(NftEntity nftEntity) {
        if (future != null) {
            future.cancel(true);
        }
        if (!isNftValid(nftEntity)) {
            return;
        }
        refreshTips(nftEntity);
        future = ThreadPoolUtils.INSTANCE.execute(() -> {
            LOCK.lock();
            this.nftEntity = nftEntity;
            try {
                if (!isNftValid(nftEntity)) {
                    return;
                }
                String url = nftEntity.getImageUrl();
                deltaX = 0;
                deltaY = 0;
                BufferedImage image;
                ImageInfo imageInfo;
                if (!DataHandler.INSTANCE.getImagesColorMap().containsKey(url)) {
                    // 从url下载图片
                    image = MetaCraftUtils.getBufferedImageForUrl(url);
                    if (image == null || !isNftValid(nftEntity)) {
                        return;
                    }
                    colors = MapRenderer.render(image);
                    imageInfo = new ImageInfo(image.getWidth(), image.getHeight(), colors);
                    DataHandler.INSTANCE.getImagesColorMap().put(nftEntity.getImageUrl(), imageInfo);
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
            } finally {
                LOCK.unlock();
            }
        });
    }

    private void refreshTips(NftEntity nftEntity) {
        tips.clear();
        tips.add(new TranslatableText("NFT name:").formatted(Formatting.RED));
        tips.add(new TranslatableText(nftEntity.getName()).formatted(Formatting.YELLOW));
        String text = nftEntity.getName();
        nftName = new TranslatableText(text);
        if (textRenderer.getTextHandler().getWidth(nftName) > 109) {
            String cutText = textRenderer.trimToWidth(nftEntity.getName(), 109) + "..";
            nftName = new TranslatableText(cutText);
        }
        nftName.setStyle(style);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver) {
            if (nftEntity == null || colors == null) {
                return super.mouseClicked(mouseX, mouseY, button);
            }
            NetworkManager.INSTANCE.getClientScreenSpawnPacket().send(new ScreenEntitySpawnC2SPacket(new Gson().toJson(nftEntity)));
            closeAll();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        super.render(matrices, mouseX, mouseY, partialTick);
        if (colors == null) {
            return;
        }
        matrices.push();
        RenderSystem.setShaderTexture(0, nftFrame.getTexture());
        drawTexture(matrices, getPositionX(), getPositionY(), 0, 0, nftFrame.getWidth(), nftFrame.getHeight(),
                nftFrame.getWidth(), nftFrame.getHeight());
//        matrices.scale(0.5f, 0.5f, 0.5f);
        matrices.pop();
        matrices.push();
        matrices.scale(0.5f, 0.5f, 0.5f);
        textRenderer.draw(matrices, nftName, (getPositionX() + 3) * 2, (getPositionY() + 75) * 2, 0x000000);
        matrices.pop();
    }

    protected boolean isNftValid(NftEntity nftEntity) {
        if (nftEntity == null) {
            return false;
        }
        return nftEntity.getCurrentRequestId().equals(DataHandler.INSTANCE.getRequestId());
    }
}
