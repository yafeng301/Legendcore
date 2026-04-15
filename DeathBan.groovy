package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.reflect.Reflects
import cn.mcdcs.legendcore.api.reflect.accessors.DefaultMethodAccessor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent

class DeathBan implements Listener {

    static def listener = new DeathBan()
    static DefaultMethodAccessor ban = null
    static DefaultMethodAccessor banPlayer

    static String 封禁原因 = CoreAPI.format("&a封禁通知\n&c你在服务器中死亡,已经被永久封禁！")

    static void onGroovyRegister() {
        listener.register()

        try {
            ban = Reflects.getDefaultMethod(Player.class, "ban", String.class, Date.class, String.class, boolean.class)
        } catch (NoSuchMethodException ignored) {
            banPlayer = Reflects.getDefaultMethod(Player.class, "banPlayer", String.class)
        }
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onDeath(PlayerDeathEvent e) {
        new BukkitRunnable() {
            @Override
            void run() {
                if (ban == null) banPlayer.invoke(e.entity, 封禁原因)
                else ban.invoke(e.entity, 封禁原因, null, null, true)
            }
        }.runTask()
    }
}
