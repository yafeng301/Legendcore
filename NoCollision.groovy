package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerJoinEvent

class NoCollision implements Listener {

    static def listener = new NoCollision()

    static void onGroovyRegister() {
        listener.register()

        for (final def world in Bukkit.worlds) {
            for (final def entity in world.entities) {
                if (entity instanceof LivingEntity) entity.collidable = false
            }
        }
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        e.player.collidable = false
    }

    @EventHandler
    void onSpawn(EntitySpawnEvent e) {
        def entity = e.entity
        if (entity instanceof LivingEntity) entity.collidable = false
    }
}
