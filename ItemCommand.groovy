package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigurationManagerAPI
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ItemCommand implements Listener {

    static List<ItemConfig> configs = new ArrayList<>()
    static def listener = new ItemCommand()

    @EventHandler
    void onRight(PlayerInteractEvent e) {
        if (!e.action.name().contains("RIGHT")) return

        if (!e.hasItem()) return

        ItemStack item = e.item

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

            e.player.chat(config.chat)
            e.cancelled = true
            break
        }
    }

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        loadConfig()
    }

    static void onGroovyRegister() {
        listener.register()
        loadConfig()
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    static void loadConfig() {
        def config = ConfigurationManagerAPI.instance.loadConfig("ItemCommand", new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("注释", "检测材质删除则不检测材质，名字和Lore雷同，另外 custom 为自定义名字 整个项目不重复即可")
                map.put("commands.custom.检测材质", "Paper")
                map.put("commands.custom.检测名字", "&a右键打开菜单")
                map.put("commands.custom.检测Lore", "&a右键打开菜单")
                map.put("commands.custom.发送聊天", "/menu")
            }
        })

        configs.clear()

        for (String key : config.getConfigurationSection("commands").getKeys(false)) {
            ItemConfig itemConfig = new ItemConfig()
            itemConfig.name = CoreAPI.format(config.getString("commands." + key + ".检测名字", null))
            itemConfig.lore = CoreAPI.format(config.getString("commands." + key + ".检测Lore", null))
            itemConfig.type = CoreAPI.format(config.getString("commands." + key + ".检测材质", null))
            itemConfig.chat = config.getString("commands." + key + ".发送聊天", "")
            configs.add(itemConfig)
        }
    }

    static class ItemConfig {
        String name
        String lore
        String type
        String chat
    }
}
