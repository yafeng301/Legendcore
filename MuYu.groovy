package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.lazy.Lazy
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MuYu {

    static Map<String, BukkitRunnable> runnables = new HashMap<>()
    static def lazy = Lazy.of(() -> {
        try {
            return Sound.BLOCK_WOOD_PRESSUREPLATE_CLICK_ON
        } catch (Throwable ignored) {
            return Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON
        }
    })

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("MuYu", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender instanceof Player) {
                    def remove = runnables.remove(sender.name)
                    if (remove == null) {
                        def name = sender.name
                        def runnable = new BukkitRunnable() {
                            @Override
                            void run() {
                                def player = Bukkit.getPlayerExact(name)
                                if (player == null) {
                                    cancel()
                                    runnables.remove(name)
                                    return
                                }

                                player.world.playSound(player.location, lazy.get(), 1.0f, 1.6f)
                                player.sendTitle("§6功德 +1", "§b南无阿弥陀佛")
                            }
                        }
                        runnable.runTaskTimer(0, 10)
                        runnables.put(sender.getName(), runnable)
                    } else {
                        remove.cancel()
                    }
                }
            }
        })
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("MuYu")
        runnables.values().forEach {
            it.cancel()
        }
    }
}
