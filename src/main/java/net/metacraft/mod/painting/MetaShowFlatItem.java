package net.metacraft.mod.painting;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class MetaShowFlatItem extends Item {
    private String activityId = "";

    public MetaShowFlatItem(EntityType<? extends AbstractDecorationEntity> type, Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockPos blockPos2 = blockPos.offset(direction);
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack itemStack = context.getStack();
        activityId = getActivityId(itemStack);
        if (playerEntity != null && !this.canPlaceOn(playerEntity, direction, itemStack, blockPos2)) {
            return ActionResult.FAIL;
        } else {
            World world = context.getWorld();
            MetaShowFlatEntity showFlatEntity = new MetaShowFlatEntity(world, blockPos2, direction, activityId);
            System.out.println("MetaShowFlatItem activityId :  " + activityId);
            NbtCompound nbtCompound = itemStack.getNbt();
            if (nbtCompound != null) {
                EntityType.loadFromEntityNbt(world, playerEntity, showFlatEntity, nbtCompound);
            }

            if (!world.isClient) {
                ((AbstractDecorationEntity)showFlatEntity).onPlace();
                world.emitGameEvent(playerEntity, GameEvent.ENTITY_PLACE, blockPos);
                world.spawnEntity(showFlatEntity);
            }
            itemStack.decrement(1);
            return ActionResult.success(world.isClient);
        }
    }

    private String getActivityId(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null) {
            System.out.println("MetaShowFlatItem get nbt is null");
            return "";
        }
        return nbt.getString("activityId");
    }

    public void saveItemInfo(ItemStack itemStack, String info) {
        itemStack.getOrCreateNbt().putString("activityId", info);
        this.activityId = info;
    }

    protected boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
        return !side.getAxis().isVertical() && player.canPlaceOn(pos, side, stack);
    }
}