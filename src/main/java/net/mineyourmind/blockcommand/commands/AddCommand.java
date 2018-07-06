package net.mineyourmind.blockcommand.commands;

import net.mineyourmind.blockcommand.BlockCommand;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class AddCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String command = args.<String>getOne("command").get();

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of(BlockCommand.PREFIX, TextColors.RED, " This command can only be executed as player!"));
            return CommandResult.success();
        }
        Player player = (Player) src;

        Optional<BlockRayHit<World>> blockRay = BlockRay.from(player).
                stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).
                distanceLimit(10).build().end();

        if (blockRay.isPresent()) {
            Location<World> location = blockRay.get().getLocation();
            BlockType blockType = location.getBlockType();
            if (BlockCommand.instance.getConfig().isAllowedBlock(blockType)) {

                BlockCommand.instance.getBlockStorage().add(location, command);
                src.sendMessage(Text.of(BlockCommand.PREFIX, TextColors.GREEN, " Successfully added command to ", blockType, "."));
            } else {
                src.sendMessage(Text.of(BlockCommand.PREFIX, TextColors.RED, " The block ", blockType, " is not listed in the config!"));
            }
        } else {
            src.sendMessage(Text.of(BlockCommand.PREFIX, TextColors.RED, " Couldn't find any block in the range of 10!"));
        }

        return CommandResult.success();
    }
}
