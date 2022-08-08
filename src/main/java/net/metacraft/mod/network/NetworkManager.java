package net.metacraft.mod.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.metacraft.mod.network.c2s.C2SPacketType;
import net.metacraft.mod.network.c2s.ScreenEntitySpawnC2SPacket;
import net.metacraft.mod.network.s2c.*;
import net.metacraft.mod.utils.Constants;
import net.minecraft.util.Identifier;

public enum NetworkManager {
    /**
     * INSTANCE;
     */
    INSTANCE;

    private Identifier SERVER_PLAYER_LOGIN = new Identifier(Constants.MOD_ID, "player_login");

    private C2SPacketType<ScreenEntitySpawnC2SPacket> clientScreenSpawnPacket;

    private S2CPacketType<MetaPaintingSpawnS2CPacket> serverCreateMetaPaintingPacket;

    private S2CPacketType<MetaShowFlatSpawnS2CPacket> serverCreateMetaShowFlatPacket;

    private S2CPacketType<TicketValidNotifyS2CPacket> serverTicketValidNotifyPacket;

    public void registerC2SListeners() {
        System.out.println("registerC2SListeners start");
        clientScreenSpawnPacket = NetworkHandler.clientToServer(ScreenEntitySpawnC2SPacket.ID, ScreenEntitySpawnC2SPacket::new);
    }

    public void registerS2CListeners() {
        serverCreateMetaPaintingPacket = NetworkHandler.serverToClient(new Identifier(Constants.MOD_ID, "meta_painting"), MetaPaintingSpawnS2CPacket::new);
        serverCreateMetaShowFlatPacket = NetworkHandler.serverToClient(new Identifier(Constants.MOD_ID, "meta_showflat"), MetaShowFlatSpawnS2CPacket::new);
        serverTicketValidNotifyPacket = NetworkHandler.serverToClient(new Identifier(Constants.MOD_ID, "meta_ticketnotify"), TicketValidNotifyS2CPacket::new);
    }

    public C2SPacketType<ScreenEntitySpawnC2SPacket> getClientScreenSpawnPacket() {
        return clientScreenSpawnPacket;
    }

    public S2CPacketType<MetaPaintingSpawnS2CPacket> getServerCreateMetaPaintingPacket() {
        return serverCreateMetaPaintingPacket;
    }

    public S2CPacketType<MetaShowFlatSpawnS2CPacket> getServerCreateMetaShowFlatPacket() {
        return serverCreateMetaShowFlatPacket;
    }

    public S2CPacketType<TicketValidNotifyS2CPacket> getServerTicketValidNotifyPacket() {
        return serverTicketValidNotifyPacket;
    }

    public void bootstrap() {
        NetworkHandler.serverToClient(SERVER_PLAYER_LOGIN, PlayerLoginS2CPacket::new);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            sender.sendPacket(SERVER_PLAYER_LOGIN, new PlayerLoginS2CPacket(handler.player).toBuffer());
        });
    }
}
