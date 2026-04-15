package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Track extends BukkitRunnable {

    static def task = new Track()
    static Map<String, String> data = new HashMap<>()

    static void onGroovyRegister() {
        task.runTaskTimer(0, 2)

        CommandManagerAPI.instance.register("track", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender instanceof Player) {
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase("cancel")) {
                            def remove = data.remove(sender.name)
                            if (remove == null) message("&c你没有正在追踪的玩家")
                            else message("&a已取消追踪玩家 &b" + remove)
                            return
                        } else {
                            def player = Bukkit.getPlayerExact(args[0])
                            if (player == null) {
                                message("&c玩家不存在")
                                return
                            }

                            data.put(sender.name, player.name)
                            message("&a已追踪玩家 &b" + player.name)
                            return
                        }
                    }
                    help("",
                            "<玩家名> &a追踪玩家",
                            "cancel &a取消追踪"
                    )
                } else {
                    message("&c这是一个玩家命令")
                }
            }
        })
    }

    static void onGroovyUnregister() {
        task.cancel()
    }

    @Override
    void run() {
        for (Player player in Bukkit.onlinePlayers) {
            def name = data.get(player.name)
            if (name == null) continue

            def target = Bukkit.getPlayerExact(name)
            if (target == null) continue

            def location = target.location
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                    "&a追踪玩家&b " + target.name + " &a坐标&b " + location.world.name + " " + location.x + " " + location.y + " " + location.z
            ))
        }
    }
}
