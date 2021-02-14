package net.plazmix.inventory_menu.menu.content;

import lombok.Getter;
import net.plazmix.core.api.spigot.inventory.icon.Icon;
import org.bukkit.entity.Player;

import java.util.function.Function;

@Getter
public class PlayerIcon extends SlotIcon {

    private final Function<Player, Icon> iconFunction;

    public PlayerIcon(Function<Player, Icon> iconFunction, int slot) {
        super(iconFunction.apply(null), slot);
        this.iconFunction = iconFunction;
    }
}
