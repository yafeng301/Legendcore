package groovy

import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import cn.mcdcs.legendcore.api.expression.ExpressionManagerAPI
import cn.mcdcs.legendcore.api.item.ItemManagerAPI
import cn.mcdcs.legendcore.api.modular.Modular
import cn.mcdcs.legendcore.api.modular.ModuleManagerAPI
import cn.mcdcs.legendcore.api.wrapper.WrapperManagerAPI
import cn.mcdcs.legendcore.api.wrapper.map.MapWrapper
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack

class CustomFish implements Listener {

    static def listener = new CustomFish()
    static def proxy = new ConfigProxy("CustomFish")
    static boolean cover
    static double change
    static List<String> condition
    static Modular modular
    static int weight
    static final List<Result> results = new ArrayList<>()

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig((map) -> {
            map.put("覆盖原版钓鱼.是否覆盖(true/false)", false)
            map.put("覆盖原版钓鱼.不覆盖额外掉落本插件自定义钓鱼物品的概率", 0.2)
            map.put("自定义条件", ["{papi:DreamPoints_value_钓鱼限制} < 20"])
            map.put("不满足条件提示", ["[message]&a钓鱼已经达到最大限制"])
            map.put("自定义内容", [
                    ["weight": 1, "command": ["eco give <p> 100"]],
                    ["weight": 1, "item": "mm-自定义鱼", "amount": 2],
                    ["weight": 1, "item": "DIAMOND", "exp": 10],
                    ["weight": 1, "item": "paper", "amount": 1, "exp": 5, "command": ["eco give <p> 100", "bc 大家注意这个人叫 <p> 他中了稀有奖励"]]
            ])
            map.put("提示", "weight 权重 item 物品可以是材质 exp 经验值 amount 数量可省略 command 是后台命令")
        })
    }

    static void onGroovyUnregister() {
        listener.unregister()
    }

    @EventHandler
    void onConfig(ConfigurationReloadEvent e) {
        def map = WrapperManagerAPI.instance.newMapWrapper(proxy.config)
        cover = map.getBoolean("覆盖原版钓鱼.是否覆盖(true/false)")
        change = map.getDouble("覆盖原版钓鱼.不覆盖额外掉落本插件自定义钓鱼物品的概率")
        condition = map.getStringList("自定义条件")
        modular = ModuleManagerAPI.instance.create(map.getStringList("不满足条件提示"))

        weight = 0
        results.clear()

        for (MapWrapper wrapper : map.getMapList("自定义内容")) {
            def result = new Result(wrapper)
            weight += result.weight
            results.add(result)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onFish(PlayerFishEvent e) {
        if (e.cancelled) return
        if (e.state != PlayerFishEvent.State.CAUGHT_FISH) return
        if (!cover && Math.random() > change) return

        Map<String, String> map = new HashMap<>()
        for (def string in condition) {
            if (!ExpressionManagerAPI.instance.condition(ExpressionManagerAPI.instance.function(e.player, string, map))) {
                modular.executeExpression(e.player, map)
                return
            }
        }

        def result = roll()

        e.expToDrop = 0
        if (result.exp != -1) e.expToDrop = result.exp

        if (result.item != null) {
            def stack = result.item.clone()

            def caught = e.caught
            if (caught instanceof Item) {
                caught.itemStack = stack
            } else {
                e.player.world.dropItemNaturally(e.player.location, stack)
            }
        } else {
            e.caught.remove()
        }

        if (result.command.isEmpty()) return

        for (final def command in result.command) Bukkit.dispatchCommand(Bukkit.consoleSender, command.replace("<p>", e.player.name))
    }

    static Result roll() {
        int weight = (int) (Math.random() * weight)
        int now = 0
        for (Result result : results) {
            now += result.weight
            if (weight < now) return result
        }
        return null
    }

    static class Result {
        final int weight
        ItemStack item = null
        int exp
        List<String> command

        Result(MapWrapper map) {
            weight = map.getInt("weight", 1)

            def string = map.getString("item", null)
            if (string != null) {
                item = ItemManagerAPI.instance.getHookItem(string)
                if (item == null) {
                    try {
                        item = new ItemStack(Material.valueOf(string.toUpperCase()))
                    } catch (IllegalArgumentException ignored) {

                    }
                }
                if (item != null) item.amount = map.getInt("amount", 1)
            }

            exp = map.getInt("exp", -1)
            command = map.getStringList("command")
        }
    }
}
