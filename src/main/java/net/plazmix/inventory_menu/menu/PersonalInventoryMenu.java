package net.plazmix.inventory_menu.menu;

import net.plazmix.core.api.spigot.inventory.paginator.GlobalPaginator;
import net.plazmix.core.api.spigot.inventory.paginator.PaginatorType;
import net.plazmix.core.api.spigot.inventory.view.PersonalViewInventory;
import net.plazmix.inventory_menu.menu.content.PlayerIcon;
import org.bukkit.plugin.Plugin;

public class PersonalInventoryMenu extends InventoryMenu<PersonalViewInventory> {

    public PersonalInventoryMenu(Plugin plugin, InventoryMenuConfig config, String name) {
        super(API.newPersonalViewInventory(plugin)
                .setTitleApplier((player, personalViewInventory) -> config.getTitle().replace("$PLAYER", player.getName()))
                .setType(config.getType())
                .setChestRows(config.getRows())
                .setOpeningAction(config.onOpen())
                .setClosingAction(config.onClose())
                .setPaginatorScheme(config.isPaginationEnabled() ? PaginatorType.GLOBAL : PaginatorType.NONE, config.getPaginationSchema())
                .build(), config.getViewType(), name, config.getPermission(), config.getCommands());

        for (PlayerIcon content : config.getContents())
            menu.setGlobalIcon(content.getSlot(), content.getIconFunction());

        if (config.isPaginationEnabled()) {
            GlobalPaginator paginator = (GlobalPaginator) menu.getPaginator();
            paginator.addContents(config.getPageContents());
            paginator.refresh();
        } else
            menu.refresh();
    }
}
