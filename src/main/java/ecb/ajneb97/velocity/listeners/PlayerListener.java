package ecb.ajneb97.velocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.proxy.Player;
import ecb.ajneb97.core.managers.CommandsManager;
import ecb.ajneb97.core.model.internal.UseCommandResult;
import ecb.ajneb97.velocity.EasyCommandBlocker;
import ecb.ajneb97.velocity.api.CommandBlockedEvent;
import ecb.ajneb97.velocity.utils.ActionsUtils;
import ecb.ajneb97.velocity.utils.OtherUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collection;

public class PlayerListener {

    private EasyCommandBlocker plugin;
    private static final Map<String, String> COMMAND_PERMISSIONS = new HashMap<>();

    static {
        COMMAND_PERMISSIONS.put("velocity", "velocity.command.velocity");
        COMMAND_PERMISSIONS.put("server", "velocity.command.server");
        COMMAND_PERMISSIONS.put("send", "velocity.command.send");
        COMMAND_PERMISSIONS.put("find", "velocity.command.find");
        COMMAND_PERMISSIONS.put("glist", "velocity.command.glist");
        COMMAND_PERMISSIONS.put("greload", "velocity.command.greload");
        COMMAND_PERMISSIONS.put("plugins", "velocity.command.plugins");
        COMMAND_PERMISSIONS.put("hear", "velocity.command.hear");
        COMMAND_PERMISSIONS.put("info", "velocity.command.info");
        // Add more if needed
    }

    public PlayerListener(EasyCommandBlocker plugin){
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void executeCommand(CommandExecuteEvent event) {
        if(event.getCommandSource() instanceof Player){
            Player player = (Player) event.getCommandSource();
            String command = "/"+event.getCommand();

            boolean isProxyCommand = plugin.getServer().getCommandManager().hasCommand(event.getCommand().split(" ")[0].toLowerCase());
            if(!isProxyCommand){
                return;
            }

            if(player.hasPermission("easycommandblocker.bypass.commands")){
                return;
            }

            CommandsManager commandsManager = plugin.getCommandsManager();
            UseCommandResult result = commandsManager.useCommand(command);
            if(!result.isCanUseCommand()){
                CommandBlockedEvent commandBlockedEvent = new CommandBlockedEvent(player,result.getFoundCommand(),command);
                plugin.getServer().getEventManager().fire(commandBlockedEvent).thenAccept((finalEvent) -> {
                    if(!finalEvent.getResult().isAllowed()){
                        return;
                    }

                    List<String> actions = commandsManager.getActionsForCustomCommand(result.getFoundCommand());
                    if(actions == null){
                        actions = commandsManager.getBlockCommandDefaultActions();
                    }
                    ActionsUtils.executeActions(actions,player);
                    event.setResult(CommandExecuteEvent.CommandResult.denied());
                });
            }
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onTab(PlayerAvailableCommandsEvent event){
        Player player = event.getPlayer();
        if(player.hasPermission("easycommandblocker.bypass.tab")){
            return;
        }

        Collection<String> proxyCommands = plugin.getServer().getCommandManager().getAliases();

        event.getRootNode().getChildren().removeIf((child -> {
            String command = child.getName().toLowerCase();
            if (!proxyCommands.contains(command)) {
                return false; // Not a proxy command, keep it
            }
            String perm = COMMAND_PERMISSIONS.get(command);
            if (perm == null) {
                // If no permission defined, assume it's allowed or check default
                return false; // Keep it
            }
            return !plugin.getLuckPermsManager().hasPermission(player, perm);
        }));
    }
}
