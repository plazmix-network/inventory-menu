package net.plazmix.inventory_menu.menu.content;

import lombok.Data;
import net.plazmix.core.api.spigot.inventory.icon.Icon;

@Data
public class SlotIcon {

    private final Icon icon;
    private final int slot;
}
