package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity

class EveryoneGetsHorny extends BukkitRunnable {

    static def task = new EveryoneGetsHorny()

    static void onGroovyRegister() {
        task.runTaskTimer(0, 20)
    }

    static void onGroovyUnregister() {
        task.cancel()
    }

    @Override
    void run() {
        Bukkit.worlds.forEach { it.entities.forEach {
            if (it instanceof LivingEntity) {
                it.fireTicks = 30
            }
        }}
    }
}
