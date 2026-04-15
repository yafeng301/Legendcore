package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import io.rokuko.azureflow.features.item.factory.AzureFlowItemFactory
import io.rokuko.azureflow.features.item.factory.AzureFlowItemFactoryService
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AzureFlowFinder {

    static Map<String, AzureFlowItemFactory> map = AzureFlowItemFactoryService.INSTANCE.container

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("AzureFlowFinder", new CommandExecutorBox() {
            @Override
            String prefix() {
                return "&3AzureFlowFinder &f>>> "
            }

            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (args.length > 0) {
                    List<String> names = new ArrayList<>()
                    List<String> lores = new ArrayList<>()
                    List<String> types = new ArrayList<>()

                    for (String text : args) {
                        def index = text.indexOf(":")
                        if (index == -1) continue

                        switch (text.substring(0, index).toLowerCase()) {
                            case "name" -> names.add(text.substring(index + 1, text.length()))
                            case "lore" -> lores.add(text.substring(index + 1, text.length()))
                            case "type" -> types.add(text.substring(index + 1, text.length()).toUpperCase())
                        }
                    }

                    if (names.isEmpty() && lores.isEmpty() && types.isEmpty()) {
                        message("请至少输入一个条件！")
                        return
                    }

                    message("&a正在全力搜索中...")
                    List<AzureFlowItemFactory> list = new ArrayList<>(map.values())
                    List<AzureFlowItemFactory> found = new ArrayList<>()
                    new BukkitRunnable() {
                        @Override
                        void run() {
                            main: for (AzureFlowItemFactory factory : list) {
                                if (factory.displayName == null && !names.isEmpty()) continue
                                if (factory.lore == null && !lores.isEmpty()) continue
                                if (factory.material == null && !types.isEmpty()) continue

                                if (!names.isEmpty()) for (String text : names) if (!factory.displayName.contains(text)) continue main

                                if (!lores.isEmpty()) {
                                    String lore = factory.lore.toString()
                                    for (String text : lores) if (!lore.contains(text)) continue main
                                }

                                if (!types.isEmpty()) {
                                    String type = factory.material.toUpperCase()
                                    for (String text : types) if (!type.contains(text)) continue main
                                }

                                found.add(factory)
                            }

                            if (found.isEmpty()) {
                                sender.sendMessage(CoreAPI.format(prefix() + "&c根据条件未查询到任何结果！"))
                                return
                            }

                            new BukkitRunnable() {
                                @Override
                                void run() {
                                    int size = found.size()
                                    sender.sendMessage(CoreAPI.format(prefix() + "&a本次查询总计 $size 个结果:"))
                                    if (sender instanceof Player) {
                                        for (AzureFlowItemFactory factory : found) {
                                            TextComponent component = new TextComponent()
                                            if (factory.alias.size() > 0)
                                                component.setText(CoreAPI.format(" -> &b" + factory.uuid + " &7(" + factory.alias.get(0) + ")"))
                                            else component.setText(CoreAPI.format(" -> &b" + factory.uuid))

                                            component.setHoverEvent(new HoverEvent(
                                                    HoverEvent.Action.SHOW_TEXT,
                                                    TextComponent.fromLegacyText(CoreAPI.format("&6点我生成获取命令"))
                                            ))

                                            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                                    "/af get " + factory.uuid))

                                            sender.spigot().sendMessage(component)
                                        }
                                    } else {
                                        for (AzureFlowItemFactory factory : found) {
                                            if (factory.alias.size() > 0)
                                                sender.sendMessage(CoreAPI.format(" -> &b" + factory.uuid + " &7(" + factory.alias.get(0) + ")"))
                                            else sender.sendMessage(CoreAPI.format(" -> &b" + factory.uuid))
                                        }
                                    }
                                }
                            }.runTask()
                        }
                    }.runTaskAsynchronously()
                    return
                }
                help("&b[name:模糊名字] [lore:模糊Lore] [type:模糊材质] &a检索AzureFlow物品")
                message("&e/" + label + " name:宝珠 name:极品 lore:材料 type:diamond")
            }
        }, "aff", "AzureFlowFind")
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("AzureFlowFinder")
    }
}
