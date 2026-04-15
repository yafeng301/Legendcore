package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitTask

class HealthScale implements Listener { //血量压缩

    static ConfigProxy proxy = new ConfigProxy("HealthScale")
    static def listener = new HealthScale()
    static BukkitTask task
    static double scale

    static void trigger(Player player) {
        if (player.dead) return
        if (!player.valid) return
        if (!player.healthScaled) player.healthScaled = true
        if (player.healthScale != scale) player.healthScale = scale
    }

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        scale = proxy.config.getDouble("scale")
        if (scale <= 0) scale = 40
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onLogin(PlayerJoinEvent e) {
        trigger(e.player)
    }

    static void onGroovyRegister() {
        listener.register()
        task = new BukkitRunnable() {
            @Override
            void run() {
                for (def player in Bukkit.onlinePlayers) trigger(player)
            }
        }.runTaskTimer(0, 20)
        proxy.loadConfig(map -> map.put("scale", 40D))
        scale = proxy.config.getDouble("scale")
        if (scale <= 0) scale = 40
    }

    static void onGroovyUnregister() {
        listener.unregister()
        task.cancel()
    }
}
