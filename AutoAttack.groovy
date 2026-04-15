package groovy

import cn.mcdcs.legendcore.api.bukkit.BukkitRunnable
import cn.mcdcs.legendcore.api.bukkit.Listener
import cn.mcdcs.legendcore.api.command.CommandExecutor
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.configuration.ConfigCallback
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.event.configuration.ConfigurationReloadEvent
import cn.mcdcs.legendcore.api.legendpets.CorePetAPI
import cn.mcdcs.legendcore.api.reflect.Reflects
import cn.mcdcs.legendcore.api.reflect.accessors.ConstructorAccessor
import cn.mcdcs.legendcore.api.reflect.accessors.DefaultFieldAccessor
import cn.mcdcs.legendcore.api.reflect.accessors.DefaultMethodAccessor
import cn.mcdcs.legendcore.api.wrapper.WrapperManagerAPI
import cn.mcdcs.legendcore.api.wrapper.map.MapWrapper
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Tameable
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.util.Vector

class AutoAttack extends BukkitRunnable {

    static def task
    static def proxy = new ConfigProxy("AutoAttack")
    static long tick = 0
    static def list = new ArrayList()
    static def normal = new Data()
    static List<Data> datas = new ArrayList<>()
    static def listener = new Listener() {
        @EventHandler
        void onReload(ConfigurationReloadEvent e) {
            datas.clear()

            normal.permission = null
            normal.priority = -9999
            normal.damage = proxy.config.getDouble("攻击伤害")
            normal.range = proxy.config.getDouble("范围半径")
            normal.knockback = proxy.config.getBoolean("防击退")
            normal.cd = proxy.config.getInt("攻击冷却(-1不生效)", -1)
            normal.amount = proxy.config.getInt("攻击数量(-1全攻击)", 1)

            def wrapper = WrapperManagerAPI.instance.newMapWrapper(proxy.config)

            for (MapWrapper map : wrapper.getMapList("权限效果")) {
                Data data = new Data()
                data.permission = map.getString("权限节点")
                data.priority = map.getDouble("优先级(越高越优先)")
                data.damage = map.getDouble("攻击伤害")
                data.range = map.getDouble("范围半径")
                data.knockback = map.getBoolean("防击退")
                data.cd = map.getInt("攻击冷却(-1不生效)", -1)
                data.amount = map.getInt("攻击数量(-1全攻击)", 1)
                datas.add(data)
            }
        }
    }

    static DefaultMethodAccessor getAttribute = null
    static Object attribute = null
    static DefaultMethodAccessor getValue = null

