package net.metacraft.mod.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public interface Packet<T extends PlayerEntity> {

    void onPacket(T player);

    void toBuffer(PacketByteBuf buffer);

    default PacketByteBuf toBuffer() {
        PacketByteBuf buffer = PacketByteBufs.create();
        toBuffer(buffer);
        return buffer;
    }
}