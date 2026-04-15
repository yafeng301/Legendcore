package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.event.inventory.AnvilCompleteEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.inventory.ItemStack

class NoAnvilRename implements Listener {

    static def listener = new NoAnvilRename()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onAnvil(AnvilCompleteEvent e) {
        if (getName(e.target) == getName(e.result)) return

        e.cancelled = true
    }

    static String getName(ItemStack itemStack) {
        if (itemStack == null || itemStack.type == Material.AIR) return ""

        if (!itemStack.hasItemMeta() || !itemStack.itemMeta.hasDisplayName()) return ""

        return itemStack.itemMeta.displayName
    }
}
