package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import cn.mcdcs.legendcore.api.view.ViewAPI
import cn.mcdcs.legendcore.api.wrapper.WrapperManagerAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent

class NoClickLore implements Listener {

    static def listener = new NoClickLore()
    static def proxy = new ConfigProxy("NoClickLore")
    static Map<String, List<Node>> cache = new HashMap<>()
    static def EMPTY = ""

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig((map) -> {
            map.put("debug", false)
            map.put("禁用Lore.模糊检测", [["界面名称": "分解界面", "Lore列表": ["STONE", "DIAMOND"]]])
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onOpen(InventoryOpenEvent e) {
        if (proxy.config.getBoolean("debug"))
            e.player.sendMessage(ViewAPI.instance.transformation(e.view).title)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onClick(InventoryClickEvent e) {
        if (e.slot == -999) return
        if (e.cancelled) return

        def currentLoreMsg = EMPTY
        if (e.clickedInventory == e.whoClicked.inventory &&
                e.currentItem != null && e.currentItem.hasItemMeta() && e.currentItem.itemMeta.hasLore())
            currentLoreMsg = e.currentItem.itemMeta.lore.toString()


        def numberLoreMsg = EMPTY
        if (e.click == ClickType.NUMBER_KEY) {
            def item = e.whoClicked.inventory.getItem(e.hotbarButton)
            if (item != null && item.hasItemMeta() && item.itemMeta.hasLore()) numberLoreMsg = item.itemMeta.lore.toString()
        }

        if (currentLoreMsg == EMPTY && numberLoreMsg == EMPTY) return

        def title = ViewAPI.instance.transformation(e.view).title

        for (Node node : cache.computeIfAbsent("Lore模糊检测", k -> new ArrayList<>())) {
            if (title.contains(node.title)) {
                if (contains(currentLoreMsg, node.value) || contains(numberLoreMsg, node.value)) {
                    e.cancelled = true
                    return
                }
            }
        }
    }

    static boolean contains(String loreMsg, List<String> list) {
        if (loreMsg == EMPTY) return false

        for (String text : list) if (loreMsg.contains(text)) return true

        return false
    }

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        def map = WrapperManagerAPI.instance.newMapWrapper(proxy.config)

        cache.clear()

        map.getMapList("禁用Lore.模糊检测").forEach {
            cache.computeIfAbsent("Lore模糊检测", k -> new ArrayList<>()).add(
                    new Node(it.getString("界面名称"), it.getStringList("Lore列表"))
            )
        }
    }

    static class Node {
        String title
        List<String> value

        Node(String title, List<String> value) {
            this.title = title
            this.value = value
        }
    }
}
