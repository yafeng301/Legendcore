package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.nms.tag.TagManager
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

class MobNanFixer implements Listener {

    static def listener = new MobNanFixer()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onDamage(EntityDamageEvent e) {
        def entity = e.entity
        if (entity instanceof Player) return

        if (entity instanceof LivingEntity) {
            def tag = TagManager.getEntityTag(entity)
            if (Double.isNaN(tag.getDouble("Health"))) {
                tag.set("AbsorptionAmount", 0.0f)
                tag.set("Health", 1.0f)
                TagManager.setEntityTag(entity, tag)
            }
        }
    }
}
