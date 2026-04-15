package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import cn.mcdcs.legendcore.api.modular.Modular
import cn.mcdcs.legendcore.api.wrapper.map.MapWrapper
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

class JoinCommand implements Listener {

    static def listener = new JoinCommand()
    static def proxy = new ConfigProxy("JoinCommand")
    static Modular module = null

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig(map -> {
            map.put("入服执行模块", ["[message]&a 你好欢迎进入服务器"])
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        MapWrapper wrapper = MapWrapper.of(proxy.config)

        module = Modular.of(wrapper.getStringList("入服执行模块"))
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        if (module == null) return

        module.executeExpression(e.player, new HashMap<String, String>())
    }
}
