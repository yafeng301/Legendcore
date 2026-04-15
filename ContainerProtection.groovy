package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent

class ContainerProtection implements Listener {

    static def listener = new ContainerProtection()
    static def proxy = new ConfigProxy("ContainerProtection")

    static void onGroovyRegister() {
        listener.register()
        proxy.loadConfig(map -> map.put("提示信息", "&6LegendCore &f>>> &c该容器内存有物品无法被破坏！"))
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onBreak(BlockBreakEvent e) {
        if (e.cancelled) return
        def state = e.block.state
        if (state instanceof Container) {
            if (e.block.type.name().contains("SHULKER_BOX")) return
            for (final def item in state.inventory.getContents()) {
                if (item != null && item.type != Material.AIR) {
                    if (!proxy.config.getString("提示信息").isEmpty())
                        e.player.sendMessage(CoreAPI.format(proxy.config.getString("提示信息")))
                    e.cancelled = true
                    return
                }
            }
        }
    }
}
