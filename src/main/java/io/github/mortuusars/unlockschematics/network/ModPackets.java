package io.github.mortuusars.unlockschematics.network;

import io.github.mortuusars.unlockschematics.UnlockSchematics;
import io.github.mortuusars.unlockschematics.network.packets.UnlockSchematicPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPackets {
    private static final String PROTOCOL_VERSION = "1";
    private static int id = 0;

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(UnlockSchematics.MODID, "packets"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);;


    public static void register() {
        CHANNEL.messageBuilder(UnlockSchematicPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(UnlockSchematicPacket::toBuffer)
                .decoder(UnlockSchematicPacket::fromBuffer)
                .consumer(UnlockSchematicPacket::handle)
                .add();
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
