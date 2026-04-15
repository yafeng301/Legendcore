package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.meta.ItemMeta

import java.util.regex.Matcher
import java.util.regex.Pattern

class LoreDurability implements Listener {

    static def listener = new LoreDurability()
    static def proxy = new ConfigProxy("LoreDurability")
    static def pattern = Pattern.compile("(?<!§)\\d+")

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("耐久检测Lore", "耐久度:")
                map.put("是否销毁物品", true)
            }
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDamage(PlayerItemDamageEvent e) {
        if (e.cancelled) return

        if (e.item == null || e.item.type == Material.AIR) return

        if (!e.item.hasItemMeta() || !e.item.itemMeta.hasLore()) return

        int value = -1
        Matcher matcher
        int index = 0

        List<String> lore = e.item.itemMeta.lore
        for (; index < lore.size(); index++) {
            String text = lore.get(index)
            if (text.contains(CoreAPI.format(proxy.config.getString("耐久检测Lore")))) {
                matcher = pattern.matcher(text)
                if (matcher.find()) {
                    value = Integer.parseInt(matcher.group())
                    break
                }
            }
        }

        if (value == -1) return

        e.setCancelled(true)
        if ((value -= e.damage) <= 0) {
            value = 0
            if (proxy.config.getBoolean("是否销毁物品", true)){
                e.item.amount = 0
                return
            }
        }

        lore.set(index, matcher.replaceFirst(value + ""))
        ItemMeta meta = e.item.itemMeta
        meta.lore = lore
        e.item.itemMeta = meta
    }
}
