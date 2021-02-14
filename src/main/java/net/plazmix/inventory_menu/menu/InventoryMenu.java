package net.plazmix.inventory_menu.menu;

import net.plazmix.core.api.Core;
import net.plazmix.core.api.spigot.SpigotCoreApi;
import net.plazmix.core.api.spigot.inventory.ClickData;
import net.plazmix.core.api.spigot.inventory.icon.Icon;
import net.plazmix.core.api.spigot.inventory.view.InventoryBase;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InventoryMenu<T extends InventoryBase> {

    static final SpigotCoreApi API = (SpigotCoreApi) Core.getApi();

    protected final T menu;
    private final ViewType viewType;
    private final String name, permission;
    private final List<String> commands;

    public InventoryMenu(T menu, ViewType viewType, String name) {
        this(menu, viewType, name, "", Collections.emptyList());
    }

    public InventoryMenu(T menu, ViewType viewType, String name, String permission, List<String> commands) {
        this.menu = menu;
        this.viewType = viewType;
        this.name = name;
        this.permission = permission;
        this.commands = commands;
    }

    public T unwrap() {
        return menu;
    }

    public String getName() {
        return name;
    }

    public boolean hasPermission() {
        return permission != null && !permission.isEmpty();
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getCommands() {
        return commands;
    }

    public InventoryType getType() {
        return menu.getType();
    }

    public Collection<Player> getCurrentViewers() {
        return menu.getCurrentViewers();
    }

    public Icon getIcon(ClickData clickData) {
        return menu.getIcon(clickData);
    }

    public int getSlots() {
        return menu.getSlots();
    }

    public boolean isViewing(Player player) {
        return menu.isViewing(player);
    }

    public void open(Player player) {
        menu.open(player);
    }
}
