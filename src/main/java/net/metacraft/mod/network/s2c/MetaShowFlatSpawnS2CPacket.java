package net.metacraft.mod.network.s2c;

import net.metacraft.mod.network.ClientNetworkCallbackImpl;
import net.metacraft.mod.network.Packet;
import net.metacraft.mod.painting.MetaShowFlatEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class MetaShowFlatSpawnS2CPacket extends EntitySpawnS2CPacket implements Packet<PlayerEntity> {
    private final int id;
    private final UUID uuid;
    private final BlockPos pos;
    private final Direction facing;
    private final int motiveId;
    private String activityId;

    public MetaShowFlatSpawnS2CPacket(MetaShowFlatEntity entity) {
        super(entity);
        this.id = entity.getId();
        this.uuid = entity.getUuid();
        this.pos = entity.getDecorationBlockPos();
        this.facing = entity.getHorizontalFacing();
        this.motiveId = Registry.PAINTING_MOTIVE.getRawId(entity.motive);
        this.activityId = entity.getActivityId();
    }

    public MetaShowFlatSpawnS2CPacket(PacketByteBuf buf) {
        super(buf);
        this.id = buf.readVarInt();
        this.uuid = buf.readUuid();
        this.pos = buf.readBlockPos();
        this.facing = Direction.fromHorizontal(buf.readUnsignedByte());
        this.motiveId = buf.readVarInt();
        this.activityId = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeVarInt(this.id);
        buf.writeUuid(this.uuid);
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.facing.getHorizontal());
        buf.writeVarInt(this.motiveId);
        buf.writeString(this.activityId);
    }


    public int getId() {
        return this.id;
    }

    public UUID getPaintingUuid() {
        return this.uuid;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public PaintingMotive getMotive() {
        return Registry.PAINTING_MOTIVE.get(this.motiveId);
    }

    public String getActivityId() {
        return activityId;
    }

    @Override
    public void onPacket(PlayerEntity player) {
        ClientNetworkCallbackImpl.INSTANCE.handleMetaShowFlatPacket(this);
    }

    @Override
    public void toBuffer(PacketByteBuf buffer) {
        write(buffer);
    }
}
