package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.TranslateAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.command.CommandExecutor
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent

class AutoPickup implements Listener { // 自动拾取
    static List<String> use = new ArrayList<>()
    static def listener = new AutoPickup()
    static ConfigProxy proxy = new ConfigProxy("AutoPickup")

    @EventHandler(priority = EventPriority.MONITOR)
    void onDeath(EntityDeathEvent e) {
        if (e.entity instanceof Player) return

        if (e.entity.killer == null) return

        if (proxy.config.getBoolean("是否启用开关功能") && use.contains(e.entity.killer.name)) return

        e.drops.removeIf { it == null || it.getType() == Material.AIR || it.amount == 0}

        def player = e.entity.killer
        for (final def itemStack in e.drops) {
            def map = player.inventory.addItem(itemStack.clone())
            if (map.isEmpty()) {
                String show = CoreAPI.format(proxy.config.getString("显示内容", "")
                        .replace("<m>", TranslateAPI.convertShowName(itemStack))
                        .replace("<n>", itemStack.amount.toString()))

                for (String text : proxy.config.getStringList("显示类型")) {
                    switch (text.toLowerCase()) {
                        case "actionbar":
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(show))
                            break
                        case "title":
                            player.sendTitle(show, "", 0, 20, 0)
                            break
                        case "subtitle":
                            player.sendTitle("", show, 0, 20, 0)
                            break
                        case "message":
                            player.sendMessage(show)
                            break
                    }
                }
            } else {
                for (final def entry in map.entrySet()) {
                    e.entity.world.dropItemNaturally(e.entity.location, entry.value)
                }
            }
        }

        e.drops.clear()
    }

    static void onGroovyRegister() {
        listener.register()
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("是否启用开关功能", false)
                map.put("on-message", "&f[&6LegendCore&f] &a成功开启自动拾取功能！")
                map.put("off-message", "&f[&6LegendCore&f] &a成功关闭自动拾取功能！")
                map.put("server-message", "&f[&6LegendCore&f] &c服务器未开启自定义开关自动拾取功能！")
                map.put("显示类型", ["ActionBar", "Title", "SubTitle", "Message"])
                map.put("显示内容", "<m> * <n>")
            }
        })
        CommandManagerAPI.instance.register("autopickup", new CommandExecutor() {
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
        }, "pickup", "apick")
    }

    static void onGroovyUnregister() {
        listener.unregister()
        CommandManagerAPI.instance.unregister("autopickup")
    }
}
