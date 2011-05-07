package nu.nerd.shit;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

class DataStorage {
    private ItemStack[] inventory;
    private Location location;

    public DataStorage(ItemStack[] inventory, Location location) {
        this.inventory = inventory;
        this.location = location;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public Location getLocation() {
        return location;
    }
}
