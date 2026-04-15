package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.scheduler.BukkitTask

class TimeStop extends BukkitRunnable {

    static BukkitTask task
    static def proxy = new ConfigProxy("TimeStop")

    long tick = 0

    @Override
    void run() {
        tick++

        if (tick >= proxy.config.getInt("每隔多久刷新时间(1tick=0.05秒)")) {
            tick = 0

            for (String name : proxy.config.getConfigurationSection("世界列表").getKeys(false)) {
                def world = Bukkit.getWorld(name)
                if (world == null) continue
                world.setTime(proxy.config.getLong("世界列表." + name))
            }

        }
    }

    static void onGroovyRegister() {
        task = new TimeStop().runTaskTimer(0, 1)
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("每隔多久刷新时间(1tick=0.05秒)", 20)
                map.put("世界列表.world", 12000)
            }
        })
    }

    static void onGroovyUnregister() {
        task.cancel()
    }
}
