package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import eos.moe.dragoncore.network.PacketSender
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class DragonPickupInfo implements Listener {

    static def listener = new DragonPickupInfo()
    static def yaml = new YamlConfiguration()

    static def 背景贴图路径 = "http://stonewiki.cn/png/DragonPickupHud.jpg"
    static def 持续时间 = 2000
    static def 是否启用自定义HUD = false //自定义HUD 仅需要放置到 DragonCore/Gui/DragonPickupInfo_Hud.yml 即可
    static def 宽带节流 = true // 开启后可能导致龙核贴图失效

    static void onGroovyRegister() {
        listener.register()

        if (是否启用自定义HUD) return

        yaml.loadFromString(text)

        Bukkit.onlinePlayers.forEach { PacketSender.sendYaml(it, "Gui/DragonPickupInfo_Hud", yaml)}
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        if (是否启用自定义HUD) return

        PacketSender.sendYaml(e.player, "Gui/DragonPickupInfo_Hud", yaml)
    }

    @EventHandler
    void onCommand(PlayerCommandPreprocessEvent e) {
        if (是否启用自定义HUD) return
        if (!e.player.op) return

        String lower = e.message.toLowerCase()
        if (lower.startsWith("/core reload") || lower.startsWith("/dragoncore reload")) {
            new BukkitRunnable() {
                @Override
                void run() {
                    Bukkit.onlinePlayers.forEach { PacketSender.sendYaml(it, "Gui/DragonPickupInfo_Hud", yaml)}
                }
            }.runTask()
        }
    }

    @EventHandler
    void onCommand(ServerCommandEvent e) {
        if (是否启用自定义HUD) return

        String lower = e.command.toLowerCase()
        if (lower.startsWith("core reload") || lower.startsWith("dragoncore reload")) {
            new BukkitRunnable() {
                @Override
                void run() {
                    Bukkit.onlinePlayers.forEach { PacketSender.sendYaml(it, "Gui/DragonPickupInfo_Hud", yaml)}
                }
            }.runTask()
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onPickup(PlayerPickupItemEvent e) {
        if (e.cancelled) return

        ItemStack stack = e.item.itemStack
        ItemStack itemStack = stack
        if (宽带节流) {
            itemStack = new ItemStack(e.item.itemStack.type)

            itemStack.amount = stack.amount

            if (stack.hasItemMeta() && stack.itemMeta.hasDisplayName()) {
                ItemMeta meta = itemStack.itemMeta
                meta.displayName = stack.itemMeta.displayName
                itemStack.itemMeta = meta
            }
        }

        //为什么这样？ 因为客户端仅需要知道物品的名字 和 数量 以及 材质 不需要其他NBT 多发送一些信息 则会增重带宽的负担

        PacketSender.putClientSlotItem(e.player, "DragonPickupInfo_Item", itemStack)
        PacketSender.sendOpenHud(e.player, "DragonPickupInfo_Hud")
    }

    static String text = "Functions:\n" +
            "  open: |-\n" +
            "    方法.异步执行方法(\"自适应\");\n" +
            "    界面变量.物品信息 = 方法.合并加入文本(\"§6\",方法.取物品名(方法.取槽位物品(\"DragonPickupInfo_Item\")),\" §f*§b\",方法.到整数(方法.取物品数(方法.取槽位物品(\"DragonPickupInfo_Item\"))));\n" +
            "    方法.设置组件值(\"物品信息\", \"texts\", 界面变量.物品信息);\n" +
            "    方法.异步执行方法(\"延时关闭\");\n" +
            "  延时关闭: |-\n" +
            "    方法.延时(" + 持续时间 +");\n" +
            "    方法.关闭界面()\n" +
            "  自适应: |-\n" +
            "    界面变量.x=(方法.取屏幕宽度-方法.取组件值(\"背景\",\"width\"))/2;\n" +
            "    界面变量.y=(方法.取屏幕高度-方法.取组件值(\"背景\",\"height\"))/2;\n" +
            "    方法.延时(250);\n" +
            "    方法.异步执行方法(\"自适应\");\n" +
            "    \n" +
            "    \n" +
            "背景:\n" +
            "  type: texture\n" +
            "  x: \"界面变量.x\"\n" +
            "  y: \"界面变量.y-250*(h/720)\"\n" +
            "  width: \"180*(h/720)\"\n" +
            "  height: \"50*(h/720)\"\n" +
            "  texture: \"" + 背景贴图路径 + "\"\n" +
            "  \n" +
            "物品:\n" +
            "  type: slot\n" +
            "  x: \"界面变量.x+10*(h/720)\"\n" +
            "  y: \"界面变量.y-240*(h/720)\"\n" +
            "  identifier: \"DragonPickupInfo_Item\"\n" +
            "  scale: 1.8*(h/720)\n" +
            "  \n" +
            "拾取提示:\n" +
            "  type: label\n" +
            "  x: \"界面变量.x+108*(h/720)\"\n" +
            "  y: \"界面变量.y-242*(h/720)\"\n" +
            "  z: 10\n" +
            "  texts: \"§a拾取物品\"\n" +
            "  center: true\n" +
            "  scale: 1.7*(h/720)\n" +
            "  \n" +
            "物品信息:\n" +
            "  type: label\n" +
            "  x: \"界面变量.x+110*(h/720)\"\n" +
            "  y: \"界面变量.y-224*(h/720)\"\n" +
            "  z: 10\n" +
            "  texts: \"\"\n" +
            "  scale: 1.6*(h/720)\n" +
            "  center: true";
}
