package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HyperBossBar {

    static Set<BukkitRunnable> tasks = new HashSet<>()

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("HyperBossBar",new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender.op) {
                    StringBuilder builder = new StringBuilder()
                    for (int i = 0; i < 40; i++) builder.append(" ")
                    boolean b = false
                    for (String text : args) {
                        if (b) builder.append(" ")
                        builder.append(text)
                        b = true
                    }

                    BossBar bar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID)
                    for (Player player : Bukkit.onlinePlayers) bar.addPlayer(player)
                    bar.show()

                    def runnable = new BukkitRunnable() {

                        int line = 0
                        char[] chars = CoreAPI.format(builder.toString()).chars
                        int index = 0

                        @Override
                        void run() {
                            builder = new StringBuilder()
                            for (int i = 0; i < 40; i++) {
                                int index = index + i
                                if (index >= chars.length) {
                                    if (line == 2) builder.append(" ")
                                    else builder.append("§").append((int) (Math.random() * 10)).append(chars[index - chars.length])
                                } else builder.append("§").append((int) (Math.random() * 10)).append(chars[index])
                            }

                            bar.title = builder.toString()

                            index++

                            if (line == 2 && index == chars.length) {
                                cancel()
                                tasks.remove(this)
                                bar.hide()
                            }

                            if (index == chars.length) {
                                index = 0
                                line++
                            }
                        }
                    }
                    runnable.runTaskTimer(0, 2)
                    tasks.add(runnable)
                }
            }
        }, "hbb", "hbar")
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("HyperBossBar")
        tasks.forEach { it.cancel()}
    }
}
