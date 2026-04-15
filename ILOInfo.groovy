package groovy

import cn.mcdcs.legendcore.api.attribute.AttributeManagerAPI
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ILOInfo {

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("iloinfo", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender.op) {
                    if (args.length > 0) {
                        Player player = Bukkit.getPlayerExact(args[0])
                        if (player == null) {
                            message("&c请选择一个在线的玩家")
                            return
                        }

                        sender.sendMessage(AttributeManagerAPI.instance.getItemLoreOriginHookItem(player).toString().replace("§", "&"))
                        return
                    }
                }
            }
        })
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("iloinfo")
    }
}
