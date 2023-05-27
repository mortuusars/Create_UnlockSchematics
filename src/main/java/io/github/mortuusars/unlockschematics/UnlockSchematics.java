package io.github.mortuusars.unlockschematics;

import com.mojang.logging.LogUtils;
import io.github.mortuusars.unlockschematics.commands.SchematicCommand;
import io.github.mortuusars.unlockschematics.network.ModPackets;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Mod(UnlockSchematics.MODID)
public class UnlockSchematics
{
    public static final String MODID = "unlockschematics";

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public UnlockSchematics()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
//        // Register the enqueueIMC method for modloading
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
//        // Register the processIMC method for modloading
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
//        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    // Should be called on the client.
    public static void unlock(String schematic, boolean replaceExisting){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null){
            LOGGER.error("Player should not be null.");
            return;
        }

        try {

            String schematicFileName = schematic.endsWith(".nbt") ? schematic : schematic + ".nbt";
            String schematicPrettyName = WordUtils.capitalize(schematic.replace(".nbt", "")
                                                  .replace('_', ' '));

            Path configPath = Paths.get("schematics/");
            Path lockedConfigPath = Paths.get("schematics/locked/");

            Files.createDirectories(lockedConfigPath);

            if (!Files.isReadable(lockedConfigPath)) {
                player.displayClientMessage(Component.translatable("message.unlockschematics.no_folder_permissions")
                        .withStyle(ChatFormatting.DARK_RED), false);
                return;
            }

//            if (!Files.exists(lockedConfigPath)) {
//                player.sendMessage(Component.translatable("message.unlockschematics.no_locked_schematics_available")
//                        .withStyle(ChatFormatting.DARK_RED), false);
//                return;
//            }

            Path schematicFilePath = lockedConfigPath.resolve(schematicFileName);
            Path unlockedSchematicFilePath = configPath.resolve(schematicFileName);

            if (Files.exists(unlockedSchematicFilePath)) {
                player.displayClientMessage(Component.translatable("message.unlockschematics.schematic_already_unlocked", schematicPrettyName), false);
                return;
            }

            if (!Files.exists(schematicFilePath)) {
                player.displayClientMessage(Component.translatable("message.unlockschematics.schematic_does_not_exist", schematic)
                        .withStyle(ChatFormatting.DARK_RED), false);
                return;
            }

            Files.copy(schematicFilePath, unlockedSchematicFilePath, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(schematicFilePath);

            player.displayClientMessage(Component.translatable("message.unlockschematics.schematic_unlocked", schematicPrettyName)
                    .withStyle(ChatFormatting.DARK_GREEN), false);
        }
        catch (IOException e) {
            LOGGER.error("Failed to unlock schematic: {}\n{}", schematic, e.toString());

            player.displayClientMessage(Component.translatable("message.unlockschematics.failed_to_unlock")
                    .withStyle(ChatFormatting.DARK_RED), false);
        }
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        SchematicCommand.register(event.getDispatcher());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(ModPackets::register);
    }
}
