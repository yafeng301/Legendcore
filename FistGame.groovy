package groovy

import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.currency.DreamPointsAPI
import cn.mcdcs.legendcore.api.currency.PlayerPointsAPI
import cn.mcdcs.legendcore.api.currency.VaultAPI
import cn.mcdcs.legendcore.api.dragoncore.PacketProcessor
import org.bukkit.Bukkit

// 方法.发包("LegendCoreGroovy", "猜拳", 1) 返回结果变量为 猜拳结果

//直接
//方法.发包("LegendCoreGroovy", "猜拳", 1); //这里的 1 位置 可以弄 1 2 3 自己决定是 石头 剪刀 步
// //另外 失败了，自动返回 你输入值以外的 两个值随机一个 例如你发送 1 随机 返回 2 3 发送 2 随机返回 1 3
//方法.延时(100);
//局部变量.结果 = 方法.取变量("猜拳结果");
//
// //如果结果 == -1 则是 货币不足
// //如果结果 == 你发送的数字 则是成功 在服务端配置指令 自动发货
// //如果结果 != 你发送的数字 则是失败

class FistGame extends PacketProcessor {

    static def processor = new FistGame()
    static def proxy = new ConfigProxy("FistGame")

    static void onGroovyRegister() {
        processor.register()
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("猜拳费用", 500)
                map.put("猜拳类型(vault,points,dreampoints)", "points")
                map.put("猜拳概率", 0.1D)
                map.put("猜拳奖励", ["points give <p> 1000"])
                map.put("DreamPoints类型", "xxx")
            }
        })
    }

    static void onGroovyUnregister() {
        processor.unregister()
    }

    @Override
    void execute(String[] args, boolean isALL) {
        double d = proxy.config.getDouble("猜拳费用")
        switch (proxy.config.getString("猜拳类型(vault,points,dreampoints)").toLowerCase()) {
            case "vault":
                if (!VaultAPI.instance.has(player, d)) {
                    sendPacket("猜拳结果", "-1")
                    return
                }
                VaultAPI.instance.take(player, d)
                break
            case "points":
                if (!PlayerPointsAPI.instance.has(player, d as int)) {
                    sendPacket("猜拳结果", "-1")
                    return
                }
                PlayerPointsAPI.instance.take(player, d as int)
                break
            case "dreampoints":
                if (DreamPointsAPI.instance.getDouble(player, proxy.config.getString("DreamPoints类型")) < d) {
                    sendPacket("猜拳结果", "-1")
                    return
                }
                DreamPointsAPI.instance.take(player, proxy.config.getString("DreamPoints类型"), d)
                break
            default :
                sendPacket("猜拳结果", "-1")
                return
        }

        int i = Double.valueOf(args[0]).intValue()
        if (Math.random() < proxy.config.getDouble("猜拳概率")) {
            sendPacket("猜拳结果", i + "")
            proxy.config.getStringList("猜拳奖励").forEach {
                Bukkit.dispatchCommand(Bukkit.consoleSender, it.replace("<p>", player.name))
            }
        } else sendPacket("猜拳结果", randomFail(i) + "")
    }

    int randomFail(int i) {
        int result = (int) (Math.random() * 3) + 1
        if (i == result) return randomFail(i)
        return i
    }

    @Override
    String getCustomIdentifier() {
        return "猜拳"
    }
}
