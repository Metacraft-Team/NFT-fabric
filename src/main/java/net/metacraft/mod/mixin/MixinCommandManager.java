package net.metacraft.mod.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.metacraft.mod.MetaItems;
import net.metacraft.mod.network.NetworkManager;
import net.metacraft.mod.network.core.TicketUtils;
import net.metacraft.mod.network.data.NftEntity;
import net.metacraft.mod.network.data.Position;
import net.metacraft.mod.network.s2c.TicketValidNotifyS2CPacket;
import net.metacraft.mod.utils.MetaCraftUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(CommandManager.class)
abstract class MixinCommandManager {
    private static final int COMMAND_TICKET_LENGTH = 5;

    private Map<String, Long> playerExecTime = new HashMap<>();

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void commandExecute(ServerCommandSource commandSource, String command, CallbackInfoReturnable<Integer> callback) {
        try {
            if (command != null && (command.startsWith("/metacraft") || command.startsWith("metacraft"))) {
                if (!commandSource.hasPermissionLevel(2)) {
                    return;
                }
                String[] splitCommands = command.split(" ");
                int result = executeCheckTicket(commandSource, splitCommands);
                if (result == 1 || result == 0) {
                    callback.setReturnValue(result);
                }
            }
        } catch (Throwable e) {
            System.out.println("commandExecute exception : " + e);
            callback.setReturnValue(0);
        }
    }

    private int executeCheckTicket(ServerCommandSource source, String[] splitCommands) {
        // metacraft checkTicket --clicker-- 1
        // metacraft checkTicket --clicker-- 1 {"type":"tp", "x":"-910", "y":"72", "z":"-1526"}
        if ((splitCommands.length != COMMAND_TICKET_LENGTH && splitCommands.length != COMMAND_TICKET_LENGTH - 1)
                || !splitCommands[1].equals("checkTicket")) {
            return 2;
        }
        try {
            String playName = splitCommands[2];
            PlayerEntity player = source.getServer().getPlayerManager().getPlayer(playName);
            if (player == null) {
                System.out.println("executeCheckTicket player is null");
                return 0;
            }

            long time = playerExecTime.getOrDefault(playName, 0L);
            long currentTime = System.currentTimeMillis();
            if (currentTime - time < 800) {
                System.out.println("executeCheckTicket interval is too short");
                return 0;
            }
            playerExecTime.put(playName, currentTime);

            String activityId = splitCommands[3];
            Position position = null;
            if (splitCommands.length == COMMAND_TICKET_LENGTH) {
                position = parseNBT(splitCommands[4]);
            }
            if (MetaCraftUtils.isEmpty(activityId)) {
                System.out.println("executeCheckTicket activityId or position is null");
                return 0;
            }
            ItemStack mainHandItem = player.getMainHandStack();
            System.out.println("executeCheckTicket mainHandItem is " + mainHandItem + " activityId : " + activityId);
            if (mainHandItem.getItem() == MetaItems.ITEM_META_PAINTING) {
                System.out.println("executeCheckTicket item is ticket");
                String imageInfoJson = mainHandItem.getNbt().getString("imageInfoJson");
                if (MetaCraftUtils.isEmpty(imageInfoJson)) {
                    System.out.println("executeCheckTicket imageInfoJson is null");
                    return 0;
                }
                NftEntity nftEntity = new Gson().fromJson(imageInfoJson, NftEntity.class);
                if (nftEntity == null) {
                    System.out.println("executeCheckTicket nftEntity is null");
                    return 0;
                }
                String result = TicketUtils.ticketCheck(player.getUuidAsString(), activityId, nftEntity.getTokenId(), nftEntity.getContractAddress());
                if (result.equals("success")) {
                    System.out.println("executeCheckTicket start to packet");
                    // notify all players refresh
                    PlayerManager playerManager = source.getServer().getPlayerManager();
                    playerManager.getPlayerList().forEach(serverPlayerEntity -> NetworkManager.INSTANCE.getServerTicketValidNotifyPacket()
                            .send(serverPlayerEntity, new TicketValidNotifyS2CPacket(activityId)));

                    player.sendMessage(new TranslatableText("Verification completed, you have been teleported to this area.").formatted(Formatting.GOLD), false);
                    if (position != null) {
                        tpPlayer(source, player, position);
                    }
                    return 1;
                } else {
                    player.sendMessage(new TranslatableText(result).formatted(Formatting.RED), false);
                }
            } else {
                player.sendMessage(new TranslatableText("Sorry, you don't have the required NFT in your main hand.").formatted(Formatting.RED), false);
            }
        } catch (Exception e) {
            System.out.println("executeCheckTicket Exception : " + e);
        }
        return 0;
    }

    private void tpPlayer(ServerCommandSource source, PlayerEntity player, Position position) {
        if (position != null) {
            StringBuilder builder = new StringBuilder("/execute in");
            builder.append(" ").append("minecraft:overworld")
                    .append(" run tp ").append(player.getEntityName()).append(" ").append(position.getX()).append(" ").append(position.getY()).append(" ").append(position.getZ());
            source.getServer().getCommandManager().execute(source.getServer().getCommandSource(), builder.toString());
        }
    }

    private Position parseNBT(String nbtString) {
        Position position = null;
        JsonObject jsonObject = new Gson().fromJson(nbtString, JsonObject.class);
        String type = jsonObject.get("type").getAsString();
        if (type.equals("tp")) {
            position = new Position(jsonObject.get("x").getAsInt(),
                    jsonObject.get("y").getAsInt(),
                    jsonObject.get("z").getAsInt());
        }
        return position;
    }
}
