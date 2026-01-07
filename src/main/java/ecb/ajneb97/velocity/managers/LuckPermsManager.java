package ecb.ajneb97.velocity.managers;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

public class LuckPermsManager {

    private LuckPerms luckPerms;

    public LuckPermsManager() {
        try {
            this.luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            // LuckPerms not loaded
            this.luckPerms = null;
        }
    }

    public boolean isLuckPermsLoaded() {
        return luckPerms != null;
    }

    public boolean hasPermission(com.velocitypowered.api.proxy.Player player, String permission) {
        if (!isLuckPermsLoaded()) {
            return player.hasPermission(permission);
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return false;
        }

        return user.getCachedData().getPermissionData(QueryOptions.defaultContextualOptions()).checkPermission(permission).asBoolean();
    }
}
