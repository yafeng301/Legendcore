package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.item.ItemResources
import cn.mcdcs.legendcore.api.matcher.item.SingleLineItemMatcher
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.inventory.EquipmentSlot
import org.serverct.ersha.api.event.AttrAttributeReadEvent

class HandNoAP implements Listener {

    static def listener = new HandNoAP()

    static List<SingleLineItemMatcher> matchers = new ArrayList<>()

    static void onGroovyRegister() {
        listener.register()

        // 匹配器教程
        // https://docs.qq.com/aio/DU0F4RHZtZU5CamRW?p=O1kxlqQaNBdiVUntqLOyw8

        for (String string : [ // 按照格式 无限添加
                "lorec=手持无属性",
                "lorec=手持不加载属性",
                "name=&a小小玩具"
        ]) {
            matchers.add(SingleLineItemMatcher.of(string))
        }
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onRead(AttrAttributeReadEvent e) {
        if (e.cancelled) return

        if (e.slot != EquipmentSlot.HAND) return

        if (e.itemStack == null || e.itemStack.type == Material.AIR) return

        def resources = ItemResources.of(e.itemStack)
        for (def matcher : matchers) {
            if (matcher.match(resources)) {
                e.cancelled = true
                return
            }
        }
    }
}
