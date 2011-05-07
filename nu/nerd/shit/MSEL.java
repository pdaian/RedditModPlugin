/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nu.nerd.shit;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

/**     
 *
 * @author kingnerd
 */
class MSEL extends EntityListener {

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entEvent = (EntityDamageByEntityEvent) event;
            if ((entEvent.getDamager() instanceof Player) && (entEvent.getEntity() instanceof Player)) {
                Player damager = (Player) entEvent.getDamager();
                Player damagee = (Player) entEvent.getEntity();
                if (ModMode.mods.containsKey(damager.getDisplayName())) {
                    damager.sendMessage("You cannot do damage while in mod mode.");
                    event.setCancelled(true);
                }
                else if (ModMode.mods.containsKey(damagee.getDisplayName())) {
                    damager.sendMessage("This mod is in mod-mode.");
                    damager.sendMessage("This means you cannot damage them and they cannot deal damage.");
                    damager.sendMessage("Mod mod should only be used for official server business.");
                    damager.sendMessage("Please let an admin know if a mod is abusing mod-mode.");
                    event.setCancelled(true);
                }
            }
        }
        else if (event.getEntity() instanceof Player && ModMode.mods.containsKey(((Player) event.getEntity()).getDisplayName()))
            event.setCancelled(true);
    }
}
