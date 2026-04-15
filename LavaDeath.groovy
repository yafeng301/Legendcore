package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

class LavaDeath implements Listener {

    static def listener = new LavaDeath()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDamage(EntityDamageEvent e) {
        if (e.cancelled) return

        if (e.cause != EntityDamageEvent.DamageCause.LAVA) return

        def entity = e.entity
        if (entity instanceof Player) {
            entity.health = 0
            e.cancelled = true
        }
    }
}
