package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import priv.seventeen.artist.arcartx.api.ArcartXAPI

class ArcartXDamageDisplay implements Listener {

    static def listener = new ArcartXDamageDisplay()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onDamage(EntityDamageByEntityEvent e) {
        Player damager = null

        if (e.damager instanceof Player) damager = (Player) e.damager
        else if (e.damager instanceof Projectile && ((Projectile) e.damager).shooter instanceof Player)
            damager = (Player) ((Projectile) e.damager).shooter

        if (damager == null) return

        ArcartXAPI.networkSender.sendDamageDisplay(damager, "default", e.finalDamage, e.entity)
    }
}
