package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class BanUseItem implements Listener {

    static def listener = new BanUseItem()
    static def proxy = new ConfigProxy("BanUseItem")
    static Map<String, List<String>> worlds = new HashMap<>()

    static void onGroovyRegister() {
        proxy.loadConfig(map -> {
            map.put("worlds.world", ["STONE", "FISHING_ROD"])
        })

        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        def section = proxy.config.getConfigurationSection("worlds")
        worlds.clear()
        for (final def key in section.getKeys(false)) {
            worlds.put(key, section.getStringList(key))
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onInteract(PlayerInteractEvent e) {
        if (e.action == Action.PHYSICAL) return

        if (e.hasItem()) {
            def list = worlds.get(e.player.world.name)
            if (list == null) return

            if (list.contains(e.item.type.toString())) e.cancelled = true
        }
    }
}
