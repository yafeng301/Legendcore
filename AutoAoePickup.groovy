package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent

class AutoAoePickup extends BukkitRunnable {

    static def runnable = new AutoAoePickup()
    static def proxy = new ConfigProxy("AutoAoePickup")

    static void onGroovyRegister() {
        runnable.runTaskTimer(0,1)
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("每隔多久检测一次(20=1秒)", 10)
                map.put("拾取范围", 2D)
            }
        })
    }

    static void onGroovyUnregister() {
        runnable.cancel()
    }

    long tick = 0

    @Override
    void run() {
        tick++

        if (tick >= proxy.config.getLong("每隔多久检测一次(20=1秒)")) {

            double range = proxy.config.getDouble("拾取范围")

            for (Player player : Bukkit.onlinePlayers) {
                boolean hasPlayer = false

                List<Item> items = new ArrayList<>()

                for (Entity entity : player.getNearbyEntities(range, range, range)) {
                    if (entity instanceof Player) {
                        hasPlayer = true
                        break
                    }

                    if (entity instanceof Item) {
                        items.add(entity)
                    }
                }

                if (hasPlayer) continue

                for (Item item : items) {
                    if (player.inventory.firstEmpty() == -1) break

                    PlayerPickupItemEvent pe = new PlayerPickupItemEvent(player, item, 0)
                    Bukkit.pluginManager.callEvent(pe)
                    if (pe.cancelled) continue

                    if (pe.item.dead || !pe.item.valid) continue
                    if (pe.item.itemStack == null || pe.item.itemStack.amount == 0) continue

                    EntityPickupItemEvent ee = new EntityPickupItemEvent(player, item, 0)
                    Bukkit.pluginManager.callEvent(ee)
                    if (ee.cancelled) continue

                    if (ee.item.dead || !ee.item.valid) continue
                    if (ee.item.itemStack == null || ee.item.itemStack.amount == 0) continue

                    item.remove()
                    player.inventory.addItem(item.itemStack)
                }
            }

            tick = 0
        }
    }
}
