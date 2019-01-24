package net.mineyourmind.blockcommand;

import com.google.inject.Inject;
import net.mineyourmind.blockcommand.commands.AddCommand;
import net.mineyourmind.blockcommand.commands.ListCommand;
import net.mineyourmind.blockcommand.commands.MainCommand;
import net.mineyourmind.blockcommand.config.Config;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.nio.file.Path;


@Plugin(
        id = "blockcommand",
        name = "BlockCommand",
        description = "Run commands during block interactions",
        authors = {
                "SirWill"
        }
)
public class BlockCommand {

    private Logger logger;
    private PluginContainer plugin;
    private Path configPath;

    public static BlockCommand instance;
    public static Text PREFIX = Text.of(TextColors.GOLD, "[BlockCommand]", TextColors.RESET);

    private Config config;
    private BlockStorage blockStorage;

    @Inject
    public BlockCommand(Logger logger, PluginContainer plugin, @ConfigDir(sharedRoot = false) Path configDir) {
        this.logger = logger;
        this.plugin = plugin;
        this.configPath = configDir;

        configDir.toFile().mkdirs();
    }

    @Listener
    public void onEnable(GamePreInitializationEvent event) {
        instance = this;
    }

    @Listener
    public void onStart(GameStartingServerEvent event) {
        config = new Config(HoconConfigurationLoader.builder().setPath(configPath.resolve("config.conf")).build());
        blockStorage = new BlockStorage(HoconConfigurationLoader.builder().setPath(configPath.resolve("blockStorage.conf")).build());

        CommandSpec addCommand = CommandSpec.builder()
                .description(Text.of("Add a command to the block you are looking at."))
                .arguments(GenericArguments.optional(
                        GenericArguments.remainingJoinedStrings(Text.of("command"))
                ))
                .executor(new AddCommand())
                .build();

        CommandSpec listCommand = CommandSpec.builder()
                .description(Text.of("Print the assigned commands of the block you are looking at."))
                .executor(new ListCommand())
                .build();

        CommandSpec mainCommand = CommandSpec.builder()
                .description(Text.of("Shows command help."))
                .permission("blockcommand.command.use")
                .child(addCommand, "add")
                .child(listCommand, "list")
                .executor(new MainCommand())
                .build();
        Sponge.getCommandManager().register(plugin, mainCommand, "blockcommand", "bcmd");

        Sponge.getEventManager().registerListeners(this, new EventListener());
    }

    @Listener
    public void onStop(GameStoppingServerEvent event) {
        blockStorage.save();
    }

    public Logger getLogger() {
        return logger;
    }

    public Config getConfig() {
        return config;
    }

    public BlockStorage getBlockStorage() {
        return blockStorage;
    }
}
