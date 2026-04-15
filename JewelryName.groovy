package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderProcessor
import cn.mcdcs.legendjewelry.api.JewelryAPI
import org.bukkit.Material
import org.bukkit.OfflinePlayer

// 新增变量 %JewelryName_饰品背包名_饰品槽位ID% 获取对应饰品位置的物品名字
// 有名字则返回名字 没名字则返回 ""

class JewelryName extends PlaceholderProcessor {

    static def api
    static def processor = new JewelryName()

    static void onGroovyRegister() {
        api = JewelryAPI.instance.getAPI(CoreAPI.instance)
        processor.onRegister()
    }

    static void onGroovyUnregister() {
        processor.onUnregister()
    }

    @Override
    String execute(OfflinePlayer player, String originParams, String[] args) {
        if (player.online) {
            if (args.length > 1) {
                def stack = api.getItemStack(player.player, args[0], Integer.parseInt(args[1]))
                if (stack == null || stack.type == Material.AIR) return ""
                if (!stack.hasItemMeta() || !stack.itemMeta.hasDisplayName()) return ""
                return stack.itemMeta.displayName
            }
        }
        return null
    }

    @Override
    String getIdentifier() {
        return "JewelryName"
    }
}
