package net.metacraft.mod.network.c2s;

import net.metacraft.mod.MetaItems;
import net.metacraft.mod.network.Packet;
import net.metacraft.mod.network.core.HttpsUtils;
import net.metacraft.mod.painting.MetaDecorationItem;
import net.metacraft.mod.utils.Constants;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ScreenEntitySpawnC2SPacket implements Packet<ServerPlayerEntity> {
    public static final Identifier ID = new Identifier(Constants.MOD_ID, "screen_spawn");

    private String imageInfoJson;

    public ScreenEntitySpawnC2SPacket(PacketByteBuf buffer) {
        imageInfoJson = buffer.readString();
    }

    public ScreenEntitySpawnC2SPacket(String imageInfoJson) {
        this.imageInfoJson = imageInfoJson;
    }

    @Override
    public void onPacket(ServerPlayerEntity player) {
        System.out.println("ScreenEntitySpawnC2SPacket onPacket, " + player.getUuid().toString());
        if (!HttpsUtils.checkNftOwner(imageInfoJson, player.getUuid().toString())) {
            System.out.println("nft check false");
            return;
        }
        ItemStack stack = new ItemStack(MetaItems.ITEM_META_PAINTING);
        ((MetaDecorationItem) MetaItems.ITEM_META_PAINTING).saveItemInfo(stack, imageInfoJson);
        if (!player.getInventory().insertStack(stack)) {
            player.dropItem(stack, false);
        }
    }

    @Override
    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeString(imageInfoJson);
    }
}
