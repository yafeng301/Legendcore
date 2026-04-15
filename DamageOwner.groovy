package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.serverct.ersha.api.AttributeAPI
import org.serverct.ersha.api.enums.AttributeName
import org.serverct.ersha.attribute.data.AttributeData
import org.serverct.ersha.script.AttrScriptUtils

class DamageOwner implements Listener {

    static def listener = new DamageOwner()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onDamageLowest(EntityDamageByEntityEvent e) {
        if (e.entity instanceof Player) {
            Player damager = null
            if (e.damager instanceof Player) damager = e.damager as Player
            else if (e.damager instanceof Projectile)
                if ((e.damager as Projectile).shooter instanceof Player)
                    damager = (e.damager as Projectile).shooter as Player

            if (damager == null) return

            def data = AttributeAPI.getAttrData(e.entity as Player)
            def value = data.getAttributeValue(AttributeName.REFLECTION_RATE.toDefaultName())[0].doubleValue() * 0.9
            AttributeAPI.addSourceAttribute(data, "Temp-DamageOwner", [AttributeName.REFLECTION_RATE.toServerName() + " -" + value])
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDamage(EntityDamageByEntityEvent e) {
        if (e.entity instanceof Player) {
            Player damager = null
            if (e.damager instanceof Player) damager = e.damager as Player
            else if (e.damager instanceof Projectile)
                if ((e.damager as Projectile).shooter instanceof Player)
                    damager = (e.damager as Projectile).shooter as Player

            if (damager == null) return

            e.damage = e.damage / 50
            AttributeAPI.takeSourceAttribute(AttributeAPI.getAttrData(e.entity as Player), "Temp-DamageOwner")
        }
    }
}
