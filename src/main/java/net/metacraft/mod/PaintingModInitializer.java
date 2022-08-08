package net.metacraft.mod;


import net.fabricmc.api.ModInitializer;
import net.metacraft.mod.config.ConfigHandler;
import net.metacraft.mod.network.NetworkManager;

public class PaintingModInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        MetaItems.init();
        MetaEntityType.init();
        NetworkManager.INSTANCE.bootstrap();
        NetworkManager.INSTANCE.registerC2SListeners();
        NetworkManager.INSTANCE.registerS2CListeners();
        ConfigHandler.loadConfig();
    }
}
