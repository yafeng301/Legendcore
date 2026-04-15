package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.Material
import org.bukkit.block.BlockState
import org.bukkit.block.Container
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack

class ContainerControl implements Listener {

    static def listener = new ContainerControl()
    static def proxy = new ConfigProxy("ChestControl")

    static void onGroovyRegister() {
        listener.register()
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("限制数量", 10)
                map.put("限制信息", "&a已经达到最大放置数量")
            }
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onPlace(BlockPlaceEvent e) {
        if (e.cancelled) return

        if (!(e.block.state instanceof Container)) return
        if (e.block.type.name().contains("SHULKER_BOX")) return

        int count = 0
        for (BlockState state : e.block.chunk.tileEntities) if (state instanceof Container) count++

        if (count < proxy.config.getInt("限制数量")) return

        e.player.sendMessage(CoreAPI.format(proxy.config.getString("限制信息")))
        e.cancelled = true
    }
}
