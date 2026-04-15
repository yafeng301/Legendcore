package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.event.inventory.AnvilCompleteEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

class NoAnvil implements Listener {

    static def listener = new NoAnvil()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onAnvil(AnvilCompleteEvent e) {
        e.cancelled = true
    }
}
