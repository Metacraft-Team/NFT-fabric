package net.metacraft.mod.network;

import net.metacraft.mod.network.s2c.MetaPaintingSpawnS2CPacket;
import net.metacraft.mod.network.s2c.MetaShowFlatSpawnS2CPacket;
import net.minecraft.entity.player.PlayerEntity;

public interface IClientNetworkCallback {
    void handleMetaPaintingPacket(MetaPaintingSpawnS2CPacket packet);

    void handleMetaShowFlatPacket(MetaShowFlatSpawnS2CPacket packet);

    void handlePlayerLoginPacket(PlayerEntity player);

    void handleTicketValidNotifyPacket(PlayerEntity player, String activityId);
}
