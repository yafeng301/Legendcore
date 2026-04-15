package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GuciBindCommand {

    static def string = CoreAPI.format("&a绑定状态:&b 未绑定")

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("bind", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender instanceof Player) {
                    ItemStack item = sender.itemInHand
                    if (item == null || item.type == Material.AIR) {
                        message("&c请手持物品绑定")
                        return
                    }

                    if (!item.hasItemMeta() || !item.itemMeta.hasLore() ||
                    !item.itemMeta.lore.contains(string)) {
                        message("&c这个物品无需绑定")
                        return
                    }

                    if (sender.op) {
                        sender.performCommand("GuciBind item bind")
                    } else {
                        sender.op = true
                        try {
                            sender.performCommand("GuciBind item bind")
                        } catch (Throwable ignored) {

                        }
                        sender.op = false
                    }
                } else {
                    message("&c这是一个玩家命令")
                }
            }
        })
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("bind")
    }
}
