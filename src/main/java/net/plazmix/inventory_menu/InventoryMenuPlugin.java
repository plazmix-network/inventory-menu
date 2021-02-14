package net.plazmix.inventory_menu;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import lombok.Getter;
import net.plazmix.core.api.spigot.config.SpigotYamlConfig;
import net.plazmix.inventory_menu.menu.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class InventoryMenuPlugin extends JavaPlugin implements InventoryMenuApi, Listener {

    @Getter
    public static InventoryMenuApi api;

    private final Map<String, InventoryMenu> registeredMenuMap = Maps.newHashMap();
    private final Map<String, InventoryMenu> commands = Maps.newHashMap();

    @Override
    public void onLoad() {
        api = this;
    }

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        File file = new File(this.getDataFolder() + File.separator + "example.yml");
        if (!file.exists())
            this.saveResource("example.yml", false);

        File menuFolder = new File(this.getDataFolder(), "menu");
        if (!menuFolder.exists())
            menuFolder.mkdir();

        Arrays.stream(menuFolder.listFiles(((dir, name) -> name.endsWith(".yml"))))
                .map(f -> createMenu(f.getName().replace(".yml", ""), loadMenuConfig(f)))
                .peek(menu -> getLogger().info("Loaded menu with name " + menu.getName()))
                .peek(menu -> {
                    for (String command : menu.getCommands())
                        commands.put(command, menu);
                })
                .forEach(this::registerMenu);

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    private void on(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().substring(1);
        if (commands.containsKey(command)) {
            event.setCancelled(true);
            InventoryMenu inventoryMenu = commands.get(command);
            if (!inventoryMenu.hasPermission() || event.getPlayer().hasPermission(inventoryMenu.getPermission()))
                inventoryMenu.open(event.getPlayer());
        }
    }

    @Override
    public InventoryMenuConfig loadMenuConfig(File file) {
        SpigotYamlConfig config = new SpigotYamlConfig(file);
        if (!file.exists())
            config.create();
        config.load();
        return new ApiInventoryMenuConfig(config);
    }

    @Override
    public InventoryMenu<?> createMenu(String name, InventoryMenuConfig config) {
        switch (config.getViewType()) {
            case GLOBAL:
                return new GlobalInventoryMenu(this, config, name);
            case PERSONAL:
                return new PersonalInventoryMenu(this, config, name);
        }
        throw new IllegalArgumentException("Unknown view type!");
    }

    @Override
    public InventoryMenu<?> getMenu(String name) {
        return registeredMenuMap.get(name);
    }

    @Override
    public void registerMenu(InventoryMenu inventoryMenu) {
        registeredMenuMap.put(inventoryMenu.getName(), inventoryMenu);
    }

    @Override
    public void unregisterMenu(String name) {
        registeredMenuMap.remove(name);
    }
}
