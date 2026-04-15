package groovy

import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderProcessor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

import static groovy.AutoAttack.*

// AutoAttackPlaceholder - 为 AutoAttack 扩展Papi变量
// %AutoAttack_state% 获取玩家自动攻击开关状态
// %AutoAttack_range% 获取玩家自动攻击范围
// %AutoAttack_damage% 获取玩家自动攻击伤害
// %AutoAttack_cooldown% 获取玩家自动攻击后的攻击冷却时间
// %AutoAttack_amount% 获取玩家自动攻击数量


class AutoAttackPlaceholder extends PlaceholderProcessor {

    static def instance = new AutoAttackPlaceholder()

    static void onGroovyRegister() {
        instance.register()
    }

    static void onGroovyUnregister() {
        instance.unregister()
    }

    @Override
    String execute(OfflinePlayer player, String originParams, String[] args) {
        if (isOnline()) {
            if (originParams.equalsIgnoreCase("state")) {
                return list.contains(player.name) ? "已开启" : "已关闭"
            } else if (originParams.equalsIgnoreCase("range")) {
                return getData(asOnline()).range + ""
            } else if (originParams.equalsIgnoreCase("damage")) {
                return getData(asOnline()).damage + ""
            } else if (originParams.equalsIgnoreCase("cooldown")) {
                return getData(asOnline()).cd + ""
            } else if (originParams.equalsIgnoreCase("amount")) {
                return getData(asOnline()).amount + ""
            }
        }
        return null
    }

    @Override
    String getIdentifier() {
        return "AutoAttack"
    }

    static Data getData(Player player) {
        Data now = normal
        for (Data data : datas) {
            if (data.priority > now.priority) {
                if (player.hasPermission(data.permission)) {
                    now = data
                }
            }
        }
        return now
    }
}
