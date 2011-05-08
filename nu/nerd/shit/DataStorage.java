package nu.nerd.shit;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

class DataStorage {
    private ItemStack[] inventory;
    private Location location;
    private String initialName;

    public DataStorage(ItemStack[] inventory, Location location, String initialName) {
        this.inventory = inventory;
        this.location = location;
        this.initialName = initialName;
    }

    public String getInitialName() {
        return initialName;
    }
    
    public ItemStack[] getInventory() {
        return inventory;
    }

    public Location getLocation() {
        return location;
    }
}
