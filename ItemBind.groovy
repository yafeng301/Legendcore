package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import cn.mcdcs.legendcore.api.wrapper.WrapperManagerAPI
import cn.mcdcs.legendcore.api.wrapper.map.MapWrapper
import eos.moe.dragoncore.api.SlotAPI
import eos.moe.dragoncore.api.event.PlayerSlotUpdateEvent
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

class ItemBind implements Listener {

    static def listener = new ItemBind()
    static def proxy = new ConfigProxy("ItemBind")
    static String noBind
    static String bind
    static String remind
    static String success
    static String dragon

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig((map) -> {
            map.put("检测的绑定Lore", "绑定状态: 快来绑定宝宝吧~")
            map.put("绑定后的Lore", "绑定状态: <p>")
            map.put("物品绑定提示", "&a手持物品输入 /bc 进行绑定吧,哎哟哟~")
            map.put("绑定完成提示", "&a绑定成功咯~~~")
            map.put("龙核绑定提示", "&a龙核！~~")
        })

        CommandManagerAPI.instance.register("bd", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender instanceof Player) {
                    def item = sender.itemInHand
                    if (item == null) {
                        message("&c请手持物品！")
                        return
                    }

                    if (!needBind(item)) {
                        message("&c这个物品无需绑定！")
                        return
                    }

                    def meta = item.itemMeta
                    List<String> lore = meta.lore
                    lore.set(lore.indexOf(noBind), bind.replace("<p>", sender.name))
                    meta.lore = lore
                    item.itemMeta = meta
                    message(success)
                }
            }
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
        CommandManagerAPI.instance.unregister("bd")
    }

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        def map = WrapperManagerAPI.instance.newMapWrapper(proxy.config)

        noBind = map.getString("检测的绑定Lore")
        bind = map.getString("绑定后的Lore")
        remind = map.getString("物品绑定提示")
        success = map.getString("绑定完成提示")
        dragon = map.getString("龙核绑定提示")
    }

    @EventHandler
    void onDragon(PlayerSlotUpdateEvent e) {
        if (e.identifier == null) {
            for (final def entry in SlotAPI.getCacheAllSlotItem(e.player).entrySet()) {
                if (needBind(entry.value)) {
                    e.player.sendMessage(dragon)
                    return
                }
            }
        } else if (needBind(e.itemStack)) {
            e.player.sendMessage(dragon)
        }
    }

    @EventHandler
    void onClose(InventoryCloseEvent e) {
        for (int i = 36; i <= 40; i++) {
            if (needBind(e.player.inventory.getItem(i))) {
                remind(e.player as Player)
                return
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onHand(PlayerItemHeldEvent e) {
        if (e.cancelled) return

        if (needBind(e.player.inventory.getItem(e.newSlot)))
            remind(e.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onSwap(PlayerSwapHandItemsEvent e) {
        if (e.cancelled) return

        if (needBind(e.mainHandItem) || needBind(e.offHandItem))
            remind(e.player)
    }

    void remind(Player player) {
        player.sendMessage(remind)
    }

    static boolean needBind(ItemStack itemStack) {
        return itemStack != null && itemStack.type != Material.AIR && itemStack.hasItemMeta() && itemStack.itemMeta.hasLore() && itemStack.itemMeta.lore.contains(noBind)
    }
}
