package net.metacraft.mod.network;

import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.network.core.TicketUtils;
import net.metacraft.mod.network.s2c.MetaPaintingSpawnS2CPacket;
import net.metacraft.mod.network.s2c.MetaShowFlatSpawnS2CPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

public enum ClientNetworkCallbackImpl implements IClientNetworkCallback {

    /**
     * INSTANCE
     */
    INSTANCE;

    @Override
    public void handleMetaPaintingPacket(MetaPaintingSpawnS2CPacket packet) {
        EntityType<?> entityType = packet.getEntityTypeId();
        Entity entity = entityType.create(MinecraftClient.getInstance().world);
        if (entity != null) {
            entity.onSpawnPacket(packet);
            int i = packet.getId();
            MinecraftClient.getInstance().world.addEntity(i, entity);
        }
    }

    @Override
    public void handleMetaShowFlatPacket(MetaShowFlatSpawnS2CPacket packet) {
        EntityType<?> entityType = packet.getEntityTypeId();
        Entity entity = entityType.create(MinecraftClient.getInstance().world);
        if (entity != null) {
            entity.onSpawnPacket(packet);
            int i = packet.getId();
            MinecraftClient.getInstance().world.addEntity(i, entity);
        }
    }

    @Override
    public void handlePlayerLoginPacket(PlayerEntity player) {
        DataHandler.INSTANCE.setPlayerId(MinecraftClient.getInstance().player.getUuid().toString());
    }

    @Override
    public void handleTicketValidNotifyPacket(PlayerEntity player, String activityId) {
        TicketUtils.refreshActivityImage(activityId);
    }
}
