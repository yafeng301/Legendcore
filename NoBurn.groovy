package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBurnEvent

class NoBurn implements Listener {

    @EventHandler
    void onBurn(BlockBurnEvent e) {
        e.cancelled = true
    }

    static def listener = new NoBurn()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }
}
