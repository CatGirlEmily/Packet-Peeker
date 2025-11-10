package catgirlemily.peeker;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class PacketCommands {
    private static final List<Class<? extends Packet<?>>> PACKETS = List.of(
        AcknowledgeChunksC2SPacket.class,
        AcknowledgeReconfigurationC2SPacket.class,
        AdvancementTabC2SPacket.class,
        BoatPaddleStateC2SPacket.class,
        BookUpdateC2SPacket.class,
        BundleItemSelectedC2SPacket.class,
        ButtonClickC2SPacket.class,
        ChatCommandSignedC2SPacket.class,
        ChatMessageC2SPacket.class,
        ClickSlotC2SPacket.class,
        ClientCommandC2SPacket.class,
        ClientStatusC2SPacket.class,
        ClientTickEndC2SPacket.class,
        CloseHandledScreenC2SPacket.class,
        CommandExecutionC2SPacket.class,
        CraftRequestC2SPacket.class,
        CreativeInventoryActionC2SPacket.class,
        HandSwingC2SPacket.class,
        JigsawGeneratingC2SPacket.class,
        MessageAcknowledgmentC2SPacket.class,
        PlayerActionC2SPacket.class,
        PlayerInputC2SPacket.class,
        PlayerInteractBlockC2SPacket.class,
        PlayerInteractEntityC2SPacket.class,
        PlayerInteractItemC2SPacket.class,
        PlayerMoveC2SPacket.class,
        PlayerSessionC2SPacket.class,
        QueryBlockNbtC2SPacket.class,
        QueryEntityNbtC2SPacket.class,
        RecipeBookDataC2SPacket.class,
        RecipeCategoryOptionsC2SPacket.class,
        RenameItemC2SPacket.class,
        RequestCommandCompletionsC2SPacket.class,
        SelectMerchantTradeC2SPacket.class,
        SlotChangedStateC2SPacket.class,
        SpectatorTeleportC2SPacket.class,
        TeleportConfirmC2SPacket.class,
        UpdateBeaconC2SPacket.class,
        UpdateCommandBlockC2SPacket.class,
        UpdateCommandBlockMinecartC2SPacket.class,
        UpdateDifficultyC2SPacket.class,
        UpdateDifficultyLockC2SPacket.class,
        UpdateJigsawC2SPacket.class,
        UpdatePlayerAbilitiesC2SPacket.class,
        UpdateSelectedSlotC2SPacket.class,
        UpdateSignC2SPacket.class,
        UpdateStructureBlockC2SPacket.class,
        VehicleMoveC2SPacket.class
    );

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(buildSelectPacketCommand());
            dispatcher.register(buildToggleTypeCommand());
        });
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildSelectPacketCommand() {
        return literal("packetpeek")
                .then(argument("packet", StringArgumentType.word())
                        .suggests((context, builder) -> getSuggestions(builder))
                        .executes(PacketCommands::selectPacket)
                );
    }

private static LiteralArgumentBuilder<FabricClientCommandSource> buildToggleTypeCommand() {
    return literal("packetpeekshowtype")
        .executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PacketPrinter.toggleShowTypes();
            if (client.player != null) {
                client.player.sendMessage(
                    Text.literal(
                        Formatting.AQUA + "ShowTypes toggle: " + 
                        (PacketPrinter.isShowTypes() ? "§a§lON" : "§4§lOFF")
                    ),
                    false
                );
            }
            return 1;
        });
}


    private static CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        for (Class<? extends Packet<?>> packet : PACKETS) {
            String simpleName = packet.getSimpleName();
            if (simpleName.toLowerCase().startsWith(remaining)) builder.suggest(simpleName);
        }
        return builder.buildFuture();
    }

    private static int selectPacket(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        String packetName = StringArgumentType.getString(context, "packet");

        if (PacketPrinter.getCurrentPacket() != null && PacketPrinter.getCurrentPacket().equals(packetName)) {
            PacketPrinter.setCurrentPacket(null);
            if (client.player != null) {
                client.player.sendMessage(
                    Text.literal(Formatting.RED + "Packet tracking disabled."), false
                );
            }
            return 1;
        }

        for (Class<? extends Packet<?>> packetClass : PACKETS) {
            if (packetClass.getSimpleName().equals(packetName)) {
                PacketPrinter.setCurrentPacket(packetName);
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.literal(Formatting.AQUA + "Currently Selected Packet: " + Formatting.GREEN + packetName),
                        false
                    );
                }
                return 1;
            }
        }

        if (client.player != null) {
            client.player.sendMessage(
                Text.literal(Formatting.RED + "Unknown packet: " + packetName), false
            );
        }
        return 1;
    }
}
