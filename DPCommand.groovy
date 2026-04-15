package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.serverct.ersha.dungeon.common.api.event.DungeonEvent
import org.serverct.ersha.dungeon.common.api.event.dungeon.DungeonEndEvent
import org.serverct.ersha.dungeon.common.api.event.dungeon.DungeonStartEvent
import org.serverct.ersha.dungeon.common.team.type.PlayerStateType

class DPCommand implements Listener {

    static def listener = new DPCommand()
    static def proxy = new ConfigProxy("DPCommand")

    static void onGroovyRegister() {
        listener.register()
        proxy.loadConfig(map -> {
            map.put("地牢启动", ["eco give <p> 100"])
            map.put("地牢结束", ["eco give <p> 100"])
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onDungeon(DungeonEvent e) {
        if (e.event instanceof DungeonStartEvent.Before) {
            for (final def player in e.dungeon.team.getPlayers(PlayerStateType.ONLINE))
                proxy.config.getStringList("地牢启动").forEach {
                    Bukkit.dispatchCommand(Bukkit.consoleSender, it.replace("<p>", player.name))
                }
        } else if (e.event instanceof  DungeonEndEvent.After) {
            for (final def player in e.dungeon.team.getPlayers(PlayerStateType.ONLINE))
                proxy.config.getStringList("地牢结束").forEach {
                    Bukkit.dispatchCommand(Bukkit.consoleSender, it.replace("<p>", player.name))
                }
        }
    }
}
