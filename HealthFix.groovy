package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.nms.NMSProvider
import cn.mcdcs.legendcore.api.nms.tag.TagManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitTask

class HealthFix implements Listener {

    static BukkitTask task
    static def listener = new HealthFix()

    static void onGroovyRegister() {
        if (!NMSProvider.available) return
        listener.register()
        task = new BukkitRunnable() {
            @Override
            void run() {
                Bukkit.getOnlinePlayers().forEach { execute(it)}
            }
        }.runTaskTimer(0, 200) // 注意: 该办法只能通过主线程运行,无法异步执行！如果服务器性能不够,那就调高一点即可！ 200 = 10秒
    }

    static void onGroovyUnregister() {
        if (!NMSProvider.available) return
        listener.unregister()
        task.cancel()
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        execute(e.player)
    }

    static void execute(Player player) {
        def tag = TagManager.getEntityTag(player)
        if (Double.isNaN(tag.getDouble("Health"))) {
            tag.set("AbsorptionAmount", 0.0f)
            tag.set("Health", 1.0f)
            TagManager.setEntityTag(player, tag)
        }
    }
}
