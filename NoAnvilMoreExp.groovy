package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.PrepareAnvilEvent

class NoAnvilMoreExp implements Listener {

    static def listener = new NoAnvilMoreExp()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onAnvil(PrepareAnvilEvent e) {
        e.inventory.maximumRepairCost = e.inventory.repairCost + 1
    }
}
