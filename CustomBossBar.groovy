package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderHook
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask

class CustomBossBar implements Listener {

    static def listener = new CustomBossBar()
    static Map<Player, BossBar> bars = new HashMap<>()
    static BukkitTask task
    static ConfigProxy proxy = new ConfigProxy("CustomBossBar")

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        String text = CoreAPI.format(PlaceholderHook.setPlaceholders(e.player,
                proxy.config.getString("bossbar-title", "").replace("<p>", e.player.name)))

        BossBar bar = Bukkit.createBossBar(text, BarColor.valueOf(proxy.config.getString("bossbar-color").toUpperCase()),
                BarStyle.valueOf(proxy.config.getString("bossbar-style").toUpperCase()))
        bar.setProgress(proxy.config.getDouble("bossbar-progress"))
        bar.addPlayer(e.player)

        if (proxy.config.getBoolean("自定义进度条.是否开启")) {
            def context = PlaceholderHook.setPlaceholders(e.player, proxy.config.getString("自定义进度条.内容"))
            def split = context.split("/")
            try {
                double progress = Double.parseDouble(split[0]) / Double.parseDouble(split[1])
                bar.progress = Math.max(Math.min(1 , progress), 0)
            } catch (NumberFormatException ignored) {

            }
        }

        bars.put(e.player, bar)
    }

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        bars.remove(e.player)?.removeAll()
    }

    static void onGroovyRegister() {
        listener.register()
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("bossbar-title", "&b传奇核心 CustomBossBar &a<p> &eLv. %player_level%")
                map.put("bossbar-color", "RED")
                map.put("bossbar-style", "SOLID")
                map.put("bossbar-progress", 1D)
                map.put("刷新间隔(20=1秒)", 20L)
                map.put("自定义进度条.是否开启", false)
                map.put("自定义进度条.内容", "%player_health%/%player_max_health%")
            }
        })
        task = new BukkitRunnable() {

            long tick = 0

            @Override
            void run() {
                tick += 1
                if (tick >= proxy.config.getLong("刷新间隔(20=1秒)")) {
                    for (Player player : Bukkit.onlinePlayers) {
                        String text = CoreAPI.format(PlaceholderHook.setPlaceholders(player,
                                proxy.config.getString("bossbar-title", "").replace("<p>", player.name)))


                        def bar = bars.computeIfAbsent(player, k -> {
                            BossBar bar = Bukkit.createBossBar("", BarColor.valueOf(proxy.config.getString("bossbar-color").toUpperCase()),
                                    BarStyle.valueOf(proxy.config.getString("bossbar-style").toUpperCase()))
                            bar.setProgress(proxy.config.getDouble("bossbar-progress"))
                            bar.addPlayer(player)
                            return bar
                        })
                        bar.setTitle(text)

                        if (proxy.config.getBoolean("自定义进度条.是否开启")) {
                            def context = PlaceholderHook.setPlaceholders(player, proxy.config.getString("自定义进度条.内容"))
                            def split = context.split("/")
                            try {
                                double progress = Double.parseDouble(split[0]) / Double.parseDouble(split[1])
                                bar.progress = Math.max(Math.min(1 , progress), 0)
                            } catch (NumberFormatException ignored) {

                            }
                        }
                    }
                    tick = 0
                }
            }
        }.runTaskTimer(0, 1)
    }

    static void onGroovyUnregister() {
        bars.values().forEach {it.removeAll()}
        listener.unregister()
        task.cancel()
    }
}
