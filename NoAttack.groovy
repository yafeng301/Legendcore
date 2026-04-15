package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class NoAttack implements Listener {

    static def listener = new NoAttack()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onDamageLowest(EntityDamageByEntityEvent e) {
        if (e.cause == EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK ||
                e.cause == EntityDamageByEntityEvent.DamageCause.ENTITY_SWEEP_ATTACK)
            if (e.damager instanceof Player) e.cancelled = true
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDamageMonitor(EntityDamageByEntityEvent e) {
        if (e.cancelled) return

        if (e.cause == EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK ||
                e.cause == EntityDamageByEntityEvent.DamageCause.ENTITY_SWEEP_ATTACK)
            if (e.damager instanceof Player) e.cancelled = true
    }
}
