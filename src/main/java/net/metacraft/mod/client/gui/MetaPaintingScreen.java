package net.metacraft.mod.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.client.gui.core.GuiBase;
import net.metacraft.mod.client.texture.ITexture;
import net.metacraft.mod.client.texture.Textures;
import net.metacraft.mod.network.core.HttpsUtils;
import net.metacraft.mod.network.data.NftCollection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashSet;
import java.util.List;

public class MetaPaintingScreen extends GuiBase {
    private ITexture texture = Textures.PANEL_BG;

    public MetaPaintingScreen() {
        name = "MetaPaintingScreen";
    }

    @Override
    public void init() {
        closeAllChildren(this);
        setSize(width, height);
        ImagesRightPanel imagesPanel = new ImagesRightPanel();
        this.addChild(imagesPanel);
        CollectionsLeftPanel collectionsLeftPanel = new CollectionsLeftPanel();
        this.addChild(collectionsLeftPanel);

        DataHandler.INSTANCE.setCurrentPage(1);
        DataHandler.INSTANCE.setCurrentCollection("");
        DataHandler.INSTANCE.setCollectionImage(null);

        HttpsUtils.queryNftImages(1, null, () -> {
            ImagesRightPanel.loadRightPanel();
            MinecraftClient.getInstance().execute(() -> {
                if (DataHandler.INSTANCE.getCurrentCollections().isEmpty()) {
                    List<NftCollection> nftCollections = DataHandler.INSTANCE.getNftCollections();
                    List<NftCollection> currentCollections = nftCollections.subList(0, Math.min(3, nftCollections.size()));
                    DataHandler.INSTANCE.setCurrentCollections(new HashSet<>(currentCollections));
                }
                collectionsLeftPanel.updateCollections();
            });
        });
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture.getTexture());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(
                GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
        );

        int x = (width - texture.getWidth()) / 2;
        int y = (height - texture.getHeight()) / 2;
        drawTexture(matrices, x, y, 0, 0, texture.getWidth(), texture.getHeight(),
                texture.getWidth(), texture.getHeight());
        super.render(matrices, mouseX, mouseY, partialTick);
    }
}
