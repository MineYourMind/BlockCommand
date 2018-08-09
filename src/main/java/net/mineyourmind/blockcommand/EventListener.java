package net.mineyourmind.blockcommand;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventListener {

    @Listener
    public void onBlockInteract(InteractBlockEvent event, @First Player player) {
        if (event instanceof InteractBlockEvent.Primary.OffHand || event instanceof InteractBlockEvent.Secondary.OffHand) return;

        BlockType blockType = event.getTargetBlock().getState().getType();

        if (BlockCommand.instance.getConfig().isAllowedBlock(blockType)) {

            event.getTargetBlock().getLocation().ifPresent(location -> {
                BlockCommand.instance.getBlockStorage().get(location).ifPresent(blockObject -> {
                    if (event instanceof InteractBlockEvent.Primary) {
                        if (player.hasPermission("blockcommand.admin")) {
                            if (player.get(Keys.IS_SNEAKING).orElse(false)) {
                                return;
                            }
                            player.sendMessage(Text.of(BlockCommand.PREFIX, TextColors.YELLOW, " Admin Note: Sneak to break the block."));
                        }
                    }
                    event.setCancelled(true);
                    runCommands(blockObject, player);
                });
            });
        }
    }

    // Triggered by blocks which change state like pressure plate, button...
    @Listener
    public void onBlockChange(ChangeBlockEvent.Modify event, @First Player player) {
        BlockSnapshot blockSnapshot = event.getTransactions().get(0).getFinal();
        BlockType blockType = blockSnapshot.getState().getType();

        if (BlockCommand.instance.getConfig().isAllowedBlock(blockType)) {
            blockSnapshot.getLocation().ifPresent(location -> {
                BlockCommand.instance.getBlockStorage().get(location).ifPresent(blockObject -> {
                    runCommands(blockObject, player);
                });
            });
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        BlockSnapshot blockSnapshot = event.getTransactions().get(0).getOriginal();
        BlockType blockType = blockSnapshot.getState().getType();

        if (BlockCommand.instance.getConfig().isAllowedBlock(blockType)) {
            blockSnapshot.getLocation().ifPresent(location -> {
                BlockCommand.instance.getBlockStorage().get(location).ifPresent(blockObject -> {
                    BlockCommand.instance.getBlockStorage().remove(location);
                    event.getCause().first(Player.class).ifPresent(player -> {
                        player.sendMessage(Text.of(BlockCommand.PREFIX, TextColors.GREEN, " Removed all commands from ", blockType, "."));
                    });
                });
            });
        }
    }

    private void runCommands(BlockObject blockObject, Player player) {
        for (String command : blockObject.commands) {
            CommandType type = CommandType.PLAYER;
            Matcher server = Pattern.compile("^<(?:server|srv|s)>\\s?").matcher(command);
            if (server.find()) {
                type = CommandType.SERVER;
                command = server.replaceAll("");
            }
            Matcher message = Pattern.compile("^<(?:message|msg|m)>\\s?").matcher(command);
            if (message.find()) {
                type = CommandType.MESSAGE;
                command = message.replaceAll("");
            }
            Matcher broadcast = Pattern.compile("^<(?:broadcast|bcast|b)>\\s?").matcher(command);
            if (broadcast.find()) {
                type = CommandType.BROADCAST;
                command = broadcast.replaceAll("");
            }

            command = command.replace("<player>", player.getName());
            command = command.replace("<name>", player.getName());
            command = command.replace("<uui>", player.getUniqueId().toString());

            switch (type) {
                case PLAYER:
                    Sponge.getCommandManager().process(player, command);
                    break;
                case SERVER:
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
                    break;
                case MESSAGE:
                    player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(command));
                    break;
                case BROADCAST:
                    Sponge.getServer().getBroadcastChannel().send(TextSerializers.FORMATTING_CODE.deserialize(command));
                    break;
            }
        }
    }
}
