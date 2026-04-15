package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class UltimateOptimization implements Listener {

    static def listener = new UltimateOptimization()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    static void onLogin(AsyncPlayerPreLoginEvent e) {
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "[UltimateOptimization]You are a useless consumer of resources！")
    }
}
