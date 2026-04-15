package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class Invincible implements Listener {

    static def listener = new Invincible()
    static def proxy = new ConfigProxy("Invincible")
    static long time
    static Map<String, Long> map = new HashMap<>()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        proxy.loadConfig((map) -> map.put("无敌时间(单位: 毫秒)", 1000))

        time = proxy.config.getLong("无敌时间(单位: 毫秒)")
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        map.put(e.player.name, System.currentTimeMillis() + time)
    }

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        map.remove(e.player.name)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDamage(EntityDamageEvent e) {
        if (e.cancelled) return

        if (e.entity instanceof Player) {
            def time = map.get(e.entity.name)
            if (time == null) return

            if (System.currentTimeMillis() > time) map.remove(e.entity.name)
            else e.cancelled = true
        }
    }
}
