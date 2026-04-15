package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigurationManagerAPI
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import cn.mcdcs.legendcore.api.item.ItemManagerAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack

class ItemPermanent implements Listener{

    static def task
    static def listener = new ItemPermanent()
    static List<ItemConfig> configs = new ArrayList<>()
    static long interval

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        loadConfig()
    }

    static void onGroovyRegister() {
        loadConfig()

        long tick = 0

        task = new BukkitRunnable() {
            @Override
            void run() {
                tick++

                if (tick >= interval) {
                    Map<Player, List<ItemConfig>> updates = new HashMap<>()

                    for (Player player : Bukkit.onlinePlayers) {

                        List<ItemConfig> configs = new ArrayList<>(configs)

                        for (ItemStack item : player.inventory.contents) {
                            if (item == null || item.type == Material.AIR) continue

                            String type = item.type.name()
                            String name = ""
                            List<String> lore = new ArrayList<>()

                            if (item.hasItemMeta()) {
                                if (item.itemMeta.hasDisplayName()) name = item.itemMeta.displayName
                                if (item.itemMeta.hasLore()) lore = item.itemMeta.lore
                            }

                            for (ItemConfig config : configs) {
                                if (config.type != null && !config.type.equalsIgnoreCase(type)) continue
                                if (config.name != null && !config.name.equalsIgnoreCase(name)) continue
                                if (config.lore != null && !lore.contains(config.lore)) continue

                                configs.remove(config)
                                break
                            }

                            if (configs.isEmpty()) break
                        }

                        if (configs.isEmpty()) continue

                        updates.put(player, configs)
                    }

                    if (!updates.isEmpty()) {
                        new BukkitRunnable() {
                            @Override
                            void run() {
                                for (Map.Entry<Player, List<ItemConfig>> entry : updates.entrySet()) {
                                    for (ItemConfig config : entry.getValue()) {
                                        entry.getKey().getInventory().addItem(config.item.clone())
                                    }
                                }
                            }
                        }.runTask()
                    }

                    tick = 0
                }
            }
        }.runTaskTimerAsynchronously(0,1)

        listener.register()
    }

    static void onGroovyUnregister() {
        task.cancel()
        listener.unregister()
    }

    static void loadConfig() {
        def config = ConfigurationManagerAPI.instance.loadConfig("ItemPermanent", new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("注释", "检测材质删除则不检测材质，名字和Lore雷同，另外 custom 为自定义名字 整个项目不重复即可")
                map.put("关于给予物品", "具体查看 0.1.3 的更新记录 兼容12款插件")
                map.put("每隔多久检测一次(20tick=1s)", 50)
                map.put("commands.custom.检测材质", "Paper")
                map.put("commands.custom.检测名字", "&a右键打开菜单")
                map.put("commands.custom.检测Lore", "&a右键打开菜单")
                map.put("commands.custom.给予物品", "mm-菜单")
            }
        })

        interval = config.getLong("每隔多久检测一次(20tick=1s)", 20)

        configs.clear()

        for (String key : config.getConfigurationSection("commands").getKeys(false)) {
            ItemStack itemStack = ItemManagerAPI.instance.getHookItem(config.getString("commands." + key + ".给予物品", ""))
            if (itemStack == null) continue
            ItemConfig itemConfig = new ItemConfig()
            itemConfig.name = CoreAPI.format(config.getString("commands." + key + ".检测名字", null))
            itemConfig.lore = CoreAPI.format(config.getString("commands." + key + ".检测Lore", null))
            itemConfig.type = CoreAPI.format(config.getString("commands." + key + ".检测材质", null))
            itemConfig.item = itemStack
            configs.add(itemConfig)
        }
    }

    static class ItemConfig {
        String name
        String lore
        String type
        ItemStack item
    }
}
