# BlockCommand


### Description
BlockCommand is a plugin similiar to the [ServerSigns](https://dev.bukkit.org/projects/serversigns) bukkit plugin which allows to add commands to blocks which get executed when the player interacts with the block.

### Features
- Run any command as player or server
- Send or broadcast a message
- Supports all blocks and pressure plates (Configurable, default is signs)

### Commands
- `/bcmd add <command>` - Adds a command to the block you are looking at
- `/bcmd list` - Lists the commands of the block you are looking at

### Permissions
- `blockcommand.command.use` - Allows command usage
- `blockcommand.admin` - Allows breaking blocks with commands

### Placeholders
| Placeholder           | Description                                          |
| --------------------- | ---------------------------------------------------- |
| `<player|name>`       | Name of the interacting player                       |
| `<uuid>`              | UUID of the interacting player                       |
| `<server|srv|s>`      | Defines the command to be run as server/console      |
| `<message|msg|m>`     | Defines it to be a message send to the player        |
| `<broadcast|bcast|b>` | Defines it to be a message send to all players       |

### Examples
`/bcmd add <srv> give <player> stone`  
Runs the give command as server and replaces `<player>` with the name of the interacting player.

`/bcmd add help`  
Runs the help command as player.

`/bcmd add <msg> Hello <player>`  
Sends a message to the interacting player.
