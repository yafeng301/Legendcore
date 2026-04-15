package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta

class AbstractCrossbow implements Listener {
    static def listener = new AbstractCrossbow()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onBow(EntityShootBowEvent e) {
        if (e.cancelled) return

        if (e.bow == null) return

        if (e.bow.type == Material.CROSSBOW) {
            List<ItemStack> list = new ArrayList<>()
            for (ItemStack item : ((CrossbowMeta) e.bow.itemMeta).chargedProjectiles) list.add(item.clone())

            Bukkit.scheduler.callSyncMethod(CoreAPI.instance, () -> {
                CrossbowMeta meta = (CrossbowMeta) e.bow.itemMeta

                meta.chargedProjectiles = list
                e.bow.itemMeta = meta
                return ""
            })
        }
    }
}
