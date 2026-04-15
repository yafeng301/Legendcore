package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

class HealthRemind implements Listener {

    static def listener = new HealthRemind()
    static def proxy = new ConfigProxy("HealthRemind")

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig((map) -> {
            map.put("50提醒", "血量小于50%")
            map.put("10提醒", "血量小于10%")
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDamage(EntityDamageEvent e) {
        if (e.cancelled) return

        def entity = e.entity
        if (entity instanceof Player) {
            if (e.finalDamage <= 0) return

            def health = entity.health - e.finalDamage
            if (health <= 0) return

            def proportion = health / entity.maxHealth

            if (proportion <= 0.1) {
                entity.sendMessage(CoreAPI.format(proxy.config.getString("10提醒")))
            } else if (proportion <= 0.5) {
                entity.sendMessage(CoreAPI.format(proxy.config.getString("50提醒")))
            }
        }
    }
}
