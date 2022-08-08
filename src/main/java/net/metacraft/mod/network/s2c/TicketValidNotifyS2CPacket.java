package net.metacraft.mod.network.s2c;

import net.metacraft.mod.network.ClientNetworkCallbackImpl;
import net.metacraft.mod.network.Packet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class TicketValidNotifyS2CPacket implements Packet<PlayerEntity> {
    private final String activityId;

    public TicketValidNotifyS2CPacket(String activityId) {
        this.activityId = activityId;
    }

    public TicketValidNotifyS2CPacket(PacketByteBuf buffer) {
        this.activityId = buffer.readString();
    }

    @Override
    public void onPacket(PlayerEntity player) {
        System.out.println("TicketValidNotifyS2CPacket : " + player.getUuid().toString() + " " + activityId);
        ClientNetworkCallbackImpl.INSTANCE.handleTicketValidNotifyPacket(player, activityId);
    }

    @Override
    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeString(activityId);
    }
}
