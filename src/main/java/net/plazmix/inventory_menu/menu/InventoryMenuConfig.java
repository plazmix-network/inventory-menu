package net.plazmix.inventory_menu.menu;

import net.plazmix.core.api.spigot.inventory.InventoryData;
import net.plazmix.core.api.spigot.inventory.icon.Icon;
import net.plazmix.inventory_menu.menu.content.PlayerIcon;
import org.bukkit.event.inventory.InventoryType;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface InventoryMenuConfig {

    ViewType getViewType();

    String getTitle();

    InventoryType getType();

    int getRows();

    Consumer<InventoryData> onOpen();

    Consumer<InventoryData> onClose();

    boolean isPaginationEnabled();

    String getPaginationSchema();

    List<Icon> getPageContents();

    Collection<PlayerIcon> getContents();

    List<String> getCommands();

    String getPermission();
}
