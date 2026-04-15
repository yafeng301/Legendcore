package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityCombustEvent

class NoCombust implements Listener {

    static def listener = new NoCombust()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onCombust(EntityCombustEvent e) {
        if (e.entity instanceof Player) return

        e.cancelled = true
    }
}
