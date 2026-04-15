package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderHook
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobLootDropEvent
import io.lumine.xikage.mythicmobs.drops.DropMetadata
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler

class ExtraDrops implements Listener {
    static def 传奇映射词条名字 = "掉落提升"

    static def listener = new ExtraDrops()

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onDrop(MythicMobLootDropEvent e) {
        if (e.killer instanceof Player) {
            Player player = e.killer as Player
            double value = Double.parseDouble(PlaceholderHook.setPlaceholders(player, "%LegendMapping_max_$传奇映射词条名字%")) / 100
            int count = (int) value
            if (Math.random() < value % 1) count++

            def metadata = e.drops.metadata
            for (int i = 0; i < count; i++) {
                def data = new DropMetadata(metadata.caster, metadata.trigger)
                def generate = e.mobType.dropTable.generate(data)

                e.drops.add(data, generate)
            }
        }
    }

    @EventHandler
    void onDeath(MythicMobDeathEvent e) {
        
    }
}
