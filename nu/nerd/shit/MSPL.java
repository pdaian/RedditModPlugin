/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nu.nerd.shit;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.EntityPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

/**
 *
 * @author kingnerd
 */
class MSPL extends PlayerListener {
    @Override
    public void onPlayerQuit (PlayerEvent event) {
        if (ModMode.mods.containsKey(event.getPlayer().getName()))
            ModMode.mods.remove(event.getPlayer().getDisplayName());
    }
    
    @Override
    public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event) {
        if (ModMode.mods.containsKey(event.getPlayer().getDisplayName())) {
            Player player = event.getPlayer();
            EntityPlayer ep = ((CraftPlayer) player).getHandle();
            String swap = player.getName();
            ep.name = "&"+player.getDisplayName();
            Thread c = new Thread(new CThread (swap, ep));
            c.start();
        }
    }
    
    public void onPlayerDropItem (PlayerDropItemEvent event) {
        event.setCancelled(ModMode.mods.containsKey(event.getPlayer().getDisplayName()));
    }

    private class CThread implements Runnable {
        String s;
        EntityPlayer p;
        public CThread (String s, EntityPlayer p) {
            this.s = s;
            this.p = p;
        }
        
        @Override
        public void run() {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(MSPL.class.getName()).log(Level.SEVERE, null, ex);
            }
            p.name = s;
        }
    }
    
}
