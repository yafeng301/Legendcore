package groovy

// 本脚本由热心老板提供 (QQ 1361134310)

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

// 禁止破坏和放置方块
class NoBuild implements Listener {

    static def listener = new NoBuild()
    static ConfigProxy proxy = new ConfigProxy("NoBuild") // 生成配置文件代理类

    @EventHandler
    void onBlockBreak(BlockBreakEvent e) { // 监听事件禁止玩家破坏方块
        if (e.player.op) return
        if (proxy.config.getStringList("受保护的世界").contains(e.block.world.name)) {
            e.cancelled = true // 禁止破坏
            e.player.sendMessage(CoreAPI.format(proxy.config.getString("break-message"))) // 提示玩家不能破坏方块
        }
    }

    @EventHandler
    void onBlockPlace(BlockPlaceEvent e) { // 监听事件禁止玩家放置方块
        if (e.player.op) return
        if (proxy.config.getStringList("受保护的世界").contains(e.block.world.name)) {
            e.cancelled = true // 禁止放置
            e.player.sendMessage(CoreAPI.format(proxy.config.getString("place-message"))) // 提示玩家不能放置方块
        }
    }

    static void onGroovyRegister() {
        listener.register() // 注册事件
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("受保护的世界", ["world", "world_nether"]) // 默认受保护的世界
                map.put("break-message", "此世界禁止破坏方块！")
                map.put("place-message", "此世界禁止放置方块！")
            }
        }) // 载入配置
    }

    static void onGroovyUnregister() {
        listener.unregister() // 注销事件
    }
}