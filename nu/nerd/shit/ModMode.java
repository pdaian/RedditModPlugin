package nu.nerd.shit;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ModMode extends JavaPlugin {

    private MSPL playerListener;
    private MSEL entityListener;
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    public static HashMap<String, Boolean> mods = new HashMap<String, Boolean>();
    public static PermissionHandler permissionHandler;

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    @Override
    public void onDisable() {
        System.out.println("Goodbye world!");
    }

    @Override
    public void onEnable() {
        load();
        playerListener = new MSPL(this);
        entityListener = new MSEL();
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.High, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.High, this);
        setupPermissions();

    }

    private void setupPermissions() {
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (this.permissionHandler == null) {
            if (permissionsPlugin != null) {
                this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
            } else {
                this.getServer().getLogger().info("Permission system not detected, defaulting to OP");
            }
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length != 1 || !(sender instanceof Player)) {
            return false;
        }
        if (!ModMode.permissionHandler.has((Player)sender, "modmode.mod")) {
            sender.sendMessage("No permissions");
            return false;
        }
        if (command.getName().equalsIgnoreCase("modmode") && args[0].equalsIgnoreCase("on")) {
            Player player = (Player) sender;
            if (mods.containsKey(player.getDisplayName())) {
                player.sendMessage("Already in mod mode.");
                return false;
            }
            player.saveData();
            EntityPlayer ep = ((CraftPlayer) player).getHandle();
            ModMode.mods.put(((Player) sender).getDisplayName(), true);
            String newname = player.getDisplayName().length() > 11 ? player.getDisplayName().substring(0, 11) : player.getDisplayName();
            ep.name = ChatColor.GREEN + newname + ChatColor.WHITE;
            player.getInventory().clear();
            player.sendMessage(ChatColor.RED + "You are now in mod mode.");
            refreshName(player);
            save();
            return true;
        }
        if (command.getName().equalsIgnoreCase("modmode") && args[0].equalsIgnoreCase("off")) {
            Player player = (Player) sender;
            EntityPlayer ep = ((CraftPlayer) player).getHandle();
            if (!mods.containsKey(player.getDisplayName())) {
                player.sendMessage("Not in mod mode.");
                return false;
            }
            ModMode.mods.remove(((Player) sender).getDisplayName());
            player.kickPlayer(ChatColor.RED + "You are no longer in mod mode.");
            save();
            return true;
        }
        return false;
    }

    public void refreshName(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        for (Player p : this.getServer().getOnlinePlayers()) {
            if (getDistance(p, player) < 512 && !p.equals(player)) {
                CraftPlayer one = (CraftPlayer) p;
                one.getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(((CraftPlayer) player).getEntityId()));
                one.getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(ep));
            }
        }
    }

    // Shamelessly stolen from Vanish plugin.
    public double getDistance(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player1.getLocation();
        return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2) + Math.pow(loc1.getY() - loc2.getY(), 2) + Math.pow(loc1.getZ() - loc2.getZ(), 2));
    } // End shame

    private void save() {
        try {
            FileOutputStream fout = new FileOutputStream("ModModeMods.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(ModMode.mods);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            FileInputStream fin = new FileInputStream("ModModeMods.dat");
            ObjectInputStream ois = new ObjectInputStream(fin);
            HashMap<String, Boolean> saver = (HashMap<String, Boolean>) ois.readObject();
            ModMode.mods = saver;
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
