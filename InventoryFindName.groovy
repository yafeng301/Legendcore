package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderProcessor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack

class InventoryFindName extends PlaceholderProcessor {

    static def processor = new InventoryFindName()

    static void onGroovyRegister() {
        processor.register()
    }

    static void onGroovyUnregister() {
        processor.unregister()
    }

    @Override
    String execute(OfflinePlayer player, String originParams, String[] args) {
        if (player.online) {
            def online = player.player
            String name = CoreAPI.format(originParams)
            int count = 0
            for (ItemStack itemStack : online.inventory) {
                if (itemStack == null || itemStack.type == Material.AIR) continue
                if (!itemStack.hasItemMeta() || !itemStack.itemMeta.hasDisplayName()) continue
                if (itemStack.itemMeta.displayName == name) count += itemStack.amount
            }
            return count + ""
        }
        return null
    }

    @Override
    String getIdentifier() {
        return "InventoryFindName"
    }
}
