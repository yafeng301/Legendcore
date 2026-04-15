package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.command.CommandExecutor
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerDropItemEvent

// 禁止丢弃
class NoDrop implements Listener {

    static List<String> use = new ArrayList<>()
    static def listener = new NoDrop()
    static ConfigProxy proxy = new ConfigProxy("NoDrop") // 生成配置文件代理类 生成名字教 NoDrop.yml 的配置文件 当然也是为了防止代码过长
    // 注意： 当传奇核心(LegendCore)重载的时候 ConfigProxy 的配置文件会自动重载
    // 当然使用 ConfigurationManagerAPI.instance.getConfig("NoDrop") 同理

    @EventHandler(priority = EventPriority.MONITOR)
    void onDrop(PlayerDropItemEvent e) { // 监听事件禁止玩家丢弃物品
        if (proxy.config.getBoolean("是否启用开关功能") && use.contains(e.player.name)) return
        e.cancelled = true // 禁止丢弃
        e.itemDrop.remove()
        e.player.sendMessage(CoreAPI.format(proxy.config.getString("message"))) // 提示玩家丢弃物品
    }

    static void onGroovyRegister() {
        listener.register() //注册这个事件
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) { // map 参数为有序的Map (LinkedHashMap)
                map.put("是否启用开关功能", false) // 设置一个Config的默认配置 是否允许自定义开关丢弃
                map.put("message", "&f[&6LegendCore&f] 禁止随地大小便")
                map.put("on-message", "&f[&6LegendCore&f] &a成功开启防丢弃功能！")
                map.put("off-message", "&f[&6LegendCore&f] &a成功关闭防丢弃功能！")
                map.put("server-message", "&f[&6LegendCore&f] &c服务器未开启自定义开关丢弃功能！")
            }
        }) //载入配置 会自动缓存 当配置没有生成则载入回调函数 进行生成 (防止性能浪费 多次生成 map)
        CommandManagerAPI.instance.register("drop", new CommandExecutor() {
            @Override
            void onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender instanceof Player) {
                    if (proxy.config.getBoolean("是否启用开关功能")) {
                        if (use.remove(sender.name)) {
                            sender.sendMessage(CoreAPI.format(proxy.config.getString("on-message")))
                        } else {
                            use.add(sender.name)
                            sender.sendMessage(CoreAPI.format(proxy.config.getString("off-message")))
                        }
                    } else {
                        sender.sendMessage(CoreAPI.format(proxy.config.getString("server-message")))
                    }
                } else {
                    sender.sendMessage(CoreAPI.format("&c后台无法使用该命令"))
                }
            }
        }, "nodrop") // 注册一个命令叫 drop 并且自定义开关防丢弃状态
    }

    static void onGroovyUnregister() {
        listener.unregister() // 注销掉这个事件
        CommandManagerAPI.instance.unregister("drop") // 注销掉对应的命令
    }
}
