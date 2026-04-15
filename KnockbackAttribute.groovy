package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.lazy.Lazy
import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderHook
import cn.mcdcs.legendcore.api.reflect.Reflects
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

class KnockbackAttribute extends BukkitRunnable {

    static def 传奇映射词条名字 = "抗击退"

    static def task = new KnockbackAttribute()
    static def uid = UUID.fromString("c1ebe0c3-4dcf-49fc-820e-87359a6bd04c")
    static def attribute = Lazy.of(() -> {
        for (Attribute attribute : Attribute.values()) {
            if (attribute.name().contains("KNOCKBACK_RESISTANCE"))
                return attribute
        }
        return null
    })

    static void onGroovyRegister() {
        task.runTaskTimer(20, 50)
    }

    static void onGroovyUnregister() {
        task.cancel()
    }

    @Override
    void run() {
        for (Player player : Bukkit.onlinePlayers) {
            player.getAttribute(attribute.get()).setBaseValue(
                    Double.parseDouble(PlaceholderHook.setPlaceholders(player, "%LegendMapping_max_$传奇映射词条名字%")) / 100)
        }
    }
}
