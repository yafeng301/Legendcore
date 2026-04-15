package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.lazy.Lazy
import cn.mcdcs.legendcore.api.reflect.Reflects
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class KnockbackKing implements Listener {

    static def listener = new KnockbackKing()
    static def modifier = new AttributeModifier(UUID.randomUUID(), "LegendCore-Groovy-KnockbackKing",
            1, AttributeModifier.Operation.ADD_NUMBER)

    static def attribute = Lazy.of(() -> {
        for (Attribute attribute : Attribute.values()) {
            if (attribute.name().contains("KNOCKBACK_RESISTANCE"))
                return attribute
        }
        return null
    })

    static void onGroovyRegister() {
        listener.register()

        for (Player player : Bukkit.onlinePlayers) player.getAttribute(attribute.get()).addModifier(modifier)
    }

    static void onGroovyUnregister() {
        listener.unregister()

        for (Player player : Bukkit.onlinePlayers) player.getAttribute(attribute.get()).removeModifier(modifier)
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        e.player.getAttribute(attribute.get()).addModifier(modifier)
    }

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        e.player.getAttribute(attribute.get()).removeModifier(modifier)
    }
}
