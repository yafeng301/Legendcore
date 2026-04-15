package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent

class AutoRespawn implements Listener {

    static def listener = new AutoRespawn()

    @EventHandler(priority = EventPriority.MONITOR)
    void onDeath(PlayerDeathEvent e) {
        def entity = e.entity
        new BukkitRunnable() {
            @Override
            void run() {
                entity.spigot().respawn()
            }
        }.runTaskLater(1)
    }

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }
}
