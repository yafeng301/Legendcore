package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.KeyAPI
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import cn.mcdcs.legendcore.api.event.mod.KeyEvent
import cn.mcdcs.legendcore.api.expression.ExpressionManagerAPI
import cn.mcdcs.legendcore.api.modular.Modular
import cn.mcdcs.legendcore.api.modular.ModuleManagerAPI
import cn.mcdcs.legendcore.api.wrapper.WrapperManagerAPI
import org.bukkit.event.EventHandler

import java.util.concurrent.ConcurrentHashMap

class KeyModule implements Listener {

    static def listener = new KeyModule()
    static def proxy = new ConfigProxy("KeyModule")
    static Map<Integer, Data> modulars = new HashMap<>()
    static Map<String, Map<Integer, Long>> cd = new ConcurrentHashMap<>()
    static def runnable = new BukkitRunnable() {
        @Override
        void run() {
            def millis = System.currentTimeMillis()
            cd.values().removeIf {
                it.values().removeIf(value -> millis > value)
                return it.isEmpty()
            }
        }
    }

    static void onGroovyRegister() {
        listener.register()

        proxy.loadConfig((map) -> {
            map.put("按键执行模块", [["按键ID": 5, "前置条件": ["{hasAuth:vip.1}"], "执行模块": ["[console]open <p> xxx"], "冷却(毫秒)": 1000],
                                     ["按键ID": "T", "执行模块": ["在config.yml看具体模块"]]])
        })

        runnable.runTaskTimer(20, 100)
    }

    static void onGroovyUnregister() {
        listener.unregister()
        runnable.cancel()
    }

    @EventHandler
    void onReload(ConfigurationReloadEvent e) {
        modulars.clear()

        def wrapper = WrapperManagerAPI.instance.newMapWrapper(proxy.config)
        wrapper.getMapList("按键执行模块").forEach {
            Data data = new Data()
            data.modular = ModuleManagerAPI.instance.create(it.getStringList("执行模块"))
            data.cd = it.getLong("冷却(毫秒)")
            data.condition = it.getStringList("前置条件")
            def object = it.get("按键ID")
            if (object instanceof Number) {
                modulars.put(object.intValue(), data)
                KeyAPI.instance.registerKey(object.intValue())
            } else {
                def transformation = KeyAPI.instance.transformation(object.toString().toUpperCase())
                if (transformation != -1) {
                    modulars.put(transformation, data)
                    KeyAPI.instance.registerKey(transformation)
                }
            }
        }
    }

    @EventHandler
    void onKey(KeyEvent e) {
        if (e.type != KeyEvent.Type.PRESS) return

        def data = modulars.get(e.key)
        if (data == null) return

        def get = cd.computeIfAbsent(e.player.name, k -> new ConcurrentHashMap<>())
        def time = get.getOrDefault(e.key, 0)
        def current = System.currentTimeMillis()
        if (time > current) return

        get.put(e.key, current + data.cd)

        Map<String, String> map = new HashMap<>()

        for (String string : data.condition) {
            if (!ExpressionManagerAPI.instance.condition(ExpressionManagerAPI.instance.function(e.player, string, map))) return
        }

        data.modular.executeExpression(e.player, map)
    }

    static class Data {
        List<String> condition
        Modular modular
        long cd
    }
}
