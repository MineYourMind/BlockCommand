package net.mineyourmind.blockcommand;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockStorage {

    @Inject
    private ConfigurationLoader<?> loader;
    @Inject
    private ConfigurationNode node;

    @Setting
    private Map<String, BlockObject> blockLocations = new HashMap<>();

    public BlockStorage(ConfigurationLoader<?> configurationLoader) {
        this.loader = configurationLoader;
        try {
            // create mapper
            ObjectMapper<BlockStorage>.BoundInstance mapper = loader.getDefaultOptions().getObjectMapperFactory()
                    .getMapper(BlockStorage.class).bind(this);

            // load file
            node = loader.load();

            // populate hierarchy
            mapper.populate(node);

            // save transferred default values if any
            loader.save(node);
        } catch (ObjectMappingException | IOException e) {
            // terminate forcefully
            throw new RuntimeException("Unable to load block storage", e);
        }
    }

    public void save() {
        BlockCommand.instance.getLogger().info("Saving block storage");
        try {
            ObjectMapper<BlockStorage>.BoundInstance mapper = loader.getDefaultOptions().getObjectMapperFactory()
                    .getMapper(BlockStorage.class).bind(this);

            mapper.serialize(node);

            loader.save(node);
        } catch (ObjectMappingException | IOException e) {
            BlockCommand.instance.getLogger().error("Couldn't save block storage", e);
        }
    }

    public void add(Location<World> location, String command) {
        BlockObject blockObject = blockLocations.get(location.toString());
        if (blockObject != null) {
            blockObject.commands.add(command);
        } else {
            blockLocations.put(location.toString(), new BlockObject(command));
        }
    }

    public void remove(Location<World> location) {
        blockLocations.remove(location.toString());
    }

    public Optional<BlockObject> get(Location<World> location) {
        BlockObject blockObject = blockLocations.get(location.toString());
        if (blockObject != null) {
            return Optional.of(blockObject);
        }
        return Optional.empty();
    }
}
