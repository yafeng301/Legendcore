package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import com.germ.germplugin.api.GermPacketAPI
import com.germ.germplugin.api.dynamic.gui.GermGuiScreen
import com.germ.germplugin.api.dynamic.gui.GermGuiSlot
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class GermPickupInfo implements Listener {

    static def listener = new GermPickupInfo()
    static def screen = GermGuiScreen.getGermGuiScreen("GermPickupInfo")
    static Map<Player, GermGuiScreen> map = new HashMap<>()

    static void onGroovyRegister() {
        listener.register()

        screen.setScreenType(GermGuiScreen.ScreenType.HUD)
        screen.addGuiPart(
                new GermGuiSlot("GermPickupInfo_Item")
                .setIdentity("GermPickupInfo_Item")
        )
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onPickup(PlayerPickupItemEvent e) {
        if (e.cancelled) return

        GermGuiScreen screen = map.computeIfAbsent(e.player, k -> {
            def clone = screen.clone()
            clone.openGui(e.player)
            return clone
        })

        ItemStack stack = e.item.itemStack
        ItemStack itemStack = new ItemStack(e.item.itemStack.type)

        itemStack.amount = stack.amount

        if (stack.hasItemMeta() && stack.itemMeta.hasDisplayName()) {
            ItemMeta meta = itemStack.itemMeta
            meta.displayName = stack.itemMeta.displayName
            itemStack.itemMeta = meta
        }

        //为什么这样？ 因为客户端仅需要知道物品的名字 和 数量 以及 材质 不需要其他NBT 多发送一些信息 则会增重带宽的负担

        GermPacketAPI.sendSlotItemStack(e.player, "GermPickupInfo_Item", itemStack)
        screen.setInvalid(false)
    }
}