    static void onGroovyRegister() {
        try {
            def attributableClazz = Class.forName("org.bukkit.attribute.Attributable")
            def attributeClazz = Class.forName("org.bukkit.attribute.Attribute")
            attribute
            try {
                attribute = Reflects.getStaticField(attributeClazz, "GENERIC_ATTACK_DAMAGE").get()
            } catch (Throwable ignored) {
                attribute = Reflects.getStaticField(attributeClazz, "ATTACK_DAMAGE").get()
            }
            getAttribute = Reflects.getDefaultMethod(attributableClazz, "getAttribute", attribute.class)
            getValue = Reflects.getDefaultMethod(getAttribute.method.returnType, "getValue")
        } catch (Throwable ignored) {

        }

        listener.register()
        task = new AutoAttack().runTaskTimer(0, 1)
        proxy.loadConfig(new ConfigCallback<Map<String, Object>>() {
            @Override
            void call(Map<String, Object> map) {
                map.put("范围半径", 4D)
                map.put("攻击伤害", 1D)
                map.put("攻击间隔(20=1秒)", 1)
                map.put("攻击冷却(-1不生效)", 5)
                map.put("攻击数量(-1全攻击)", 1)
                map.put("防击退", false)
                map.put("是否过事件(增加兼容性)", false)
                map.put("是否过原版属性", false)
                map.put("开启提示", "§6LegendCore §f>>> §a已开启自动攻击")
                map.put("关闭提示", "§6LegendCore §f>>> §c已关闭自动攻击")
                map.put("是否挥动主手", false)
                map.put("权限效果", [
                        [
                                "优先级(越高越优先)": 0,
                                "权限节点": "vip.1",
                                "范围半径": 5D,
                                "攻击伤害": 2D,
                                "防击退": false,
                                "攻击冷却(-1不生效)": 4,
                                "攻击数量(-1全攻击)": 1
                        ],
                        [
                                "优先级(越高越优先)": 1,
                                "权限节点": "vip.2",
                                "范围半径": 6D,
                                "攻击伤害": 3D,
                                "防击退": true
                        ]
                ])
            }
        })
        CommandManagerAPI.instance.register("AutoAttack", new CommandExecutor() {
            @Override
            void onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender instanceof Player) {
                    if (list.remove(sender.name)) {
                        sender.sendMessage(proxy.config.getString("关闭提示").replace("&","§"))
                    } else {
                        list.add(sender.name)
                        sender.sendMessage(proxy.config.getString("开启提示").replace("&","§"))
                    }
                }
            }
        }, "aat")
    }

    static void onGroovyUnregister() {
        task.cancel()
        CommandManagerAPI.instance.unregister("AutoAttack")
        listener.unregister()
    }

    @Override
    void run() {
        tick = tick + 1
        if (tick >= proxy.config.getLong("攻击间隔(20=1秒)")) {
            for (Player player : Bukkit.onlinePlayers) {
                if (!list.contains(player.getName())) continue

                if (player.dead) continue
                if (!player.valid) continue
                if (player.gameMode.name() == "SPECTATOR") continue

                Data now = normal
                for (Data data : datas) {
                    if (data.priority > now.priority) {
                        if (player.hasPermission(data.permission)) {
                            now = data
                        }
                    }
                }

                LivingEntity target = null
                double range = now.range
                int amount = now.amount
                for (Entity entity : player.getNearbyEntities(range, range, range)) {
                    if (!(entity instanceof LivingEntity)) continue
                    if (entity instanceof Player) continue
                    if (CorePetAPI.isPetEntity(entity)) continue
                    if (entity.type.name() == "ARMOR_STAND") continue
                    if (entity.dead) continue
                    if (player.world != entity.world) continue
                    if (entity instanceof Tameable && entity.isTamed()) continue

                    target = (LivingEntity) entity

                    double damage = now.damage

                    if (proxy.config.getBoolean("是否过原版属性") && getAttribute != null) {
                        damage += getValue.invoke(double.class, getAttribute.invoke(player, attribute))
                    }

                    if (proxy.config.getBoolean("是否过事件(增加兼容性)")) {
                        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(player, target, EntityDamageByEntityEvent.DamageCause.ENTITY_ATTACK, damage)
                        Bukkit.pluginManager.callEvent(event)
                        if (event.isCancelled()) continue
                        damage = event.getDamage()
                    }

                    target.damage(damage, player)

                    if (now.knockback) target.velocity = new Vector(0,0,0)

                    if (now.amount != -1) amount--

                    if (now.amount != -1 && amount <= 0) break
                }

                if (target == null) continue

                if (proxy.config.getBoolean("是否挥动主手")) {
                    if (!isInitPacket || packet != null) {
                        swingMainHand(player, player)
                        for (Player entity : player.world.players) {
                            if (entity == player) continue
                            if (entity.location.distance(player.location) < 32) {
                                swingMainHand(player, entity)
                            }
                        }
                    }
                }

                setCooldown(player, now.cd)
            }
            tick = 0
        }
    }

    static boolean isInitPacket = false
    static ConstructorAccessor packet = null
    static DefaultMethodAccessor getHandle = null
    static DefaultFieldAccessor getPlayerConnection = null
    static DefaultMethodAccessor sendPacket = null

    static void swingMainHand(Player player, Player target) {
        if (isInitPacket) {
            if (packet == null) return
        } else {
            isInitPacket = true
            try {
                getHandle = Reflects.getDefaultMethod(player.class, "getHandle")
                def type = getHandle.method.returnType
                def nms = type.getPackage().getName()
                try {
                    Class<?> clazz = Class.forName(nms + ".PacketPlayOutAnimation")
                    packet = Reflects.getConstructor(clazz, Class.forName(nms + ".Entity"), int.class)
                    getPlayerConnection = Reflects.getDefaultField(type, "playerConnection")
                    sendPacket = Reflects.getDefaultMethod(getPlayerConnection.field.type, "sendPacket", Class.forName(nms + ".Packet"))
                } catch (ClassNotFoundException ignored) {

                }
            } catch (Throwable ignored) {

            }
        }

        sendPacket.invoke(getPlayerConnection.get(getHandle.invoke(target)), packet.newInstance(getHandle.invoke(player), 0))
    }

    static boolean isInitCooldown = false
    static DefaultMethodAccessor setCooldown = null

    static void setCooldown(Player player, int cd) {
        if (cd <= 0) return

        if (isInitCooldown) {
            if (setCooldown == null) return
        } else {
            isInitCooldown = true
            try {
                setCooldown = Reflects.getDefaultMethod(HumanEntity.class, "setCooldown", Material.class, int.class)
            } catch (NoSuchMethodException ignored) {

            }
        }

        setCooldown.invoke(player, player.itemInHand.type, cd)
    }

    static class Data {
        String permission
        double priority
        double range
        double damage
        int cd
        boolean knockback
        int amount
    }
}
