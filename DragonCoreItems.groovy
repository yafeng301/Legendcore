package groovy

import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import eos.moe.dragoncore.api.SlotAPI
import eos.moe.dragoncore.database.IDataBase
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class DragonCoreItems {

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("dci", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (!sender.isOp()) return
                if (!(sender instanceof Player)) {
                    message("&c这是一个玩家才能执行的命令")
                    return
                }
                if (args.length > 0) {
                    def player = Bukkit.getPlayerExact(args[0])
                    if (player == null) {
                        message("&c对应玩家不在线！")
                        return
                    }

                    message("&a开始获取玩家的龙核数据,请稍后！")
                    SlotAPI.getAllSlotItem(player, new IDataBase.Callback<Map<String, ItemStack>>() {
                        @Override
                        void onResult(Map<String, ItemStack> map) {
                            player = sender as Player
                            boolean b = false
                            map.entrySet().forEach {
                                if (player.inventory.firstEmpty() == -1) {
                                    b = true

                                    player.world.dropItemNaturally(player.location, it.value)
                                } else {
                                    player.inventory.addItem(it.value)
                                }
                            }
                            message("&a玩家数据获取成功！" + b ? " &b(背包已满部分在地上)" : "")
                        }

                        @Override
                        void onFail() {
                            message("&c玩家数据获取失败！")
                        }
                    })
                }
                help("&b<玩家名> &a获取对应玩家的所有龙核物品！")
            }
        })
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("dci")
    }
}
