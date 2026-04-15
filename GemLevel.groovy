package groovy

import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.nms.tag.TagManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GemLevel {

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("GemLevel", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender instanceof Player) {
                    def hand = sender.getItemInHand()
                    def tag = TagManager.getItemTag(hand)

                    tag.set("GemLevel", Integer.parseInt(args[0]))
                    TagManager.setItemTag(hand, tag)

                    sender.sendMessage("设置成功")
                }
            }
        })
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("GemLevel")
    }
}
