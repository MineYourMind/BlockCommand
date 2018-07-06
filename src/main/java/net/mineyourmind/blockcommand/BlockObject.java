package net.mineyourmind.blockcommand;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ConfigSerializable
public class BlockObject {

    @Setting
    public List<String> commands;

    public BlockObject() {
        this.commands = new ArrayList<>();
    }

    public BlockObject(List<String> commands) {
        this.commands = commands;
    }

    public BlockObject(String command) {
        this.commands = new ArrayList<>(Collections.singletonList(command));
    }
}
