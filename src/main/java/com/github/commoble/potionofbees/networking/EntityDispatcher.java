package com.github.commoble.potionofbees.networking;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.PacketByteBuf;

import java.util.Optional;

//https://gist.github.com/ChloeDawn/adbb88a72c248c33ee65a5b64f22000b#file-packets-java
public final class EntityDispatcher {
    private EntityDispatcher() {
    }

    public static void spawnFrom(final PacketContext ctx, final PacketByteBuf bytes) {
        Packets.readFrom(bytes, EntitySpawnS2CPacket::new).ifPresent(packet ->
                ctx.getTaskQueue().execute(() -> {
                    final ClientWorld world = MinecraftClient.getInstance().world;
                    Optional.ofNullable(packet.getEntityTypeId().create(world)).ifPresent(entity -> {
                        entity.updateTrackedPosition(packet.getX(), packet.getY(), packet.getZ());
                        entity.setVelocity(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
                        entity.pitch = (float) (packet.getPitch() * 360) / 256.0F;
                        entity.yaw = (float) (packet.getYaw() * 360) / 256.0F;
                        entity.setEntityId(packet.getId());
                        entity.setUuid(packet.getUuid());
                        world.addEntity(packet.getId(), entity);
                    });
                }));
    }
}