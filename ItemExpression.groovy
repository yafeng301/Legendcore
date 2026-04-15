package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.expression.ExpressionManagerAPI
import cn.mcdcs.legendcore.api.expression.ExpressionModule
import cn.mcdcs.legendcore.api.item.ItemManagerAPI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// {hasItem:mm-xxx} 判断玩家是否拥有 mm 物品库里面 xxx 物品 1个
// {takeItem:mm-xxx:5} 扣除玩家背包 mm 物品库里面 xxx 物品 5个

class ItemExpression implements ExpressionModule {

    static def module = new ItemExpression()

    static void onGroovyRegister() {
        ExpressionManagerAPI.register(CoreAPI.instance, "Item", module)
    }

    static void onGroovyUnregister() {
        ExpressionManagerAPI.unregister("Item")
    }

    private final List<String> list = Arrays.asList("hasItem", "takeItem")

    @Override
    List<String> alias() {
        return list
    }

    @Override
    String run(Player player, String text, String[] args, Map<String, String> termMap, Map<String, Object> customData) {
        if (player == null) return null

        ItemStack material = ItemManagerAPI.instance.getHookItem(args[1])

        if (args.length > 2) {
            try {
                material.setAmount(Integer.parseInt(args[2]))
            } catch (NumberFormatException ignored) {

            }
        }

        switch (args[0].toLowerCase()) {
            case "hasitem":
                for (final def item in player.inventory.contents) {
                    if (item == null || item.type == Material.AIR) continue

                    if (material.isSimilar(item)) {
                        material.amount -= item.amount

                        if (material.amount <= 0) break
                    }
                }

                return material.amount <= 0
            case "takeitem":
                for (int i = 0; i < player.inventory.size; i++) {
                    ItemStack item = player.inventory.getItem(i)
                    if (item == null || item.type == Material.AIR) continue

                    if (material.isSimilar(item)) {
                        if (material.amount >= item.amount) {
                            material.amount -= item.amount
                            item.amount = 0
                            player.inventory.setItem(i, null)
                        } else {
                            item.amount -= material.amount
                            material.amount = 0
                        }

                        if (material.amount <= 0) break
                    }
                }
                return null
        }
        return null
    }
}
