package net.mineyourmind.blockcommand.commands;

import net.mineyourmind.blockcommand.BlockCommand;
import net.mineyourmind.blockcommand.BlockObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
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
                Optional<BlockObject> blockObject = BlockCommand.instance.getBlockStorage().get(location);
                if (blockObject.isPresent()) {
                    List<Text> textList = new ArrayList<>();
                    int num = 1;
                    for (String command : blockObject.get().commands) {
                        textList.add(Text.of(TextColors.GRAY, " ", num, " ", TextColors.YELLOW, command));
                        num++;
                    }
                    Sponge.getServiceManager().provide(PaginationService.class).ifPresent(paginationService -> {
                        paginationService.builder()
                                .title(Text.of(TextColors.GOLD, BlockCommand.PREFIX))
                                .padding(Text.of(TextColors.WHITE, "-"))
                                .header(Text.of(TextColors.GRAY, "Block: ", TextColors.WHITE, blockType, TextColors.GRAY, " Location: ", TextColors.WHITE, location.getBlockPosition().toString(),
                                        TextColors.GOLD, "\nCommands:"))
                                .contents(textList)
                                .sendTo(player);
                    });
                } else {
                    src.sendMessage(Text.of(TextColors.RED, "The block ", blockType, " doesn't have any commands"));
                }
            } else {
                src.sendMessage(Text.of(BlockCommand.PREFIX, TextColors.RED, " The block ", blockType, " is not listed in the config!"));
            }
        } else {
            src.sendMessage(Text.of(BlockCommand.PREFIX, TextColors.RED, " Couldn't find any block in the range of 10!"));
        }

        return CommandResult.success();
    }
}
