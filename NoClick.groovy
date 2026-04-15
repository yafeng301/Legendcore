package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import cn.mcdcs.legendcore.api.view.ViewAPI
import cn.mcdcs.legendcore.api.wrapper.WrapperManagerAPI
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

import java.util.stream.Collectors

class NoClick implements Listener {

    static def listener = new NoClick()
    static def proxy = new ConfigProxy("NoClick")
    static Map<String, List<Node>> cache = new HashMap<>()

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig((map) -> {
            map.put("禁用材质.模糊检测", [["界面名称": "分解界面", "材质列表": ["STONE", "DIAMOND"]]])
            map.put("禁用材质.精准检测", [["界面名称": "&a强化界面", "材质列表": ["STONE", "DIAMOND"]]])
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onClick(InventoryClickEvent e) {
        if (e.slot == -999) return
        if (e.cancelled) return

        def currentType = Material.AIR
        if (e.clickedInventory == e.whoClicked.inventory && e.currentItem != null) currentType = e.currentItem.type
        def numberType = Material.AIR
        if (e.click == ClickType.NUMBER_KEY) {
            def item = e.whoClicked.inventory.getItem(e.hotbarButton)
            if (item != null) numberType = item.type
        }

        if (currentType == Material.AIR && numberType == Material.AIR) return

        def title = ViewAPI.instance.transformation(e.view).title

        for (Node node : cache.computeIfAbsent("材质精准检测", k -> new ArrayList<>())) {
            if (node.title == title) {
                if (node.value.contains(numberType) || node.value.contains(currentType)) {
                    e.cancelled = true
                    return
                }
                break
            }
        }

        for (Node node : cache.computeIfAbsent("材质模糊检测", k -> new ArrayList<>())) {
            if (title.contains(node.title)) {
                if (node.value.contains(numberType) || node.value.contains(currentType)) {
                    e.cancelled = true
                    return
                }
            }
        }
    }

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        def map = WrapperManagerAPI.instance.newMapWrapper(proxy.config)

        cache.clear()

        map.getMapList("禁用材质.模糊检测").forEach {
            cache.computeIfAbsent("材质模糊检测", k -> new ArrayList<>()).add(
                    new Node(it.getString("界面名称"), it.getStringList("材质列表").stream().map {
                        return Material.valueOf(it.toUpperCase())
                    }.collect(Collectors.toList()))
            )
        }

        map.getMapList("禁用材质.精准检测").forEach {
            cache.computeIfAbsent("材质精准检测", k -> new ArrayList<>()).add(
                    new Node(it.getString("界面名称"), it.getStringList("材质列表").stream().map {
                        return Material.valueOf(it.toUpperCase())
                    }.collect(Collectors.toList()))
            )
        }
    }

    static class Node {
        String title
        List<Material> value

        Node(String title, List<Material> value) {
            this.title = title
            this.value = value
        }
    }
}
