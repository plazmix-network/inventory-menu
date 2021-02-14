package net.plazmix.inventory_menu.menu.action;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.plazmix.core.api.bungee.util.Colors;
import net.plazmix.core.api.common.util.EnumUtils;
import net.plazmix.core.api.spigot.inventory.InventoryData;
import net.plazmix.core.api.spigot.inventory.view.PersonalViewInventory;
import net.plazmix.inventory_menu.InventoryMenuPlugin;
import org.bukkit.Sound;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Actions {

    private static final Map<Action, Function<String, Consumer<InventoryData>>> ACTION_MAP;

    static {
        ImmutableMap.Builder<Action, Function<String, Consumer<InventoryData>>> builder = ImmutableMap.builder();

        builder.put(Action.OPEN_OTHER, str -> data -> InventoryMenuPlugin.getApi().getMenu(str));
        builder.put(Action.CLOSE, str -> data -> data.getIssuer().closeInventory());
        builder.put(Action.COMMAND, str -> data -> data.getIssuer().chat("/" + str));
        builder.put(Action.MESSAGE, str -> data -> data.getIssuer().sendMessage(Colors.colorize(str).replace("$PLAYER", data.getIssuer().getName())));
        builder.put(Action.SOUND, str -> data -> {
            String[] soundData = str.split(";");
            String enumStr = soundData[0];
            float
                    volume = soundData.length >= 2 ? Float.parseFloat(soundData[1]) : 1F,
                    pitch = soundData.length >= 3 ? Float.parseFloat(soundData[2]) : 1F;
            Preconditions.checkArgument(EnumUtils.isValidEnum(Sound.class, enumStr), "Sound %s is not found!", enumStr);
            data.getIssuer().playSound(data.getIssuer().getLocation(), Sound.valueOf(enumStr), volume, pitch);
        });
        builder.put(Action.SERVER, str -> data -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(str);
            data.getIssuer().sendPluginMessage(data.getInventoryBase().getPlugin(), "BungeeCord", out.toByteArray());
        });
        builder.put(Action.REFRESH, str -> data -> {
            if (data.getInventoryBase() instanceof PersonalViewInventory) {
                PersonalViewInventory inventory = (PersonalViewInventory) data.getInventoryBase();
                if (inventory.getPaginator() != null)
                    inventory.getPaginator().refresh(data.getIssuer());
                else
                    inventory.refresh(data.getIssuer());
            } else
                data.getInventoryBase().refresh();
        });
        builder.put(Action.REFRESH_ALL, str -> data -> {
            if (!(data.getInventoryBase() instanceof PersonalViewInventory))
                data.getInventoryBase().refresh();

            PersonalViewInventory inventory = (PersonalViewInventory) data.getInventoryBase();
            if (inventory.getPaginator() == null)
                inventory.refresh();
            else
                inventory.getPaginator().refresh();
        });
        builder.put(Action.FIRST_PAGE, str -> data -> {
            if (!(data.getInventoryBase() instanceof PersonalViewInventory))
                data.getInventoryBase().refresh();

            PersonalViewInventory inventory = (PersonalViewInventory) data.getInventoryBase();
            if (inventory.getPaginator() != null)
                inventory.getPaginator().firstPage(data.getIssuer());
        });
        builder.put(Action.NEXT_PAGE, str -> data -> {
            if (!(data.getInventoryBase() instanceof PersonalViewInventory))
                data.getInventoryBase().refresh();

            PersonalViewInventory inventory = (PersonalViewInventory) data.getInventoryBase();
            if (inventory.getPaginator() != null)
                inventory.getPaginator().nextPage(data.getIssuer());
        });
        builder.put(Action.PREVIOUS_PAGE, str -> data -> {
            if (!(data.getInventoryBase() instanceof PersonalViewInventory))
                data.getInventoryBase().refresh();

            PersonalViewInventory inventory = (PersonalViewInventory) data.getInventoryBase();
            if (inventory.getPaginator() != null)
                inventory.getPaginator().previousPage(data.getIssuer());
        });
        builder.put(Action.LAST_PAGE, str -> data -> {
            if (!(data.getInventoryBase() instanceof PersonalViewInventory))
                data.getInventoryBase().refresh();

            PersonalViewInventory inventory = (PersonalViewInventory) data.getInventoryBase();
            if (inventory.getPaginator() != null)
                inventory.getPaginator().lastPage(data.getIssuer());
        });

        ACTION_MAP = builder.build();
    }

    public static Function<String, Consumer<InventoryData>> getActionFunction(Action action) {
        return ACTION_MAP.get(action);
    }
}
