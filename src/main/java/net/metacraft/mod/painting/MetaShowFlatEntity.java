package net.metacraft.mod.painting;

import com.google.common.collect.Lists;
import net.metacraft.mod.MetaEntityType;
import net.metacraft.mod.MetaItems;
import net.metacraft.mod.network.NetworkManager;
import net.metacraft.mod.network.core.TicketUtils;
import net.metacraft.mod.network.s2c.MetaShowFlatSpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class MetaShowFlatEntity extends AbstractDecorationEntity {
    private String activityId;

    private static final TrackedData<ItemStack> ITEM_STACK;

    public PaintingMotive motive = PaintingMotive.ALBAN;

    static {
        ITEM_STACK = DataTracker.registerData(MetaShowFlatEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }

    protected void initDataTracker() {
        this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
    }

    public MetaShowFlatEntity(EntityType<? extends MetaShowFlatEntity> entityType, World world) {
        super(MetaEntityType.ENTITY_TYPE_META_SHOWFLAT, world);
    }

    MetaShowFlatEntity(World world, BlockPos pos, Direction direction, String activityId) {
        super(MetaEntityType.ENTITY_TYPE_META_SHOWFLAT, world, pos);
        this.activityId = activityId;
        List<PaintingMotive> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = Registry.PAINTING_MOTIVE.iterator();

        PaintingMotive paintingMotive2;
        while (iterator.hasNext()) {
            paintingMotive2 = (PaintingMotive) iterator.next();
            this.motive = paintingMotive2;
            this.setFacing(direction);
            if (this.canStayAttached()) {
                list.add(paintingMotive2);
                int j = paintingMotive2.getWidth() * paintingMotive2.getHeight();
                if (j > i) {
                    i = j;
                }
            }
        }

        if (!list.isEmpty()) {
            iterator = list.iterator();

            while (iterator.hasNext()) {
                paintingMotive2 = (PaintingMotive) iterator.next();
                if (paintingMotive2.getWidth() * paintingMotive2.getHeight() < i) {
                    iterator.remove();
                }
            }

            this.motive = list.get(this.random.nextInt(list.size()));
        }

        this.setFacing(direction);
        setHeldItemStack(getAsItemStack());
    }

    public String getActivityId() {
        return activityId;
    }

    public void setAttachmentPos(BlockPos pos) {
        this.attachmentPos = pos;
    }

    public void setFacing(Direction direction) {
        super.setFacing(direction);
    }

    public void setMotive(PaintingMotive motive) {
        this.motive = motive;
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("Motive", Registry.PAINTING_MOTIVE.getId(this.motive).toString());
        nbt.putByte("Facing", (byte) this.facing.getHorizontal());
        nbt.putString("activityId", activityId);
        if (!this.getHeldItemStack().isEmpty()) {
            nbt.put("Item", this.getHeldItemStack().writeNbt(new NbtCompound()));
        }
        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.motive = Registry.PAINTING_MOTIVE.get(Identifier.tryParse(nbt.getString("Motive")));
        this.facing = Direction.fromHorizontal(nbt.getByte("Facing"));
        this.activityId = nbt.getString("activityId");
        refreshActivityImage();
        NbtCompound nbtCompound = nbt.getCompound("Item");
        if (nbtCompound != null && !nbtCompound.isEmpty()) {
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            this.setHeldItemStack(itemStack);
        }
        super.readCustomDataFromNbt(nbt);
        this.setFacing(this.facing);
    }

    private void refreshActivityImage() {
        if (!world.isClient) {
            return;
        }
        TicketUtils.refreshActivityImage(activityId);
    }

    public int getWidthPixels() {
        return 16 * 16;
    }

    public int getHeightPixels() {
        return 16 * 6;
    }

    private ItemStack getHeldItemStack() {
        return this.getDataTracker().get(ITEM_STACK);
    }

    private void setHeldItemStack(ItemStack value) {
        if (!value.isEmpty()) {
            value = value.copy();
            value.setCount(1);
            value.setHolder(this);
            value.getOrCreateNbt().putString("activityId", activityId);
        }

        this.getDataTracker().set(ITEM_STACK, value);
    }


    public void onBreak(@Nullable Entity entity) {
        System.out.println("onBreak");
        ItemStack itemStack = this.getHeldItemStack();
        this.setHeldItemStack(ItemStack.EMPTY);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
            if (entity instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity) entity;
                if (playerEntity.getAbilities().creativeMode) {
                    return;
                }
            }
            this.dropStack(itemStack);
        }
    }


    public void onPlace() {
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
    }

    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.setPosition(x, y, z);
    }

    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        BlockPos blockPos = this.attachmentPos.add(x - this.getX(), y - this.getY(), z - this.getZ());
        this.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return NetworkManager.INSTANCE.getServerCreateMetaShowFlatPacket().toPacket(new MetaShowFlatSpawnS2CPacket(this));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        MetaShowFlatSpawnS2CPacket metaShowFlatSpawnS2CPacket = (MetaShowFlatSpawnS2CPacket) packet;
        BlockPos pos = metaShowFlatSpawnS2CPacket.getPos();
        this.attachmentPos = pos;
        this.motive = metaShowFlatSpawnS2CPacket.getMotive();
        this.activityId = metaShowFlatSpawnS2CPacket.getActivityId();
        setFacing(metaShowFlatSpawnS2CPacket.getFacing());
        double d = pos.getX();
        double e = pos.getY();
        double f = pos.getZ();
        refreshActivityImage();
        this.setPosition(d, e, f);
        this.setId(packet.getId());
        this.setUuid(packet.getUuid());
    }

    protected ItemStack getAsItemStack() {
        return new ItemStack(MetaItems.ITEM_META_SHOWFLAT);
    }
}
