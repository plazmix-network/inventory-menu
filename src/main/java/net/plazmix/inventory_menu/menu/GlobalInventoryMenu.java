package net.plazmix.inventory_menu.menu;

import net.plazmix.core.api.spigot.inventory.view.GlobalViewInventory;
import net.plazmix.inventory_menu.menu.content.SlotIcon;
import org.bukkit.plugin.Plugin;

public class GlobalInventoryMenu extends InventoryMenu<GlobalViewInventory> {

    public GlobalInventoryMenu(Plugin plugin, InventoryMenuConfig config, String name) {
        super(API.newGlobalViewInventory(plugin)
                .setTitle(config.getTitle())
                .setType(config.getType())
                .setChestRows(config.getRows())
                .setOpeningAction(config.onOpen())
                .setClosingAction(config.onClose())
                .build(), config.getViewType(), name, config.getPermission(), config.getCommands());

        for (SlotIcon content : config.getContents())
            menu.setIcon(content.getSlot(), content.getIcon());
        menu.refresh();
    }
}
