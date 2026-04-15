package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.TNTPrimed
import org.bukkit.scheduler.BukkitTask

class TNTTime extends BukkitRunnable {

    static def runnable = new TNTTime()
    static BukkitTask task
    static Set<TNTPrimed> tnt = new HashSet<>()

    static void onGroovyRegister() {
        runnable.runTaskTimerAsynchronously(0, 1)

        task = new BukkitRunnable() {
            @Override
            void run() {
                if (tnt.isEmpty()) return

                Iterator<TNTPrimed> iterator = tnt.iterator()
                while (iterator.hasNext()) {
                    TNTPrimed next = iterator.next()
                    if (next.dead || !next.valid) {
                        iterator.remove()
                        continue
                    }

                    if (next.fuseTicks <= 0) {
                        iterator.remove()
                        continue
                    }

                    next.customName = (next.fuseTicks - 1) * 0.05 + ""
                }
            }
        }.runTaskTimer(0, 1)
    }

    static void onGroovyUnregister() {
        runnable.cancel()
        task.cancel()
    }

    @Override
    void run() {
        for (World world : new ArrayList<>(Bukkit.worlds)) {
            for (Entity entity : new ArrayList<>(world.entities)) {
                if (tnt.contains(entity)) continue

                if (entity instanceof TNTPrimed) {
                    if (entity.dead || !entity.valid) continue

                    tnt.add(entity)
                    entity.customNameVisible = true
                }
            }
        }
    }
}
