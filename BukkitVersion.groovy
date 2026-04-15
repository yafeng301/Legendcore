package groovy

import org.bukkit.Bukkit

class BukkitVersion {

    static void onGroovyRegister() {
        println Bukkit.bukkitVersion
        println Bukkit.version
    }
}
