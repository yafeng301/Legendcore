import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.TranslateAPI
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.nms.NMSProvider
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ItemShow {

    static def proxy = new ConfigProxy("ItemShow")

    static void onGroovyRegister() {
        proxy.loadConfig(map -> {
            map.put("content", "玩家 <p> 展示了物品 <i>")
        })

        CommandManagerAPI.instance.register("ItemShow", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender instanceof Player) {
                    def hand = sender.itemInHand
                    if (hand == null || hand.type == Material.AIR) {
                        message("&c请手持物品展示！")
                        return
                    }

                    if (!NMSProvider.available) {
                        message("&c物品展示不受支持不可用")
                        return
                    }

                    def tag = NMSProvider.instance.getItemRootTag(hand)
                    if (tag == null) {
                        message("&c物品展示失败，请检查物品是否正常")
                        return
                    }

                    TextComponent component = new TextComponent(CoreAPI.format(proxy.config
                            .getString("content")
                            .replace("<p>", sender.name)
                            .replace("<i>", TranslateAPI.convertShowName(hand))))

                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                    TextComponent.fromLegacyText(tag.toString())))

                    for (Player player : Bukkit.onlinePlayers)
                        player.spigot().sendMessage(component)
                }
            }
        }, "is", "i")
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("ItemShow")
    }
}
