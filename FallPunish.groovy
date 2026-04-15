package groovy

import cn.mcdcs.legendcore.api.attribute.AttributeManagerAPI
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask

class FallPunish implements Listener {

    static def listener = new FallPunish()
    static Map<String, BukkitTask> tasks = new HashMap<>()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()

        tasks.entrySet().forEach {
            it.value.cancel()

            def player = Bukkit.getPlayerExact(it.key)
            if (player != null) AttributeManagerAPI.instance.removeCustomAttribute(player, "FallPunish")
        }
    }

    @EventHandler
    void onFall(EntityDamageEvent e) {
        if (e.cause != EntityDamageEvent.DamageCause.FALL) return

        def entity = e.entity
        if (entity instanceof Player) {
            if (entity.fallDistance < 5) {
                AttributeManagerAPI.instance.addCustomAttribute(entity, "FallPunish", ["减速 30%"])
            } else {
                AttributeManagerAPI.instance.addCustomAttribute(entity, "FallPunish", ["减速 50%"])
                e.damage = 2 * entity.fallDistance + entity.health * 0.05 * entity.fallDistance
            }

            String name = e.entity.name
            def task = tasks.remove(name)
            if (task != null) task.cancel()

            tasks.put(name, new BukkitRunnable(){
                @Override
                void run() {
                    def player = Bukkit.getPlayerExact(name)
                    if (player == null) return
                    AttributeManagerAPI.instance.removeCustomAttribute(player, "FallPunish")
                }
            }.runTaskLater(40))
        }
    }

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        def task = tasks.remove(e.player.name)
        if (task == null) return
        task.cancel()

        AttributeManagerAPI.instance.removeCustomAttribute(e.player, "FallPunish")
    }
}
