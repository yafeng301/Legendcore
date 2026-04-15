package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.TranslateAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.entity.ItemSpawnEvent

class ItemShowName implements Listener {

    static def listener = new ItemShowName()
    static def proxy = new ConfigProxy("ItemShowName")

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig(map -> map.put("显示名字", "<s> §f*§b <n>"))
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onItem(ItemSpawnEvent e) {
        if (e.cancelled) return
        update(e.entity)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onItem(ItemMergeEvent e) {
        if (e.cancelled) return

        def stack = e.target.itemStack
        if (stack == null || stack.type == Material.AIR) return

        int amount = stack.amount + e.entity.itemStack.amount
        if (amount > 64) amount = 64

        update(e.target, amount)
    }

    static void update(Item item, int amount) {
        def stack = item.itemStack

        if (stack == null || stack.type == Material.AIR) return

        String show = null

        if (stack.hasItemMeta() && stack.itemMeta.hasDisplayName()) show = stack.itemMeta.displayName

        if (show == null) show = TranslateAPI.convert(stack)

        if (show == null) show = stack.type.name()

        if (amount == -1) amount = stack.amount

        item.customNameVisible = true
        item.customName = CoreAPI.format(proxy.config.getString("显示名字")).replace("<s>", show).replace("<n>", amount + "")
    }

    static void update(Item item) {
        update(item, -1)
    }
}
