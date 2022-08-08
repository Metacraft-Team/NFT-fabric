package net.metacraft.mod.network.s2c;

import net.metacraft.mod.network.ClientNetworkCallbackImpl;
import net.metacraft.mod.network.Packet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class PlayerLoginS2CPacket implements Packet<PlayerEntity> {
    private final String uuid;

    public PlayerLoginS2CPacket(PlayerEntity player) {
        this.uuid = player.getUuid().toString();
    }

    public PlayerLoginS2CPacket(PacketByteBuf buffer) {
        this.uuid = buffer.readString();
    }

    @Override
    public void onPacket(PlayerEntity player) {
        System.out.println("PlayerLoginS2CPacketPlayerLoginS2CPacket : " + player.getUuid().toString());
        ClientNetworkCallbackImpl.INSTANCE.handlePlayerLoginPacket(player);
    }

    @Override
    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeString(uuid);
    }
}
