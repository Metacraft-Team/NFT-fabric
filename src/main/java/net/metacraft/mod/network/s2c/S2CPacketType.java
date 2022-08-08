package net.metacraft.mod.network.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.metacraft.mod.network.Packet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface S2CPacketType<T extends Packet<PlayerEntity>> {
    Identifier getId();

    default void send(ServerPlayerEntity playerEntity, T packet) {
        ServerPlayNetworking.send(playerEntity, getId(), packet.toBuffer());
    }

    default net.minecraft.network.Packet<?> toPacket(T packet) {
        return ServerPlayNetworking.createS2CPacket(getId(), packet.toBuffer());
    }
}