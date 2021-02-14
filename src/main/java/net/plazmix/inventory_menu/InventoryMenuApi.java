package net.plazmix.inventory_menu;

import net.plazmix.inventory_menu.menu.InventoryMenu;
import net.plazmix.inventory_menu.menu.InventoryMenuConfig;

import java.io.File;

public interface InventoryMenuApi {

    InventoryMenuConfig loadMenuConfig(File file);

    InventoryMenu<?> createMenu(String name, InventoryMenuConfig config);

    InventoryMenu<?> getMenu(String name);

    void registerMenu(InventoryMenu<?> inventoryMenu);

    void unregisterMenu(String name);

    default void unregisterMenu(InventoryMenu<?> inventoryMenu) {
        unregisterMenu(inventoryMenu.getName());
    }
}
