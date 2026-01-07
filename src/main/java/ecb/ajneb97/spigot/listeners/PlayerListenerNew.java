package ecb.ajneb97.spigot.listeners;

import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.spigot.EasyCommandBlocker;
import ecb.ajneb97.spigot.utils.OtherUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.List;
import java.lang.reflect.Method;
import org.bukkit.command.CommandMap;

public class PlayerListenerNew implements Listener {
    private EasyCommandBlocker plugin;
    public PlayerListenerNew(EasyCommandBlocker plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandSend(PlayerCommandSendEvent event){
        Player player = event.getPlayer();

        if(player.isOp() || player.hasPermission("easycommandblocker.bypass.tab")){
            return;
        }

        event.getCommands().clear();

        // Add commands based on their permissions (access CommandMap via reflection)
        try {
            Method getCommandMap = plugin.getServer().getClass().getMethod("getCommandMap");
            CommandMap commandMap = (CommandMap) getCommandMap.invoke(plugin.getServer());
            for (org.bukkit.command.Command command : commandMap.getCommands()) {
                String perm = command.getPermission();
                if (perm == null || plugin.getLuckPermsManager().hasPermission(player, perm)) {
                    event.getCommands().add(command.getName());
                }
            }
        } catch (Exception e) {
            // Fallback: do nothing if command map is not accessible
        }
    }
}
