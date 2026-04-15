package groovy

import cn.mcdcs.legendcore.api.dragoncore.PacketProcessor

// 方法.发包("LegendCoreGroovy", "数值转义", 2) 只有本脚本处理
// 方法.发包("LegendCoreGroovy:ALL", 2) 所有脚本都会处理 (不推荐)
// 发包完毕,通过 方法.取变量("转义结果") 获取最终结果

class CustomDragonCorePacket extends PacketProcessor {
    @Override
    void execute(String[] args, boolean isALL) {
        sendPacket("转义结果", Integer.parseInt(args[0]) >> 2 as String)
        // 当获取是个物品 可以通过 eos.moe.dragoncore.util.ItemUtil.jsonToItem(args[1]) 转换成物品堆
    }

    @Override
    String getCustomIdentifier() {
        return "数值转义"
    }

    static PacketProcessor processor

    static void onGroovyRegister() {
        processor = new CustomDragonCorePacket().register()
    }

    static void onGroovyUnregister() {
        processor.unregister()
    }
}
