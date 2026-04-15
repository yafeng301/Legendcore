package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryTest implements Listener {

    static def listener = new InventoryTest()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onClick(InventoryClickEvent e) {
        if (e.slot == -999) return

        if (e.whoClicked instanceof Player)
            e.whoClicked.sendMessage("物品栏点击类型 " + e.click.name())
    }
}
