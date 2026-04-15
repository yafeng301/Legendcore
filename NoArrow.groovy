package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.Pair
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.version.ServerVersion
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.ItemMeta

class NoArrow implements Listener {

    static def listener = new NoArrow()
    static def arrow = new ItemStack(Material.ARROW) {
        {
            ItemMeta meta = itemMeta
            meta.setDisplayName(UUID.randomUUID().toString())
            itemMeta = meta
        }
    }
    static boolean hasOffHand = CoreAPI.version.newerOrEqualAs(ServerVersion.v1_9_R1)

    static void onGroovyRegister() {
        listener.register()
    }

    static void onGroovyUnregister() {
        listener.unregister()
        Bukkit.onlinePlayers.forEach(this::clearArrow)
    }

    static Map<Player, Pair<Integer, ItemStack>> map = new HashMap<>()
    static Set<Entity> arrows = new HashSet<>()

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        clearArrow(e.player)
    }

    @EventHandler
    void onHit(ProjectileHitEvent e) {
        if (arrows.remove(e.entity)) e.entity.remove()
    }

    @EventHandler
    void onBow(EntityShootBowEvent e) {
        if (e.entity instanceof Player)
            if (clearArrow(e.entity as Player)) arrows.add(e.projectile)
    }

    @EventHandler
    void on(PlayerItemHeldEvent e) {
        if (hasOffHand) {
            def item = e.player.inventory.getItem(40)
            if (item != null && (item.type == Material.BOW || item.type.name().equals("CROSSBOW"))) return
        }

        clearArrow(e.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onRight(PlayerInteractEvent e) {
        if (e.player.gameMode == GameMode.CREATIVE) return
        if (!e.action.toString().contains("RIGHT")) return
        if (!e.hasItem()) return
        if (e.item.type != Material.BOW && !e.item.type.name().equals("CROSSBOW")) return

        PlayerInventory inventory = e.player.inventory

        for (ItemStack itemStack : inventory.contents) {
            if (itemStack == null || itemStack.type == Material.AIR) continue
            if (itemStack.type.name().contains("ARROW")) return
        }

        int empty = 27
        ItemStack origin = null
        if (inventory.getItem(empty) != null) {
            empty = inventory.firstEmpty()
            if (empty == -1) {
                empty = 27
                origin = inventory.getItem(empty)
            }
        }

        inventory.setItem(empty, arrow)

        map.put(e.player, new Pair<>(empty, origin))
    }

    static boolean clearArrow(Player player) {
        Pair<Integer, ItemStack> pair = map.remove(player)
        if (pair == null) return false

        player.inventory.setItem(pair.key, pair.value)
        return true
    }
}
