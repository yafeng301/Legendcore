package groovy

import cn.mcdcs.legendcore.api.TranslateAPI
import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.Pair
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderHook
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask

import java.text.DecimalFormat
import java.util.concurrent.ConcurrentHashMap

class DamageBossBar implements Listener {

    static def listener = new DamageBossBar()
    static Map<Player, Map<Entity, Pair<BossBar, Long>>> map = new ConcurrentHashMap<>()
    static ConfigProxy proxy = new ConfigProxy("DamageBossBar")
    static BukkitTask task

    @EventHandler(priority = EventPriority.MONITOR)
    void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.entity instanceof LivingEntity)) return

        LivingEntity entity = e.entity as LivingEntity
        Player player = null

        if (e.damager instanceof Player) player = e.damager as Player
        if (e.damager instanceof Projectile)
            if (((Projectile) e.damager).shooter instanceof Player)
                player = (e.damager as Projectile).shooter as Player

        if (player == null) return

        if (proxy.config.getBoolean("目标限制.是否开启")) {
            if (proxy.config.getBoolean("目标限制.白名单.是否开启")) {
                boolean notFound = true
                for (String text : proxy.config.getStringList("目标限制.白名单.实体类型列表")) {
                    if (text.equalsIgnoreCase(entity.type.name())) {
                        notFound = false
                        break
                    }
                }
                if (notFound) return
            }
            if (proxy.config.getBoolean("目标限制.黑名单.是否开启")) {
                for (String text : proxy.config.getStringList("目标限制.黑名单.实体类型列表")) {
                    if (text.equalsIgnoreCase(entity.type.name())) return
                }
            }
        }

        String name
        if (entity instanceof Player) name = entity.name
        else if (entity.customName != null) name = entity.customName
        else {
            def convert = TranslateAPI.convert(entity.type)
            name = convert == null ? entity.type.name : convert
        }

        Map<Entity, Pair<BossBar, Long>> map = map.computeIfAbsent(player, k -> new ConcurrentHashMap<>())

        Pair<BossBar, Long> pair = map.get(entity)

        if (pair == null) pair = new Pair<>(Bukkit.createBossBar("",
                BarColor.valueOf(proxy.config.getString("bossbar-color").toUpperCase()),
                BarStyle.valueOf(proxy.config.getString("bossbar-style").toUpperCase())), 0L)

        BossBar bar = pair.key

        DecimalFormat df = new DecimalFormat("0.0#")

        double health = Math.max(entity.health - e.finalDamage, 0)

        if (health > entity.maxHealth) health = entity.maxHealth

        if (Double.isNaN(health)) health = 0

        def damage = e.finalDamage
        if (Double.isNaN(damage)) damage = 0

        String title = CoreAPI.format(proxy.config.getString("bossbar-title")
                .replace("<name>", name)
                .replace("<health>", df.format(health))
                .replace("<maxhealth>", df.format(entity.maxHealth))
                .replace("<damage>", df.format(damage)))

        bar.setTitle(PlaceholderHook.handle(player, title))

        double progress = health / entity.maxHealth
        if (Double.isNaN(progress)) progress = 0
        bar.setProgress(progress)
        bar.addPlayer(player)

        pair.value = System.currentTimeMillis() + proxy.config.getLong("持续时间(毫秒)")

        map.put(entity, pair)
    }

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        map.remove(e.player)?.values()?.forEach { it.key.removeAll()}
    }

    static void onGroovyRegister() {
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("bossbar-title", "&a<name> &e<health>&f/&e<maxhealth> &f(&c-<damage>)")
                map.put("bossbar-color", "RED")
                map.put("bossbar-style", "SOLID")
                map.put("持续时间(毫秒)", 3000L)
                map.put("目标限制.是否开启", false)
                map.put("目标限制.白名单.是否开启", false)
                map.put("目标限制.白名单.实体类型列表", ["Player"])
                map.put("目标限制.黑名单.是否开启", false)
                map.put("目标限制.黑名单.实体类型列表", ["Zombie"])
            }
        })
        listener.register()
        task = new BukkitRunnable() {
            @Override
            void run() {
                map.values().forEach{
                    it.values().removeIf { pair ->
                        if (System.currentTimeMillis() > pair.value) {
                            pair.key.removeAll()
                            return true
                        }
                        return false
                    }
                }
            }
        }.runTaskTimerAsynchronously(0, 1)
    }

    // 脚本被卸载的时候执行的
    // 控制注册的事件停止运行等等
    static void onGroovyUnregister() {
        map.values().forEach { it.values().forEach { it.key.removeAll()}}
        map.clear()
        listener.unregister()
        task.cancel()
        proxy = null
    }

}
