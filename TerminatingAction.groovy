package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.event.player.PlayerJumpEvent
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerMoveEvent

import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap

class TerminatingAction implements Listener {

    static def runnable = new BukkitRunnable() {
        @Override
        void run() {
            def millis = System.currentTimeMillis()
            cache.values().removeIf { millis > it }
        }
    }

    static def listener = new TerminatingAction()
    static Map<String, Long> cache = new ConcurrentHashMap<>()

    static void onGroovyRegister() {
        runnable.runTaskTimerAsynchronously(0, 20)
        listener.register()
        CommandManagerAPI.instance.register("TerminatingAction", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender.op) {
                    if (args.length > 1) {
                        if (args[0].equalsIgnoreCase("clear")) {
                            def remove = cache.remove(args[1])
                            if (remove == null) {
                                message("&c这个玩家没有被禁止动作")
                            } else {
                                message("&a成功清除玩家禁止动作")
                            }
                            return
                        } else if (args.length > 2) {
                            if (args[0].equalsIgnoreCase("add")) {
                                try {
                                    def time = Double.parseDouble(args[2]) * 1000
                                    cache.put(args[1], cache.getOrDefault(args[1], System.currentTimeMillis()) + (long) time)
                                    message("&a成功添加玩家动作禁止时间&b " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cache.get(args[1])))
                                } catch (NumberFormatException ignored) {
                                    message("&c请输入一个正确的数字")
                                }
                                return
                            } else if (args[0].equalsIgnoreCase("set")) {
                                try {
                                    def time = Double.parseDouble(args[2]) * 1000
                                    cache.put(args[1], System.currentTimeMillis() + (long) time)
                                    message("&a成功设置玩家动作禁止时间&b " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cache.get(args[1])))
                                } catch (NumberFormatException ignored) {
                                    message("&c请输入一个正确的数字")
                                }
                                return
                            } else if (args[0].equalsIgnoreCase("take")) {
                                def now = cache.get(args[1])
                                if (now == null) {
                                    message("&c对应玩家没有被动作禁止,无法进行扣除")
                                } else {
                                    try {
                                        def time = Double.parseDouble(args[2]) * 1000
                                        cache.put(args[1], cache.getOrDefault(args[1], System.currentTimeMillis()) - (long) time)
                                        message("&a成功扣除玩家动作禁止时间&b " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cache.get(args[1])))
                                    } catch (NumberFormatException ignored) {
                                        message("&c请输入一个正确的数字")
                                    }
                                }
                                return
                            }
                        }
                    }
                    help("",
                            "add &b<玩家名> <秒> &a增加玩家动作禁止的时间",
                            "set &b<玩家名> <秒> &a设置玩家动作禁止的时间",
                            "take &b<玩家名> <秒> &a减少玩家动作禁止的时间",
                            "clear &b<玩家名> &a清除玩家动作禁止的时间")
                }
            }
        }, "ta", "tac", "tan")
    }

    static void onGroovyUnregister() {
        runnable.cancel()
        listener.unregister()
        CommandManagerAPI.instance.unregister("TerminatingAction")
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onJump(PlayerJumpEvent e) {
        if (e.cancelled) return
        if (e.player.op) return
        if (cache.containsKey(e.player.name)) e.cancelled = true

    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onMove(PlayerMoveEvent e) {
        if (e.cancelled) return
        if (e.player.op) return
        if (e.to == null) return
        if (e.from.x != e.to.x || e.from.z != e.to.z) {
            if (cache.containsKey(e.player.name)) e.cancelled = true
        }
    }

    @EventHandler
    void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.cancelled) return
        if (e.player.op) return
        if (cache.containsKey(e.player.name)) e.cancelled = true
    }
}
