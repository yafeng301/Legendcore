package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityExplodeEvent

class NoBoom implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    void onBoom(EntityExplodeEvent e) {
        e.blockList().clear()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onBoom(BlockExplodeEvent e) {
        e.blockList().clear()
    }

    static def listener = new NoBoom()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }
}
