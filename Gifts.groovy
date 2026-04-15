package groovy

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import cn.mcdcs.legendcore.api.CoreAPI
import cn.mcdcs.legendcore.api.command.CommandExecutorBox
import cn.mcdcs.legendcore.api.command.CommandManagerAPI
import cn.mcdcs.legendcore.api.configuration.ConfigProxy
import cn.mcdcs.legendcore.api.configuration.ConfigCallback

import java.util.ArrayList
import java.util.List
import java.util.stream.Collectors


class Gifts{

	/*
	
		QQ 2198577289 Banhquy
		
		Gifts - 一个礼物
		
		/gifts all 给予在线的所有玩家你当前手上的物品
		/gifts give <Player> 给予目标玩家自己手上的物品
		/gifts random 随机给在线的玩家一个物品
		/gifts inventory 给予在线的所有玩家自己背包的所有物品(装备也算)
		
		当玩家背包满了之后,物品会自动掉在地下
		
	
	
	*/

	static def proxy = new ConfigProxy("Gifts")

	static void onGroovyRegister(){
	
		proxy.loadConfig(new ConfigCallback<Map<String, Object>>(){
		
			@Override
			void call(Map<String,Object> map){
				
				map.put("playergifts","&eGifts &f>>> &a管理员 &4%player% &a给予了玩家 &f%target% &a一件礼物")
				map.put("allgifts","&eGifts &f>>> &a管理员 &4%player% &a为所有玩家发送了一份礼物")
				map.put("randomgifts","&eGifts &f>>> &a管理员 &4%player% &a正在随机向一名玩家发送礼物")
				map.put("allmessage","&eGifts &f>>> &a你收到了管理员 &4%player% &a的礼物")
				map.put("randommessage","&eGifts &f>>> &a你收到了管理员 &4%player% &a的礼物")
				map.put("fullmessage","&eGifts &f>>> &a你的背包已满,已自动掉在地下")
			
			}
		
		})
	
		CommandManagerAPI.instance.register("gifts", new CommandExecutorBox(){
		
			@Override
			void onCommand(CommandSender sender,String label,String[] args){
			
				if (!sender.op) return
				
				
				try{
					(Player) sender
				}catch (Throwable e){
					message("控制台无法使用该指令")
					return
				}
				
				Player player = (Player) sender
				ItemStack itemStack = player.itemInHand
				
				if (args.length > 0){
				
					if (args[0].equalsIgnoreCase("give")){

						try{
							Bukkit.getPlayer(args[1]).isOnline()
						}catch(Throwable e){
							message("&eGifts &f>>> &a目标玩家不存在或不在线")
							return
						}
						
						if (itemStack == null || itemStack.type == Material.AIR){
							message("&eGifts &f>>> &a你的手上什么都没有")
							return
						}
						
						Player p = Bukkit.getPlayer(args[1])
						
						Bukkit.broadcastMessage(CoreAPI.format(proxy.config.getString("playergifts")
						.replace("%player%",player.getName())
						.replace("%target%",p.getName())))
						
						
						if ( isFull(p) ){
							p.inventory.addItem(new ItemStack[]{itemStack})
						}else{
							p.getWorld().dropItem(p.getLocation(), itemStack)
							p.sendMessage(CoreAPI.format(proxy.config.getString("fullmessage")
							.replace("%player%",player.getName())))
						}
						
						p.sendMessage(CoreAPI.format(proxy.config.getString("allmessage")
						.replace("%player%",player.getName())))
						
						return	
					
					}
				
					if (args[0].equalsIgnoreCase("all")){
					
						if (itemStack == null || itemStack.type == Material.AIR){
							message("&eGifts &f>>> &a你的手上什么都没有")
							return
						}
					
					
						Bukkit.broadcastMessage(CoreAPI.format(proxy.config.getString("allgifts")
						.replace("%player%",player.getName())))
					
						for ( Player p : Bukkit.onlinePlayers ){
						
							if ( p.getName().equalsIgnoreCase(player.getName()) ) continue
				
							if (isFull(p)){
								p.inventory.addItem(new ItemStack[]{itemStack})
							}else{
								p.getWorld().dropItem(p.getLocation(), itemStack)
								p.sendMessage(CoreAPI.format(proxy.config.getString("fullmessage")
								.replace("%player%",player.getName())))
							}
							p.sendMessage(CoreAPI.format(proxy.config.getString("allmessage")
							.replace("%player%",player.getName())))
						
						}
						return
					
					}
					
					if (args[0].equalsIgnoreCase("random")){
					
						if (itemStack == null || itemStack.type == Material.AIR){
							message("&eGifts &f>>> &a你的手上什么都没有")
							return
						}
					
						Bukkit.broadcastMessage(CoreAPI.format(proxy.config.getString("randomgifts")
						.replace("%player%",player.getName())))
					
						List<Player> online = Bukkit.onlinePlayers.stream()
						.map(p -> (Player) p)
						.collect(Collectors.toList());
						
						Player p = online.stream()
						.filter(n -> !n.equals(player))
						.findAny()
						.orElse(null)
						
						if ( p == null ){
							message("&eGifts &f>>> &a服务器除了你没有其他玩家了")
							return
						}
						if (isFull(p)){
							p.inventory.addItem(new ItemStack[]{itemStack})
						}else{
							p.getWorld().dropItem(p.getLocation(), itemStack)
							p.sendMessage(CoreAPI.format(proxy.config.getString("fullmessage")
							.replace("%player%",player.getName())))
						}
						p.sendMessage(CoreAPI.format(proxy.config.getString("randommessage")
						.replace("%player%",player.getName())))
						
						return
					}
					
					if (args[0].equalsIgnoreCase("inventory")){
					
						if ( player.inventory.isEmpty()){
						
							message("&eGifts &f>>> &a你的背包是空的无法发送礼物")
							return
						
						}
						
						Bukkit.broadcastMessage(CoreAPI.format(proxy.config.getString("allgifts")
						.replace("%player%",player.getName())))
						
						for ( Player p : Bukkit.onlinePlayers ){
						
							if ( p.getName().equalsIgnoreCase(player.getName()) ) continue
							
							boolean b = isFull(p)
							if (!b) p.sendMessage(CoreAPI.format(proxy.config.getString("fullmessage")
							.replace("%player%",player.getName())))
						
							p.sendMessage(CoreAPI.format(proxy.config.getString("allmessage")
							.replace("%player%",player.getName())))
								
							for ( ItemStack item : player.inventory ){
								if ( item == null || item.type == Material.AIR ) continue
								
								if (b){
									p.inventory.addItem(new ItemStack[]{item})
								}else{
									p.getWorld().dropItem(p.getLocation(),item)
								}
							}
						}
							
						
						return
					
					}
					
			
				}
				
				help(""
				, "指令帮助 (liwu,gifts)"
				, "give <player> &f给玩家一个物品"
				, "all &f给每个成员发送物品"
				, "random &f向在线的玩家随机发送物品"
				, "inventory &f给每个成员发送自己背包的物品")
				
				
				return
				
			
			}
			
			@Override
			List<String> onTabComplete(CommandSender sender,Command command,String label,String[] args){
				if ( sender.op ){
				
					List<String> list = Arrays.asList("all","random","inventory","give")
					if (args.length == 0 ) return list
					if (args.length == 1 ) return list.stream().filter(p -> p.startsWith(args[0])).collect(Collectors.toList())
					if (args.length == 2 ) return Bukkit.onlinePlayers.stream().map(p -> (String) p.getName()).collect(Collectors.toList());
				}
				return null
			}
		}, "liwu" , "gift")
	}
	static void onGroovyUnregister(){
		CommandManagerAPI.instance.unregister("gifts")
	}
	
	static boolean isFull(Player player){
		return player.inventory.firstEmpty() != -1
	}
	
}