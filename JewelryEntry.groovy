package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.lazy.Lazy
import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderProcessor
import cn.mcdcs.legendjewelry.api.JewelryAPI
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer

import java.util.regex.Pattern

// %JewelryEntry_饰品背包名字_槽位ID_词条名称%

class JewelryEntry extends PlaceholderProcessor {

    static def processor = new JewelryEntry()
    static def api = Lazy.of(() -> {
        return JewelryAPI.instance.getAPI(CoreAPI.instance)
    })
    static def pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*")

    static void onGroovyRegister() {
        processor.register()
    }

    static void onGroovyUnregister() {
        processor.unregister()
    }

    @Override
    String execute(OfflinePlayer player, String originParams, String[] args) {
        if (online) {
            if (args.length > 2) {
                def online = asOnline()
                def stack = api.get().getItemStack(online, args[0], Integer.parseInt(args[1]))
                if (stack == null || stack.getType() == Material.AIR ||
                !stack.hasItemMeta() || !stack.getItemMeta().hasLore()) return "-1"

                for (string in stack.getItemMeta().getLore()) {
                    if (string.contains(args[2])) {
                        def matcher = pattern.matcher(ChatColor.stripColor(string))
                        if (matcher.find()) return matcher.group()
                    }
                }

                return "-1"
            }
        }
        return null
    }

    @Override
    String getIdentifier() {
        return "JewelryEntry"
    }
}
