package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent

class FixedHand implements Listener {

    static def listener = new FixedHand()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onJoin(PlayerJoinEvent e) {
        e.player.inventory.heldItemSlot = 0
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onHeld(PlayerItemHeldEvent e) {
        if (e.newSlot != 0) e.player.inventory.heldItemSlot = 0
    }
}
