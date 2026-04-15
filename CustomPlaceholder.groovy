package groovy

import cn.mcdcs.legendcore.api.placeholderapi.PlaceholderProcessor
import org.bukkit.OfflinePlayer

// %数值转义_1% 即可对 1 进行转义
// isOnline() 方法可以判断对应玩家是否在线
// asOnline() 可以将离线玩家类转换成 在线玩家

class CustomPlaceholder extends PlaceholderProcessor {
    @Override
    String execute(OfflinePlayer player, String originParams, String[] args) { // 对变量进行处理
        // 这里的 args 来源是通过对 originParams 进行分割得到的 分割符号是 _
        // 也就是说 如果变量是 %数值转义_1_2% 那么 originParams 是 1_2 args的长度是2 内部数据分别是 1 和 2
        return Integer.parseInt(args[0]) >> 2 as String
    }

    @Override
    String getIdentifier() { // 变量前缀
        return "数值转义"
    }

    static PlaceholderProcessor processor

    static void onGroovyRegister() {
        (processor = new CustomPlaceholder()).onRegister()
    }

    static void onGroovyUnregister() {
        processor.onUnregister()
    }
}
