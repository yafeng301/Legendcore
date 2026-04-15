package groovy

import cn.mcdcs.legendcore.api.command.CommandExecutor
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.nms.serializer.ItemSerializer
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ItemToNBT {

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("ItemToNBT", new CommandExecutor() {
            @Override
            void onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender.op) {
                    if (sender instanceof Player) {
                        def hand = sender.itemInHand
                        if (hand == null || hand.type == Material.AIR) {
                            sender.sendMessage("请手持物品！")
                            return
                        }

                        sender.sendMessage("物品NBT信息: " + ItemSerializer.serialize(hand).replace("§","&"))
                        return
                    }

                    sender.sendMessage("只有玩家才能执行命令")
                }
            }
        }, "itn")
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("ItemToNBT")
    }
}