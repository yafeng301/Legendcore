package groovy

import cn.mcdcs.legendcore.api.command.CommandExecutor
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ItemToString {

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("ItemToString", new CommandExecutor() {
            @Override
            void onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender.op) {
                    if (sender instanceof Player) {
                        def hand = sender.itemInHand
                        if (hand == null || hand.type == Material.AIR) {
                            sender.sendMessage("请手持物品！")
                            return
                        }

                        sender.sendMessage("物品信息: " + hand.toString().replace("§","&"))
                        return
                    }

                    sender.sendMessage("只有玩家才能执行命令")
                }
            }
        }, "its")
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("ItemToString")
    }
}
