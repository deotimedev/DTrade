package org.dtrade.packets;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PacketHandler {

    private final Map<Class<? extends Packet<?>>, Set<PacketReadSubscriber<? extends Packet<?>>>> readers = new HashMap<>();
    private final Map<Class<? extends Packet<?>>, Set<PacketWriteSubscriber<? extends Packet<?>>>> writers = new HashMap<>();

    public PacketHandler(@NotNull Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(packetListener, plugin);
        Bukkit.getOnlinePlayers().forEach(this::registerPlayer);
    }

    public <T extends Packet<?>> void subscribe(Class<T> packet, PacketReadSubscriber<T> subscriber) {
        readers.computeIfAbsent(packet, (c) -> new HashSet<>()).add(subscriber);
    }

    public <T extends Packet<?>> void subscribe(Class<T> packet, PacketWriteSubscriber<T> subscriber) {
        writers.computeIfAbsent(packet, (c) -> new HashSet<>()).add(subscriber);
    }

    public interface PacketReadSubscriber<T extends Packet<?>> {
        void onRead(Player player, T packet);
    }

    public interface PacketWriteSubscriber<T extends Packet<?>> {
        void onWrite(Player player, T packet);
    }

    private final Listener packetListener = new Listener() {
        @EventHandler
        private void onJoin(PlayerJoinEvent event) {
            registerPlayer(event.getPlayer());
        }
    };

    public void registerPlayer(Player player) {
        ((CraftPlayer) player).getHandle().b.a.m.pipeline().addBefore("packet_handler", "dpackethandler", new ChannelDuplexHandler() {
            @Override @SuppressWarnings("unchecked")
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                Packet<?> packet = (Packet<?>) msg;
                Class<?> clazz = packet.getClass();
                if(readers.containsKey(clazz)) readers.get(clazz).forEach(s -> ((PacketReadSubscriber) s).onRead(player, packet));
                super.channelRead(ctx, msg);
            }

            @Override @SuppressWarnings("unchecked")
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                Packet<?> packet = (Packet<?>) msg;
                Class<?> clazz = packet.getClass();
                if(writers.containsKey(clazz)) writers.get(clazz).forEach(s -> ((PacketWriteSubscriber) s).onWrite(player, packet));
                super.write(ctx, msg, promise);
            }
        });
    }

}
