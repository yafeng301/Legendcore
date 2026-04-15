package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderHook
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class LoginMessage implements Listener {

    static def proxy = new ConfigProxy("LoginMessage")
    static def listener = new LoginMessage()

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig((map) -> {
            map.put("进入消息", "&f[&a+&f] &b%player_name%")
            map.put("退出消息", "&f[&c-&f] &b%player_name%")
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onJoin(PlayerJoinEvent e) {
        e.joinMessage = PlaceholderHook.setPlaceholders(e.player, CoreAPI.format(proxy.config.getString("进入消息")))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onQuit(PlayerQuitEvent e) {
        e.quitMessage = PlaceholderHook.setPlaceholders(e.player, CoreAPI.format(proxy.config.getString("退出消息")))
    }
}
