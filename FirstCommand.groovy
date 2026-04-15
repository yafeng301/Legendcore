package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent

class FirstCommand implements Listener {

    static def listener = new FirstCommand()
    static ConfigProxy proxy = new ConfigProxy("FirstCommand")

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        if (e.player.lastPlayed != 0) return
        proxy.config.getStringList("首次进入服务器执行命令").forEach {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("<p>", e.player.name))
        }
    }

    static void onGroovyRegister() {
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("首次进入服务器执行命令", ["bc 欢迎玩家 <p> 首次进入我们的服务器", "eco give <p> 100"])
            }
        })
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }
}
