package nu.nerd.shit;

import java.util.HashMap;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
/**
 * 
 * @author Sanders
 */
public class ModMode extends JavaPlugin
{
	private MSPL			playerListener;
	private MSEL entityListener;
	private final HashMap<Player, Boolean>	debugees	= new HashMap<Player, Boolean>();
        public static HashMap<String, Boolean> mods = new HashMap<String, Boolean>();
        private HashMap<String, DataStorage> storeShit = new HashMap<String, DataStorage>();



	public boolean isDebugging(final Player player)
	{
		if (debugees.containsKey(player))
		{
			return debugees.get(player);
		}
		else
		{
			return false;
		}
	}


    @Override
	public void onDisable()
	{
		// TODO: Place any custom disable code here

		// NOTE: All registered events are automatically unregistered when a
		// plugin is disabled

		// EXAMPLE: Custom code, here we just output some info so we can check
		// all is well
		System.out.println("Goodbye world!");
	}

	public void onEnable()
	{
		// TODO: Place any custom enable code here including the registration of
		// any events
		// Register our events

		playerListener = new MSPL();
		entityListener = new MSEL();


		final PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
                pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
                pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.High, this);
                pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Low, this);

	}   

	public void setDebugging(final Player player, final boolean value)
	{
		debugees.put(player, value);
	}
        
        
        @Override
        public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
            if (args.length != 1 || !(sender instanceof Player)) {
                return false;
            }
            if (command.getName().equalsIgnoreCase("modmode") && args[0].equalsIgnoreCase("on")) {
                Player player = (Player) sender;
                if (mods.containsKey(player.getDisplayName())) {
                    player.sendMessage("Already in mod mode.");
                    return false;
                }
                EntityPlayer ep = ((CraftPlayer) player).getHandle();
                ModMode.mods.put(((Player) sender).getDisplayName(), true);
                storeShit.put(player.getDisplayName(), new DataStorage(player.getInventory().getContents(), player.getLocation()));
                String newname = player.getName();
                if (newname.length() > 11) {
                    newname = newname.substring(0, 11);
                }
                ep.name = ChatColor.GREEN+newname+ChatColor.WHITE;
                player.getInventory().clear();
                player.teleport(new Location(player.getWorld(),0.0,2000.0,0.0));
                player.performCommand("warp modcave");
                player.sendMessage(ChatColor.RED+"You are now in mod mode.");
                return true;
            }
            if (command.getName().equalsIgnoreCase("modmode") && args[0].equalsIgnoreCase("off")) {
                Player player = (Player) sender;
                if (!mods.containsKey(player.getDisplayName())) {
                    player.sendMessage("Not in mod mode.");
                    return false;
                }
                EntityPlayer ep = ((CraftPlayer) player).getHandle();
                ModMode.mods.remove(((Player) sender).getDisplayName());
                ep.name= player.getDisplayName();
                DataStorage stuff = storeShit.get(player.getDisplayName());
                player.teleport(stuff.getLocation());
                player.getInventory().setContents(stuff.getInventory());
                storeShit.remove(player.getDisplayName());
                player.sendMessage(ChatColor.RED+"You are no longer in mod mode.");
                return true;
            }
            return false;
        }

}
