package net.plazmix.inventory_menu.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import net.plazmix.core.api.common.config.Config;
import net.plazmix.core.api.spigot.inventory.ClickData;
import net.plazmix.core.api.spigot.inventory.InventoryData;
import net.plazmix.core.api.spigot.inventory.icon.Icon;
import net.plazmix.core.api.spigot.util.ItemBuilder;
import net.plazmix.inventory_menu.menu.action.Action;
import net.plazmix.inventory_menu.menu.action.Actions;
import net.plazmix.inventory_menu.menu.content.PlayerIcon;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ApiInventoryMenuConfig implements InventoryMenuConfig {

    private final Config<?> config;
    @Getter
    private final List<Icon> pageContents = Lists.newArrayList();
    @Getter
    private final Collection<PlayerIcon> contents = Sets.newLinkedHashSet();
    @Getter
    private final List<String> commands;
    @Getter
    private final String permission;

    public ApiInventoryMenuConfig(Config<?> config) {
        this.config = config;
        this.commands = config.getStringList("commands").stream().map(String::toLowerCase).collect(Collectors.toList());
        this.permission = config.getString("permission");

        if (isPaginationEnabled()) {
            Collection<String> keys = config.getKeys("pagination.content");
            for (String key : keys) {
                String texture = config.getString("pagination.content." + key + ".texture");
                ItemStack itemStack = ItemBuilder.toItemStack(config.getStringList("pagination.content." + key + ".icon"));
                ItemMeta itemMeta = itemStack.getItemMeta();

                if (texture != null && itemMeta instanceof SkullMeta) {
                    SkullMeta skullMeta = (SkullMeta) itemMeta;
                    if (texture.length() > 16) {
                        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                        profile.getProperties().put("textures", new Property("textures", texture));
                        try {
                            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                            mtd.setAccessible(true);
                            mtd.invoke(skullMeta, profile);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                            ex.printStackTrace();
                        }
                    } else
                        skullMeta.setOwner(texture);
                }

                int slot = config.getInt("pagination.content." + key + ".slot");
                Consumer<ClickData> click = convertAndCompile(config.getStringList("pagination.content." + key + ".on-click"));
                pageContents.add(Icon.of(itemStack, click));
            }
        }

        Collection<String> keys = config.getKeys("content");
        for (String key : keys) {
            String texture = config.getString("content." + key + ".texture");
            ItemStack itemStack = ItemBuilder.toItemStack(config.getStringList("content." + key + ".icon"));
            int slot = config.getInt("content." + key + ".slot");
            Consumer<ClickData> click = convertAndCompile(config.getStringList("content." + key + ".on-click"));
            contents.add(new PlayerIcon(player -> {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (texture != null && itemMeta instanceof SkullMeta) {
                    SkullMeta skullMeta = (SkullMeta) itemMeta;
                    if (texture.equalsIgnoreCase(player.getName()))
                        skullMeta.setOwner(player.getName());
                    else if (texture.length() > 16) {
                        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                        profile.getProperties().put("textures", new Property("textures", texture));
                        try {
                            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                            mtd.setAccessible(true);
                            mtd.invoke(skullMeta, profile);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                            ex.printStackTrace();
                        }
                    } else
                        skullMeta.setOwner(texture);
                }
                if (player != null) {
                    if (itemMeta.getDisplayName() != null)
                        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("$PLAYER", player.getName()));
                    if (itemMeta.getLore() != null)
                        itemMeta.setLore(itemMeta.getLore()
                                .stream()
                                .map(str -> str.replace("$PLAYER", player.getName()))
                                .collect(Collectors.toList()));
                    itemStack.setItemMeta(itemMeta);
                }
                return Icon.of(itemStack, click);
            }, slot));
        }
    }

    @Override
    public ViewType getViewType() {
        return ViewType.valueOf(config.getString("view").toUpperCase());
    }

    @Override
    public String getTitle() {
        return config.getString("title");
    }

    @Override
    public InventoryType getType() {
        return InventoryType.valueOf(config.getString("type").toUpperCase());
    }

    @Override
    public int getRows() {
        return config.getInt("rows");
    }

    @Override
    public Consumer<InventoryData> onOpen() {
        return compile(config.getStringList("on-open"));
    }

    @Override
    public Consumer<InventoryData> onClose() {
        return compile(config.getStringList("on-close"));
    }

    @Override
    public boolean isPaginationEnabled() {
        return config.getBoolean("pagination.enabled");
    }

    @Override
    public String getPaginationSchema() {
        return config.getString("pagination.schema");
    }

    private Consumer<InventoryData> compile(List<String> actions) {
        Collection<Consumer<InventoryData>> consumers = Sets.newLinkedHashSet();
        for (String actionScript : actions) {
            String[] actionData = actionScript.split(":");
            Action action = Action.valueOf(actionData[0].toUpperCase());
            consumers.add(Actions.getActionFunction(action).apply(actionData[1]));
        }
        return data -> {
            for (Consumer<InventoryData> consumer : consumers)
                consumer.accept(data);
        };
    }

    private Consumer<ClickData> convertAndCompile(List<String> actions) {
        return clickData -> compile(actions);
    }
}
