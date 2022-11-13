package io.github.mortuusars.unlockschematics.network.packets;

import io.github.mortuusars.unlockschematics.UnlockSchematics;
import net.minecraftforge.network.NetworkEvent;

public class UnlockSchematicClientPacketHandler {

    public static void handle(UnlockSchematicPacket unlockSchematicPacket, NetworkEvent.Context context) {
        UnlockSchematics.unlock(unlockSchematicPacket.schematicName, unlockSchematicPacket.replaceExistingFile);
    }
}
