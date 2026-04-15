package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.reflect.Reflects
import io.rokuko.azureflow.AzureFlow
import io.rokuko.azureflow.features.item.factory.AzureFlowItemFactory
import io.rokuko.azureflow.features.item.factory.AzureFlowItemFactoryService
import io.rokuko.azureflow.internal.service.Service
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class AzureFlowList implements Listener {

    static def listener = new AzureFlowList()
    static List<ItemStack> cache = new ArrayList<>()
    static int max
    static Map<String, AzureFlowItemFactory> map = AzureFlowItemFactoryService.INSTANCE.container
    static def service = new CustomService()
    static def services = Reflects.getStaticField(AzureFlow.class, "services").get(LinkedHashSet.class)

    static void onGroovyRegister() {
        listener.register()

        CommandManagerAPI.instance.register("AzureFlowList", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (!sender.op) {
                    message("&c你没有权限使用该命令！")
                    return
                }

                if (sender instanceof Player) {
                    if (max == 0) {
                        message("&cAzureFlow内并未发现任何物品实例！")
                        return
                    }

                    sender.openInventory(render(1))
                    return
                }

                message("&c后台无法使用该命令")
            }
        }, "afl")

        services.add(service)
        service.enable()
    }

    static void onGroovyUnregister() {
        listener.unregister()

        CommandManagerAPI.instance.unregister("AzureFlowList")

        for (Player player : Bukkit.onlinePlayers)
            if (player.openInventory.topInventory.holder instanceof CustomHolder)
                player.closeInventory()

        services.remove(service)
        service.disable()
    }

    @EventHandler
    void onClick(InventoryClickEvent e) {
        if (e.slot == -999) return


        def holder = e.view.topInventory.holder
        if (holder instanceof CustomHolder) {
            e.cancelled = true

            if (e.slot == 45) {
                if (holder.page == 1) return

                e.whoClicked.openInventory(render(holder.page - 1))
            } else if (e.slot == 53) {
                if (holder.page == max) return

                e.whoClicked.openInventory(render(holder.page + 1))
            }
        }
    }

    static Inventory render(int page) {
        Inventory inventory = Bukkit.createInventory(new CustomHolder(page), 54, CoreAPI.format("&a&lAzureFlow &f- &b&l物品大全"))

        def item = new ItemStack(Material.PAPER)
        def meta = item.itemMeta
        meta.lore = CoreAPI.format(["&a当前页:&b " + page, "&a最大页:&b " + max])
        if (page > 1) {
            meta.displayName = CoreAPI.format("&3下一页")
            item.itemMeta = meta
            inventory.setItem(45, item.clone())
        }

        if (page != max) {
            meta.displayName = CoreAPI.format("&3上一页")
            item.itemMeta = meta
            inventory.setItem(53, item.clone())
        }

        int base = (page - 1) * 45
        for (int i = 0; i < 45; i++) {
            int location = base + i
            if (location >= cache.size()) break

            inventory.setItem(i, cache.get(location))
        }

        return inventory
    }

    static class CustomHolder implements InventoryHolder {
        int page

        CustomHolder(int page) {
            this.page = page
        }

        @Override
        Inventory getInventory() {
            return null
        }
    }

    static class CustomService implements Service {

        @Override
        void enable() {
            for (final def item in map.values()) {
                def stack = item.build().staticItemStack()
                def meta = stack.itemMeta
                if (item.displayName != null) meta.displayName = CoreAPI.format(item.displayName)
                if (item.lore != null) meta.lore = CoreAPI.format(item.lore)
                stack.itemMeta = meta
                cache.add(stack)
            }

            max = (int) (cache.size() / 45 + 1)
            if (cache.size() % 45 == 0) max--
        }

        @Override
        void disable() {
            cache.clear()
        }
    }
}
