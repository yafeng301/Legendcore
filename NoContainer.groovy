package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.block.Container
import org.bukkit.entity.Donkey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.vehicle.VehicleDamageEvent

class NoContainer implements Listener {

    static def listener = new NoContainer()
    static def proxy = new ConfigProxy("NoContainer")

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig((map) -> {
            map.put("指定的世界", ["world", "world_the_end"])
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDamage(EntityDamageEvent e) {
        if (e.cancelled) return

        if (e.entity.type == EntityType.ARMOR_STAND ||
        e.entity.type == EntityType.ITEM_FRAME ||
        e.entity.type.name().contains("MINECART")) {
            if (proxy.config.getStringList("指定的世界").contains(e.entity.world.name)) {
                if (e instanceof EntityDamageByEntityEvent) {
                    def damager = e.damager
                    if (damager instanceof Player) {
                        if (damager.op) return
                    }
                }

                e.cancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onEntity(PlayerInteractEntityEvent e) {
        if (e.cancelled) return

        if (e.rightClicked.type == EntityType.ARMOR_STAND ||
                e.rightClicked.type == EntityType.ITEM_FRAME ||
                e.rightClicked.type.name().contains("MINECART")) {
            if (proxy.config.getStringList("指定的世界").contains(e.player.world.name)) {
                if (e.player.op) return

                e.cancelled = true
                return
            }
        }


        def clicked = e.rightClicked
        if (clicked instanceof Donkey) {
            if (e.player.op) return

            if (clicked.carryingChest) {
                clicked.carryingChest = false
                e.cancelled
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onArmor(PlayerArmorStandManipulateEvent e) {
        if (e.cancelled) return

        if (proxy.config.getStringList("指定的世界").contains(e.player.world.name)) {
            if (e.player.op) return

            e.cancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDamage(VehicleDamageEvent e) {
        if (e.cancelled) return

        if (proxy.config.getStringList("指定的世界").contains(e.vehicle.world.name)) {
            def attacker = e.attacker
            if (attacker instanceof Player) if (attacker.op)return

            e.cancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onRight(PlayerInteractEvent e) {
        if (e.cancelled) return

        if (e.action != Action.RIGHT_CLICK_BLOCK) return

        if (e.clickedBlock.state instanceof Container) {
            if (e.clickedBlock.type.name().contains("SHULKER_BOX")) return

            if (proxy.config.getStringList("指定的世界").contains(e.player.world.name))
                e.cancelled = true
        }
    }
}
