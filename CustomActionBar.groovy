package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderHook
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CustomActionBar extends BukkitRunnable {

    static def proxy = new ConfigProxy("CustomActionBar")
    static def runnable = new CustomActionBar()

    static void onGroovyRegister() {
        runnable.runTaskTimer(0, 1)

        proxy.loadConfig((map) -> {
            map.put("显示内容", "&6&lLegendCore &f&l>>> &a&l玩家血量 &b&l%player_health%&f&l/&b&l%player_max_health% &a&l玩家等级 &b&l%player_level%")
            map.put("更新频率(tick=1/20秒)", 40)
        })
    }

    static void onGroovyUnregister() {
        runnable.cancel()
    }

    long tick = 0

    @Override
    void run() {
        tick = tick + 1

        if (tick >= proxy.config.getLong("更新频率(tick=1/20秒)")) {
            for (Player player : Bukkit.onlinePlayers)
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                        CoreAPI.format(PlaceholderHook.setPlaceholders(player, proxy.config.getString("显示内容")))))

            tick = 0
        }
    }
}
