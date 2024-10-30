package dev.padrewin.coldbroadcast.commands;

import java.util.Collections;
import java.util.List;
import dev.padrewin.coldbroadcast.ColdBroadcast;
import dev.padrewin.coldbroadcast.manager.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

public abstract class BaseCommand implements NamedExecutor {

    private final String name;
    private final CommandManager.CommandAliases aliases;

    public BaseCommand(String name, CommandManager.CommandAliases aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    /**
     * Execution method for the command.
     *
     * @param plugin ColdBroadcast instance.
     * @param sender Sender of the command.
     * @param args Command arguments.
     */
    public abstract void execute(ColdBroadcast plugin, CommandSender sender, String[] args);

    /**
     * Tab completion method for the command.
     *
     * @param plugin ColdBroadcast instance.
     * @param sender Sender of the command.
     * @param args Command arguments.
     */
    public abstract List<String> tabComplete(ColdBroadcast plugin, CommandSender sender, String[] args);

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = this.aliases.get();
        if (aliases.isEmpty()) {
            return Collections.singletonList(this.name);
        } else {
            return aliases;
        }
    }

    @Override
    public boolean hasPermission(Permissible permissible) {
        return permissible.hasPermission("coldbroadcast." + this.name);
    }

}
