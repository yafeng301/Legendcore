package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent

class KeepInventory implements Listener {

    static def listener = new KeepInventory()
    static def proxy = new ConfigProxy("KeepInventory")

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig(map -> {
            map.put("物品保护", true)
            map.put("经验保护", true)
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDeath(PlayerDeathEvent e) {
        if (proxy.config.getBoolean("物品保护")) e.keepInventory = true
        if (proxy.config.getBoolean("经验保护")) e.keepLevel = true
    }
}
