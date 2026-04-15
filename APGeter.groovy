package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import io.lumine.xikage.mythicmobs.MythicMobs
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.serverct.ersha.api.AttributeAPI
class APGeter implements Listener {

    static def listener = new APGeter()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        def damager = e.damager
        def entity = e.entity
        if (entity instanceof LivingEntity) {
            if (damager instanceof Player) {
                if (damager.op) {
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(e.entity)) {
                        def data = AttributeAPI.getAttrData(entity)
                        if (data != null) {
                            def field = data.class.getDeclaredField("apiSourceAttribute")
                            field.accessible = true
                            damager.sendMessage(field.get(data).toString())
                        }
                    }
                }
            }
        }
    }
}
