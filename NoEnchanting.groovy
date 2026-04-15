package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.enchantment.EnchantItemEvent

class NoEnchanting implements Listener {

    static def listener = new NoEnchanting()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onEnchant(EnchantItemEvent e) {
        e.cancelled = true
    }
}
