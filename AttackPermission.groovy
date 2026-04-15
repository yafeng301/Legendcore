package groovy

import cn.mcdcs.legendcore.api.Pair
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderProcessor
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.types.PermissionNode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.scheduler.BukkitTask

class AttackPermission extends PlaceholderProcessor implements Listener {

    static def listener = new AttackPermission()
    static def proxy = new ConfigProxy("AttackPermission")
    static List<Pair<Integer, Integer>> time = new ArrayList<>()
    static BukkitTask task
    static boolean canPVP = false

    static void onGroovyRegister() {
        listener.register()
        listener.onRegister()

        proxy.loadConfig(map -> {
            map.put("time", ["21:30-23:59", "0:0-6:30"])
        })

        task = new BukkitRunnable() {
            @Override
            void run() {
                def instance = Calendar.instance

                int nowMinute = instance.get(Calendar.HOUR_OF_DAY) * 60 + instance.get(Calendar.MINUTE)
                for (final def pair in time) {
                    if (nowMinute >= pair.key && nowMinute <= pair.value) {
                        canPVP = false
                        return
                    }
                }

                canPVP = true
            }
        }.runTaskTimer(0, 100)
    }

    static void onGroovyUnregister() {
        listener.unregister()
        listener.onUnregister()

        task.cancel()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onDamage(EntityDamageByEntityEvent e) {
        if (canPVP) return

        if (e.entity instanceof Player) {
            Player player = null
            if (e.damager instanceof Player) player = e.damager as Player
            else if (e.damager instanceof Projectile) {
                if (((Projectile) e.damager).shooter instanceof Player) player = ((Projectile) e.damager).shooter as Player
            }

            if (player == null) return

            e.cancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onAttack(EntityDamageByEntityEvent e) {
        if (canPVP) return

        if (e.cancelled) return

        if (e.entity instanceof Player) {
            Player player = null
            if (e.damager instanceof Player) player = e.damager as Player
            else if (e.damager instanceof Projectile) {
                if (((Projectile) e.damager).shooter instanceof Player) player = ((Projectile) e.damager).shooter as Player
            }

            if (player == null) return

            e.cancelled = true
        }
    }

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        time.clear()
        for (final def string in proxy.config.getStringList("time")) {
            def split = string.split("-")
            time.add(new Pair<Integer, Integer>(asMinute(split[0]), asMinute(split[1])))
        }
    }

    @Override
    String execute(OfflinePlayer player, String originParams, String[] args) {
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("info")) {
                def user = LuckPermsProvider.get().userManager.getUser(player.uniqueId)
                if (user == null) return "无"

                for (final def node in user.nodes) {
                    if (node instanceof PermissionNode) {
                        if (node.key == args[1]) {
                            if (node.hasExpiry()) {
                                long time = node.expiry.toEpochMilli() - System.currentTimeMillis()
                                if (time < 0) return "永久有效"

                                return formatMillis(time)
                            } else {
                                return "永久有效"
                            }
                        }
                    }
                }
                return "无"
            }
        }
        return ""
    }

    @Override
    String getIdentifier() {
        return "LPT"
    }

    static int asMinute(String string) {
        def split = string.split(":")
        return Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1])
    }

    static String formatMillis(long millis) {
        if (millis <= 1000) {
            return "1秒"
        }

        long totalSeconds = (long) (millis / 1000)
        long hours = (long) (totalSeconds / 3600)
        long minutes = (long) ((totalSeconds % 3600) / 60)
        long seconds = totalSeconds % 60

        StringBuilder sb = new StringBuilder()

        if (hours > 0) {
            sb.append(hours).append("时")
        }
        if (minutes > 0 || hours > 0) {
            sb.append(String.format("%02d分", minutes))
        }
        sb.append(String.format("%02d秒", seconds))

        return sb.toString()
    }
}
