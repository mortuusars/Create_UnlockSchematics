package io.github.mortuusars.unlockschematics.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.mortuusars.unlockschematics.UnlockSchematics;
import io.github.mortuusars.unlockschematics.network.ModPackets;
import io.github.mortuusars.unlockschematics.network.packets.UnlockSchematicPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SchematicCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("schematic")
                .requires((commandSourceStack -> commandSourceStack.hasPermission(2)))
                .then(Commands.literal("unlock")
                    .then(Commands.argument("name", StringArgumentType.word())
                        .executes(SchematicCommand::unlock)))
//                .then(Commands.literal("lock")
//                    .then(Commands.argument("name", StringArgumentType.word())
//                        .executes(SchematicCommand::lock)))
        );
    }

//    private static int lock(CommandContext<CommandSourceStack> commandContext) {
//        throw new NotImplementedException();
////        return 0;
//    }

    private static int unlock(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        String schematicFileName = StringArgumentType.getString(commandContext, "name")
                .trim();

        ModPackets.sendToClient(new UnlockSchematicPacket(schematicFileName, true),
                commandContext.getSource().getPlayerOrException());

        return 0;
    }
}
