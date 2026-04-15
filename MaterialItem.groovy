package groovy

import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.item.ItemManagerAPI
import cn.mcdcs.legendcore.api.item.module.ItemModule
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// 让你的 传奇系列 插件兼容原版材质的物品吧
// 通过 m-材质名 或 material-材质名 即可让传奇系列插件兼容原版物品
// legendcore-材质名 也可以 但是没必要 hhh

class MaterialItem implements ItemModule {

    static def module = new MaterialItem()

    static void onGroovyRegister() {
        ItemManagerAPI.instance.register(CoreAPI.instance, module, "m", "material")
    }

    static void onGroovyUnregister() {
        ItemManagerAPI.instance.unregister(CoreAPI.instance)
    }

    @Override
    ItemStack build(Player player, String id) {
        try {
            return new ItemStack(Material.valueOf(id.toUpperCase()))
        } catch (IllegalArgumentException ignored) {

        }
        return null
    }
}
