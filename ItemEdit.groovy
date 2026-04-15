package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.nms.NMSProvider
import cn.mcdcs.legendcore.api.nms.tag.TagManager
import cn.mcdcs.legendcore.api.reflect.Reflects
import cn.mcdcs.legendcore.api.reflect.accessors.DefaultMethodAccessor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

import java.util.stream.Collectors
import java.util.stream.Stream

class ItemEdit {

    static DefaultMethodAccessor setUnbreakable = null

    static void onGroovyRegister() {
        if (!NMSProvider.available)
            setUnbreakable = Reflects.getDefaultMethod(ItemMeta.class, "setUnbreakable", boolean.class)

        CommandManagerAPI.instance.register("ItemEdit", new CommandExecutorBox() {
            @Override
            String prefix() {
                return "&3ItemEdit &f>>> "
            }

            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (!sender.op) return

                if (sender instanceof ConsoleCommandSender) {
                    message("&c只有玩家才可以使用该命令！")
                    return
                }

                Player player = sender as Player
                ItemStack itemStack = player.itemInHand

                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("lore")) {
                        ItemMeta meta = itemStack.itemMeta
                        if (meta == null) {
                            message("&c请手持物品输入该命令！")
                            return
                        }

                        List<String> lore
                        if (meta.hasLore()) lore = meta.lore
                        else lore = new ArrayList<>()

                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("clear")) {
                                lore.clear()
                                meta.setLore(lore)
                                itemStack.setItemMeta(meta)

                                message("&a成功清空手上物品的Lore")
                                return
                            } else if (args.length > 2) {
                                if (args[1].equalsIgnoreCase("add")) {
                                    String text = CoreAPI.format(Arrays.asList(args).subList(2, args.length).join(" "))

                                    lore.add(text)
                                    meta.setLore(lore)
                                    itemStack.setItemMeta(meta)

                                    message("&a成功添加手上物品一行Lore:&b " + text)
                                    return
                                } else if (args[1].equalsIgnoreCase("remove")) {
                                    String text = CoreAPI.format(Arrays.asList(args).subList(2, args.length).join(" "))

                                    if (lore.remove(text)) {
                                        meta.setLore(lore)
                                        itemStack.setItemMeta(meta)

                                        message("&a成功删除手上物品一行Lore:&b " + text)
                                        return
                                    }

                                    message("&c手上物品Lore并不存在:&b " + text)
                                    return
                                } else if (args[1].equalsIgnoreCase("first")) {
                                    String text = CoreAPI.format(Arrays.asList(args).subList(2, args.length).join(" "))

                                    lore.add(0, text)
                                    meta.setLore(lore)
                                    itemStack.setItemMeta(meta)

                                    message("&a成功添加手上物品一行Lore:&b " + text)
                                    return
                                } else if (args.length > 3) {
                                    if (args[1].equalsIgnoreCase("insert")) {
                                        int index
                                        try {
                                            index = Integer.parseInt(args[2])
                                        } catch (NumberFormatException ignored) {
                                            message("&c请输入一个正确的整数！")
                                            return
                                        }

                                        if (index >= lore.size()) {
                                            message("&c插入的行数已经超过Lore长度！")
                                            return
                                        }

                                        String text = CoreAPI.format(Arrays.asList(args).subList(3, args.length).join(" "))

                                        lore.add(index, text)
                                        meta.setLore(lore)
                                        itemStack.setItemMeta(meta)

                                        message("&a成功在&b " + index + " &a行插入一条数据:&b " + text)
                                        return
                                    } else if (args[1].equalsIgnoreCase("set")) {
                                        int index
                                        try {
                                            index = Integer.parseInt(args[2])
                                        } catch (NumberFormatException ignored) {
                                            message("&c请输入一个正确的整数！")
                                            return
                                        }

                                        if (index >= lore.size()) {
                                            message("&c设置的行数已经超过Lore长度！")
                                            return
                                        }

                                        String text = CoreAPI.format(Arrays.asList(args).subList(3, args.length).join(" "))

                                        lore.set(index, text)
                                        meta.setLore(lore)
                                        itemStack.setItemMeta(meta)

                                        message("&a成功设置第&b " + index + " &a行的数据为:&b " + text)
                                        return
                                    }
                                }
                            }
                        }
                        help("",
                                "lore add &b<内容> &a添加一行Lore",
                                "lore remove &b<内容> &a删除一行Lore",
                                "lore first &b<内容> &a添加首行Lore",
                                "lore set &b<位置> <内容> &a设置一行Lore",
                                "lore insert &b<位置> <内容> &a插入一行Lore",
                                "lore clear &a清空物品的Lore"
                        )
                        return
                    } else if (args[0].equalsIgnoreCase("enchant")) {
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("clear")) {
                                itemStack.removeEnchantments()

                                message("&c成功删除手上物品所有附魔！")
                                return
                            } else if (args.length > 2) {
                                if (args[1].equalsIgnoreCase("remove")) {
                                    Enchantment enchantment = Enchantment.getByName(args[2].toUpperCase())

                                    if (enchantment == null) {
                                        message("&c对应附魔并不存在！")
                                        return
                                    }

                                    itemStack.removeEnchantment(enchantment)
                                    message("&a成功删除手上物品的附魔:&b " + enchantment.name)
                                    return
                                } else if (args.length > 3) {
                                    if (args[1].equalsIgnoreCase("add")) {
                                        Enchantment enchantment = Enchantment.getByName(args[2].toUpperCase())

                                        if (enchantment == null) {
                                            message("&c对应附魔并不存在！")
                                            return
                                        }

                                        int lv
                                        try {
                                            lv = Integer.parseInt(args[3])
                                        } catch (NumberFormatException ignored) {
                                            message("&c请输入一个正确的整数！")
                                            return
                                        }

                                        itemStack.addUnsafeEnchantment(enchantment, lv)
                                        message("&a成功为手上物品添加附魔:&b " + enchantment.name + " - " + lv)
                                        return
                                    }
                                }
                            }
                        }
                        help("",
                                "enchant add &b<附魔> <等级> &a添加一个附魔",
                                "enchant remove &b<附魔> &a删除一个附魔",
                                "enchant clear &a清空所有附魔"
                        )
                        return
                    } else if (args[0].equalsIgnoreCase("flag")) {
                        ItemMeta meta = itemStack.itemMeta
                        if (meta == null) {
                            message("&c请手持物品输入该命令！")
                            return
                        }

                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("clear")) {
                                for (ItemFlag flag : ItemFlag.values()) meta.removeItemFlags(flag)
                                itemStack.setItemMeta(meta)

                                message("&c成功删除手上物品所有标志！")
                                return
                            } else if (args.length > 2) {
                                if (args[1].equalsIgnoreCase("remove")) {
                                    ItemFlag flag
                                    try {
                                        flag = ItemFlag.valueOf(args[2].toUpperCase())
                                    } catch (IllegalArgumentException ignored) {
                                        message("&c对应标志并不存在！")
                                        return
                                    }

                                    meta.removeItemFlags(flag)
                                    itemStack.setItemMeta(meta)
                                    message("&a成功删除手上物品的标志:&b " + flag.name())
                                    return
                                } else if (args[1].equalsIgnoreCase("add")) {
                                    ItemFlag flag
                                    try {
                                        flag = ItemFlag.valueOf(args[2].toUpperCase())
                                    } catch (IllegalArgumentException ignored) {
                                        message("&c对应标志并不存在！")
                                        return
                                    }

                                    meta.addItemFlags(flag)
                                    itemStack.setItemMeta(meta)
                                    message("&a成功为手上物品添加标志:&b " + flag.name())
                                    return
                                }
                            }
                        }
                        help("",
                                "flag add &b<标志> &a添加一个表示",
                                "flag remove &b<标志> &a删除一个标志",
                                "flag clear &a清空所有标志"
                        )
                        return
                    } else if (args.length > 1) {
                        if (args[0].equalsIgnoreCase("type")) {
                            Material material = Material.getMaterial(args[1].toUpperCase())

                            if (material == null) {
                                message("&c对应材质并不存在！")
                                return
                            }

                            itemStack.setType(material)
                            player.itemInHand = itemStack
                            message("&a成功设置手上物品材质为:&b " + material.name())
                            return
                        } else if (args[0].equalsIgnoreCase("amount")) {
                            int amount
                            try {
                                amount = Integer.parseInt(args[1])
                            } catch (NumberFormatException ignored) {
                                message("&c请输入一个正确的整数！")
                                return
                            }

                            itemStack.setAmount(amount)
                            message("&a成功设置手上物品数量为:&b " + amount)
                            return
                        } else if (args[0].equalsIgnoreCase("durability")) {
                            short durability
                            try {
                                durability = Short.parseShort(args[1])
                            } catch (NumberFormatException ignored) {
                                message("&c请输入一个正确的整数！")
                                return
                            }

                            itemStack.setDurability(durability)
                            message("&a成功设置手上物品耐久为:&b " + durability)
                            return
                        } else if (args[0].equalsIgnoreCase("name")) {
                            String name = CoreAPI.format(Arrays.asList(args).subList(1, args.length).join(" "))

                            ItemMeta meta = itemStack.itemMeta

                            if (meta == null) {
                                message("&c请手持物品输入该命令！")
                                return
                            }

                            meta.setDisplayName(name)
                            itemStack.setItemMeta(meta)

                            message("&a成功设置手上物品名字为:&b " + name)
                            return
                        } else if (args[0].equalsIgnoreCase("unbreakable")) {
                            boolean b = Boolean.parseBoolean(args[1])

                            ItemMeta meta = itemStack.itemMeta

                            if (meta == null) {
                                message("&c请手持物品输入该命令！")
                                return
                            }

                            if (setUnbreakable == null) {
                                itemStack.itemMeta = meta
                                def tag = TagManager.getItemTag(itemStack)
                                tag.set("Unbreakable", b)
                                TagManager.setItemTag(itemStack, tag)
                            } else {
                                setUnbreakable.invoke(meta, b)
                                itemStack.itemMeta = meta
                            }
                            message("&a成功设置手上物品&b " + (b ? "无法被破坏" : "可被破坏"))
                            return
                        }
                    }
                }

                help("",
                        "type &b<材质> &a设置手上物品材质",
                        "amount &b<数量> &a设置手上物品数量",
                        "durability &b<数值> &a设置手上物品耐久值",
                        "name &b<名字> &a设置手上物品的名字",
                        "lore &a设置手上物品Lore信息",
                        "enchant &a设置手上物品附魔信息",
                        "flag &a设置手上的物品标志",
                        "unbreakable &b<true/false> &a设置手上物品无法破坏"
                )
            }

            @Override
            List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
                if (sender.op) {
                    if (args.length == 1) return Arrays.asList("type", "amount", "durability", "name", "lore", "enchant", "flag", "unbreakable")

                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("lore")) return Arrays.asList("add", "remove", "first", "set", "insert", "cleart")
                        else if (args[0].equalsIgnoreCase("enchant") || args[0].equalsIgnoreCase("flag"))
                            return Arrays.asList("add", "remove", "clear")
                    }

                    if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("enchant") && !args[1].equalsIgnoreCase("clear"))
                            return Stream.of(Enchantment.values()).map(enchant -> enchant.name).collect(Collectors.toList())
                        else if (args[0].equalsIgnoreCase("flag") && !args[1].equalsIgnoreCase("clear"))
                            return Stream.of(ItemFlag.values()).map(flag -> flag.name()).collect(Collectors.toList())
                    }
                }

                return null
            }
        }, "Lore", "LoreEdit", "ie", "iet")
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("ItemEdit")
    }
}
