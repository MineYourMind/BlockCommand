package net.mineyourmind.blockcommand.commands;

import net.mineyourmind.blockcommand.BlockCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        List<Text> textList = new ArrayList<>();

        textList.add(Text.of(TextColors.YELLOW, "/bcmd list", TextColors.WHITE, " - ", TextColors.GRAY, "Print the assigned commands of the block you are looking at."));
        textList.add(Text.of(TextColors.YELLOW, "/bcmd add", TextColors.RED, " [command]", TextColors.WHITE, " - ", TextColors.GRAY, "Add a command to the block you are looking at."));

        Sponge.getServiceManager().provide(PaginationService.class).ifPresent(paginationService -> {
            paginationService.builder()
                    .title(Text.of(TextColors.GOLD, BlockCommand.PREFIX))
                    .padding(Text.of(TextColors.WHITE, "-"))
                    .contents(textList)
                    .sendTo(src);
        });
        return CommandResult.success();
    }
}
