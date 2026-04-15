package groovy

import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack

class KLRing {

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("WareKLRing", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (args.length > 0) {
                    File folder = new File("plugins/KLRing/data/soul-space")
                    if (!folder.exists()) {
                        message("&c未检测到KLRing插件数据")
                        return
                    }

                    File ware = new File("plugins/LegendWarehouse/data")
                    if (!ware.exists()) {
                        message("&c未检测到LegendWarehouse插件数据")
                        return
                    }

                    message("&a开始转换请稍后...")

                    long players = 0
                    long count = 0

                    for (final def file in folder.listFiles()) {
                        players++

                        String name = file.name
                        name = name.substring(0, name.lastIndexOf("."))

                        File dataFolder = new File(ware, name)
                        if (!dataFolder.exists()) dataFolder.mkdirs()

                        File itemFile = new File(dataFolder, "item/" + args[0] + ".yml")
                        if (!itemFile.exists()) {
                            itemFile.parentFile.mkdirs()
                            itemFile.createNewFile()
                        }

                        File wareFile = new File(dataFolder, "ware/" + args[0] + ".yml")
                        if (!wareFile.exists()) {
                            wareFile.parentFile.mkdirs()
                            wareFile.createNewFile()
                        }

                        YamlConfiguration itemConfig = YamlConfiguration.loadConfiguration(itemFile)
                        YamlConfiguration wareConfig = YamlConfiguration.loadConfiguration(wareFile)
                        YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(file)

                        long index = wareConfig.getLong("index")

                        def items = oldConfig.getConfigurationSection("stored-items")
                        for (final def key in items.getKeys(false)) {


                            def section = items.getConfigurationSection(key)
                            long amount = section.getLong("stored-amount")
                            if (amount <= 0) continue

                            Map<String, Object> map = new HashMap<>()
                            for (final def string in section.getKeys(false)) {
                                map.put(string, section.get(string))
                            }

                            def item = ItemStack.deserialize(map)

                            index++
                            count++

                            itemConfig.set("items." + index + ".item", item)
                            itemConfig.set("items." + index + ".amount", amount)
                        }

                        wareConfig.set("index", index)

                        wareConfig.save(wareFile)
                        itemConfig.save(itemFile)
                    }

                    message("&a成功转换完成,共计玩家&b " + players + " &a物品个数&b " + count)
                    return
                }

                help("<仓库名> &a转换KLRing数据到传奇仓库")
                message("&c注意:&b 请保证服务器未使用传奇仓库再进行转换！")
            }
        })
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("WareKLRing")
    }
}
