package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.world.ChunkUnloadEvent

import java.text.DecimalFormat

class DamageDisplay implements Listener {

    static def listener = new DamageDisplay()
    static Set<Data> set = new HashSet<>()
    static def task = new BukkitRunnable() {
        @Override
        void run() {
            def iterator = set.iterator()
            while (iterator.hasNext()) {
                def next = iterator.next()
                next.tick = next.tick - 1

                if (next.tick < 0) {
                    next.entity.remove()
                    iterator.remove()
                }
            }
        }
    }

    static void onGroovyRegister() {
        listener.register()
        task.runTaskTimer(0, 1)

        Bukkit.worlds.forEach { it.entities.forEach {
            if (it instanceof ArmorStand) {
                if (!it.hasGravity() && !it.visible && it.customNameVisible) {
                    if (it.customName != null) {
                        if (it.customName.startsWith("§c❤ ")) {
                            it.remove()
                        }
                    }
                }
            }
        }}
    }

    static void onGroovyUnregister() {
        listener.unregister()
        task.cancel()

        set.forEach { it.entity.remove() }
        set.clear()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDamage(EntityDamageEvent e) {
        if (e.cancelled) return

        if (e.entity instanceof ArmorStand) {
            for (def data in set) {
                if (data.entity == e.entity) {
                    e.cancelled = true
                    return
                }
            }
        }

        if (e.entity instanceof LivingEntity) display(e.entity, e.finalDamage)
    }

    @EventHandler
    void onUnload(ChunkUnloadEvent e) {
        for (Entity entity : e.chunk.entities) {
            if (entity instanceof ArmorStand) {
                def iterator = set.iterator()
                while (iterator.hasNext()) {
                    def next = iterator.next()
                    if (next.entity == entity) {
                        next.entity.remove()
                        iterator.remove()
                    }
                }
            }
        }
    }

    static void display(Entity entity, double damage) {
        def location = entity.location
        location.add((Math.random() - 0.5) * 1.2, -0.1 + entity.getHeight() * -0.25, (Math.random() - 0.5) * 1.2)
        ArmorStand stand = entity.world.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand
        stand.customNameVisible = true
        stand.customName = "§c❤ §f" + new DecimalFormat("0.00").format(damage)
        stand.visible = false
        stand.gravity = false
        def data = new Data()
        data.entity = stand
        set.add(data)
    }

    static class Data {
        Entity entity
        int tick = 20
    }
}
