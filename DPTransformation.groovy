package groovy

import cn.mcdcs.dreampoints.data.User
import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class DPTransformation {

    static def host = "localhost"
    static def port = 8080
    static def user = "admin"
    static def password = "password"
    static def parameter = "verifyServerCertificate=false&useSSL=false"
    static def database = "数据库名"
    static def table = "表名"

    static void onGroovyRegister() {
        CommandManagerAPI.instance.register("DPTransformation", new CommandExecutorBox() {
            @Override
            void onCommand(CommandSender sender, String label, String[] args) {
                if (sender.op) {
                    new BukkitRunnable() {
                        @Override
                        void run() {
                            try {
                                sender.sendMessage("开始从数据库获取数据")
                                Map<String, Map<String, Double>> map = new HashMap<>()
                                Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port +
                                        "/" + database + "?" + parameter, user, password)

                                def statement = connection.createStatement()
                                def result = statement.executeQuery("SELECT * FROM " + table + ";")
                                while (result.next()) {
                                    map.computeIfAbsent(result.getString("uid"), k -> new HashMap<>())
                                    .put(result.getString("note"), result.getDouble("count"))
                                }

                                result.clone()
                                statement.clone()
                                connection.clone()

                                sender.sendMessage("数据获取完毕,开始对数据进行处理")

                                long count = 0
                                long total = 0
                                for (final def entry in map.entrySet()) {
                                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()))

                                    def instance = User.getInstance(player.name)
                                    entry.value.entrySet().forEach { instance.set(it.key, it.value) }

                                    total += entry.value.size()

                                    count++
                                }

                                sender.sendMessage("数据处理完毕！总计 " + total + "条数据,本次处理 " + count + " 个玩家的数据")
                            } catch (Throwable e) {
                                throw new RuntimeException(e)
                            }
                        }
                    }.runTaskAsynchronously()
                }
            }
        }, "dptn")
    }

    static void onGroovyUnregister() {
        CommandManagerAPI.instance.unregister("DPTransformation")
    }
}
