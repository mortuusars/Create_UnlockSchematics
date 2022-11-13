package io.github.mortuusars.unlockschematics.network.packets;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UnlockSchematicPacket {
    public final String schematicName;
    public final boolean replaceExistingFile;

    public UnlockSchematicPacket(String schematicName, boolean replaceExistingFile) {
        this.schematicName = schematicName;
        this.replaceExistingFile = replaceExistingFile;
    }

    public static UnlockSchematicPacket fromBuffer(FriendlyByteBuf friendlyByteBuf) {
        try {
            return new UnlockSchematicPacket(friendlyByteBuf.readUtf(), friendlyByteBuf.readBoolean());
        }
        catch (Exception e) {
            LogUtils.getLogger().error("Failed to create SchematicInfoPacket from buffer:\n{}", e.toString());
            return new UnlockSchematicPacket("", false);
        }
    }

    public void toBuffer(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(schematicName);
        friendlyByteBuf.writeBoolean(replaceExistingFile);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> UnlockSchematicClientPacketHandler.handle(this, contextSupplier.get()));
        });

        return true;
    }
}
