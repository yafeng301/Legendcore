package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendmapping.api.MappingAPI
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class MagicMappingExtra implements Listener {

    static def listener = new MagicMappingExtra()

    static def attackEntry = "魔法攻击" // 改成对应的 映射 词条名字
    static def defenseEntry = "魔法防御" // 改成对应的 映射 词条名字

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onDamage(EntityDamageByEntityEvent e) {
        double defense = 0
        double attack = 0

        if (e.entity instanceof LivingEntity) {
            LivingEntity entity = e.entity as LivingEntity
            def data = MappingAPI.getEntityMappingDataNotNull(entity, defenseEntry)
            defense = data.completeMin + (data.completeMax - data.completeMin) * Math.random()
        }

        LivingEntity attacker = null

        if (e.damager instanceof LivingEntity) attacker = e.damager as LivingEntity
        else if (e.damager instanceof Projectile)
            if ((e.damager as Projectile).shooter instanceof LivingEntity)
                attacker = (e.damager as Projectile).shooter as LivingEntity

        if (attacker != null) {
            def data = MappingAPI.getEntityMappingDataNotNull(attacker, attackEntry)
            attack = data.completeMin + (data.completeMax - data.completeMin) * Math.random()
        }

        if (attack > defense) {
            e.damage += attack - defense
        }
    }
}
