package net.metacraft.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.metacraft.mod.client.gui.MetaPaintingScreen;
import net.metacraft.mod.painting.MetaPaintingEntityRenderer;
import net.metacraft.mod.painting.MetaShowFlatRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ExampleClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(MetaEntityType.ENTITY_TYPE_META_PAINTING, (context) -> new MetaPaintingEntityRenderer(context));
        EntityRendererRegistry.register(MetaEntityType.ENTITY_TYPE_META_SHOWFLAT, (context) -> new MetaShowFlatRenderer(context));
        List<KeyBinding> bindings = new ArrayList<>(1);
        bindings.add(0, new KeyBinding("key.select.nft", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_N, "key.nft.category"));
        bindings.forEach(KeyBindingHelper::registerKeyBinding);
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (bindings.get(0).wasPressed()) {
                MinecraftClient.getInstance().setScreen(new MetaPaintingScreen());
            }
        });
    }
}