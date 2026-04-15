package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

class ForceGameMode implements Listener {

    static def listener = new ForceGameMode()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        if (e.player.gameMode != GameMode.SURVIVAL) e.player.gameMode = GameMode.SURVIVAL
    }
}
