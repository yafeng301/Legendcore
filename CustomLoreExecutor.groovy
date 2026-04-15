package groovy

import cn.mcdcs.legendstrengthen.api.LoreExecutor

class CustomLoreExecutor extends LoreExecutor {

    static def o = new CustomLoreExecutor()

    static void onGroovyRegister() {
        LoreExecutor.register("custom", o)
    }

    static void onGroovyUnregister() {
        LoreExecutor.unregister("custom")
    }

    @Override
    List<String> run(String s, List<String> list, List<String> list1) {

        list.clear()

        return list
    }
}
