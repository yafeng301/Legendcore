package groovy

import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.nms.tag.TagManager
import cn.mcdcs.legendcore.api.wrapper.map.MapWrapper
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemSetNBT {

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("ItemSetNBT", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender instanceof Player) {
                    def hand = sender.itemInHand
                    if (hand.type == Material.AIR) {
                        message("&c请手持物品")
                        return
                    }

                    def tag = TagManager.getItemTag(hand)
                    if (tag == null) {
                        message("&c不支持修改NBT数据")
                        return
                    }

                    if (args.length > 2) {
                        tag.set(args[0], switch (args[1].toLowerCase()) {
                            case "string" -> args[2]
                            case "int" -> args[2].toInteger()
                            case "double" -> args[2].toDouble()
                            case "long" -> args[2].toLong()
                            case "boolean" -> args[2].toBoolean()
                            default -> ""
                        })
                        TagManager.setItemTag(hand, tag)
                        message("&a修改成功")
                    }

                    return
                }

                message("&c这是一个玩家命令")
            }
        }, "isn", "setnbt")
    }
}
