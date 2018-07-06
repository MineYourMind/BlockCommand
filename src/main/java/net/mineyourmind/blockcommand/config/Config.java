package net.mineyourmind.blockcommand.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {

    @Setting(comment = "List of blocks which can be used with this plugin.")
    private List<BlockType> blocks = new ArrayList<>(Arrays.asList(BlockTypes.WALL_SIGN, BlockTypes.STANDING_SIGN));

    public Config(ConfigurationLoader<?> loader) {
        try {
            // create mapper
            ObjectMapper<Config>.BoundInstance mapper = loader.getDefaultOptions().getObjectMapperFactory()
                    .getMapper(Config.class).bind(this);

            // load file
            ConfigurationNode node = loader.load();

            // populate hierarchy
            mapper.populate(node);

            // save transferred default values if any
            loader.save(node);
        } catch (ObjectMappingException | IOException e) {
            // terminate forcefully
            throw new RuntimeException("Unable to load configuration", e);
        }
    }

    public boolean isAllowedBlock(BlockType blockType) {
        return blocks.contains(blockType);
    }
}
