package net.metacraft.mod.painting;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.metacraft.mod.MetaEntityType;
import net.metacraft.mod.MetaItems;
import net.metacraft.mod.client.DataHandler;
import net.metacraft.mod.network.NetworkManager;
import net.metacraft.mod.network.data.ImageInfo;
import net.metacraft.mod.network.data.NftEntity;
import net.metacraft.mod.network.s2c.MetaPaintingSpawnS2CPacket;
import net.metacraft.mod.renderer.MapRenderer;
import net.metacraft.mod.utils.MetaCraftUtils;
import net.metacraft.mod.utils.ThreadPoolUtils;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MetaPaintingEntity extends AbstractDecorationEntity {
    private String imageInfoJson;

    private static final TrackedData<ItemStack> ITEM_STACK;

    public PaintingMotive motive = PaintingMotive.ALBAN;

    private long useTime;

    static {
        ITEM_STACK = DataTracker.registerData(MetaPaintingEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }

    protected void initDataTracker() {
        this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
    }

    public MetaPaintingEntity(EntityType<? extends MetaPaintingEntity> entityType, World world) {
        super(MetaEntityType.ENTITY_TYPE_META_PAINTING, world);
    }

    MetaPaintingEntity(World world, BlockPos pos, Direction direction, String info) {
        super(MetaEntityType.ENTITY_TYPE_META_PAINTING, world, pos);
        this.imageInfoJson = info;
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

    public String getImageInfoJson() {
        return imageInfoJson;
    }

    public void setImageInfoJson(String imageInfoJson) {
        this.imageInfoJson = imageInfoJson;
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
        nbt.putString("imageInfoJson", imageInfoJson);
        if (!this.getHeldItemStack().isEmpty()) {
            nbt.put("Item", this.getHeldItemStack().writeNbt(new NbtCompound()));
        }
        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.motive = Registry.PAINTING_MOTIVE.get(Identifier.tryParse(nbt.getString("Motive")));
        this.facing = Direction.fromHorizontal(nbt.getByte("Facing"));
        this.imageInfoJson = nbt.getString("imageInfoJson");
        downloadImage();
        NbtCompound nbtCompound = nbt.getCompound("Item");
        if (nbtCompound != null && !nbtCompound.isEmpty()) {
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            this.setHeldItemStack(itemStack);
        }
        super.readCustomDataFromNbt(nbt);
        this.setFacing(this.facing);
    }

    private void downloadImage() {
        if (!world.isClient) {
            return;
        }
        NftEntity nftEntity = new Gson().fromJson(imageInfoJson, NftEntity.class);
        if (nftEntity == null) {
            return;
        }
        ThreadPoolUtils.INSTANCE.execute(() -> {
            String url = nftEntity.getImageUrl();
            if (DataHandler.INSTANCE.getImagesColorMap().containsKey(url)) {
                return;
            }
            BufferedImage image = MetaCraftUtils.getBufferedImageForUrl(url);
            if (image == null) {
                return;
            }
            byte[] colors = MapRenderer.render(image);
            ImageInfo imageInfo = new ImageInfo(image.getWidth(), image.getHeight(), colors);
            DataHandler.INSTANCE.getImagesColorMap().put(url, imageInfo);
        });

    }

    public int getWidthPixels() {
        return this.motive == null ? 1 : this.motive.getWidth();
    }

    public int getHeightPixels() {
        return this.motive == null ? 1 : this.motive.getHeight();
    }

    private ItemStack getHeldItemStack() {
        return this.getDataTracker().get(ITEM_STACK);
    }

    private void setHeldItemStack(ItemStack value) {
        if (!value.isEmpty()) {
            value = value.copy();
            value.setCount(1);
            value.setHolder(this);
            value.getOrCreateNbt().putString("imageInfoJson", imageInfoJson);
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
        return NetworkManager.INSTANCE.getServerCreateMetaPaintingPacket().toPacket(new MetaPaintingSpawnS2CPacket(this));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        MetaPaintingSpawnS2CPacket metaPaintingSpawnS2CPacket = (MetaPaintingSpawnS2CPacket) packet;
        BlockPos pos = metaPaintingSpawnS2CPacket.getPos();
        this.attachmentPos = pos;
        this.motive = metaPaintingSpawnS2CPacket.getMotive();
        this.imageInfoJson = metaPaintingSpawnS2CPacket.getImageInfoJson();
        setFacing(metaPaintingSpawnS2CPacket.getFacing());
        double d = pos.getX();
        double e = pos.getY();
        double f = pos.getZ();
        downloadImage();
        this.setPosition(d, e, f);
        this.setId(packet.getId());
        this.setUuid(packet.getUuid());
    }

    protected ItemStack getAsItemStack() {
        return new ItemStack(MetaItems.ITEM_META_PAINTING);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (System.currentTimeMillis() - useTime < 1000) {
            return super.interact(player, hand);
        }
        useTime = System.currentTimeMillis();
        System.out.println("MetaPainting interact");
        NftEntity nftEntity = new Gson().fromJson(imageInfoJson, NftEntity.class);
        if (nftEntity == null || MetaCraftUtils.isEmpty(nftEntity.getPermalink())) {
            return super.interact(player, hand);
        }
        if (world.isClient) {
            System.out.println(nftEntity.getImageUrl());
            boolean isWin = false;
            if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
                isWin = true;
            }
            try {
                if (isWin) {
                    Runtime.getRuntime().exec("cmd /c start " + nftEntity.getPermalink());
                } else {
                    String str = "open " + nftEntity.getPermalink();
                    System.out.println(str);
                    Runtime.getRuntime().exec(str);
                }
            } catch (IOException e) {
                System.out.println("interact IOException " + e);
            }
        } else {
            String content = "[\"\",{\"text\":\"Learn more about this NFT on Opensea :\",\"color\":\"white\"},{\"text\":\" \",\"color\":\"aqua\"},{\"text\":\"%s\",\"underlined\":true,\"color\":\"aqua\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"%s\"}}]";
            StringBuilder command = new StringBuilder();
            command.append("/tellraw");
            command.append(" ");
            command.append(player.getName().asString());
            command.append(" ");
            command.append(String.format(content, nftEntity.getPermalink(), nftEntity.getPermalink()));
            player.getServer().getCommandManager().execute(player.getServer().getCommandSource(), command.toString());
        }
        return super.interact(player, hand);
    }
}
