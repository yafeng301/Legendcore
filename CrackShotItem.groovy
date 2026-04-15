package groovy

import cn.mcdcs.legendcore.api.item.ItemManagerAPI
import cn.mcdcs.legendcore.api.item.module.ItemModule
import com.shampaggon.crackshot.CSDirector
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CrackShotItem implements ItemModule {

    static CSDirector director = CSDirector.getPlugin(CSDirector.class)

    static void onGroovyRegister() {

        ItemManagerAPI.instance.register(director, new CrackShotItem(), "cs")
    }

    static void onGroovyUnregister() {
        ItemManagerAPI.instance.unregister(director)
    }

    @Override
    ItemStack build(Player player, String id) {
        return director.csminion.vendingMachine(id)
    }
}
