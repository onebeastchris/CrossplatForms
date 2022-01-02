package dev.projectg.crossplatforms.form.java;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.config.ConfigId;
import dev.projectg.crossplatforms.reloadable.Reloadable;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class JavaMenuRegistry implements Reloadable {

    public static final String DEFAULT = "default";

    /**
     * If java menus are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    private boolean isEnabled;
    private final Map<String, JavaMenu> enabledMenus = new HashMap<>();

    public JavaMenuRegistry() {
        ReloadableRegistry.registerReloadable(this);
        isEnabled = load();
    }

    private boolean load() {
        FileConfiguration config = CrossplatForms.getInstance().getConfigManager().getFileConfiguration(ConfigId.FORMS);
        Logger logger = Logger.getLogger();

        enabledMenus.clear();

        if (config.contains("Java-Selector", true) && config.isConfigurationSection("Java-Selector")) {
            ConfigurationSection selectorSection = config.getConfigurationSection("Java-Selector");
            Objects.requireNonNull(selectorSection);

            if (selectorSection.contains("Enable", true) && selectorSection.isBoolean("Enable")) {
                if (selectorSection.getBoolean("Enable")) {
                    if (selectorSection.contains("Menus", true) && selectorSection.isConfigurationSection("Menus")) {
                        ConfigurationSection menus = selectorSection.getConfigurationSection("Menus");
                        Objects.requireNonNull(menus);

                        boolean noSuccess = true;
                        boolean containsDefault = false;
                        for (String entry : menus.getKeys(false)) {
                            if (!menus.isConfigurationSection(entry)) {
                                logger.warn("Java menu with name " + entry + " is being skipped because it is not a configuration section");
                                continue;
                            }
                            ConfigurationSection formInfo = menus.getConfigurationSection(entry);
                            Objects.requireNonNull(formInfo);
                            JavaMenu menu = new JavaMenu(formInfo);
                            if (menu.isEnabled) {
                                enabledMenus.put(entry, menu);
                                noSuccess = false;
                            } else {
                                logger.warn("Not adding Java manu for config section: " + entry + " because there was a failure loading it.");
                            }
                            if ("default".equals(entry)) {
                                containsDefault = true;
                            }
                        }

                        if (!containsDefault) {
                            logger.warn("Failed to load a default Java menus! The Java Server Selector compass will not work and players will not be able to open the default form with \"/ghub\"");
                        }
                        if (noSuccess) {
                            logger.warn("Failed to load ALL Java menus, due to configuration error.");
                        } else {
                            logger.info("Valid Java menus are: " + enabledMenus.keySet());
                            return true;
                        }
                    }
                } else {
                    logger.debug("Not enabling Java menus because it is disabled in the config.");
                }
            } else {
                logger.warn("Not enabling Java menus because the Enable value is not present in the config.");
            }
        } else {
            logger.warn("Not enabling Java menus because the whole configuration section is not present.");
        }
        return false;
    }

    /**
     * @return True, if Java menus are enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Get a Java menu, based off its name.
     * @param menuName The menu name
     * @return the JavaMenu, null if it doesn't exist.
     */
    @Nullable
    public JavaMenu getMenu(@Nonnull String menuName) {
        Objects.requireNonNull(menuName);
        return enabledMenus.get(menuName);
    }

    /**
     * Attempt to retrieve the menu that an ItemStack points to
     * @param itemStack The ItemStack to check. If it contains null ItemMeta, this will return null.
     * @return The menu if the ItemStack contained the menu name and the menu exists. If no menu name was contained or the menu contained doesn't exist, this will return null.
     */
    @Nullable
    public JavaMenu getMenu(@Nonnull ItemStack itemStack) {
        String menuName = getMenuName(itemStack);
        if (menuName == null) {
            return null;
        } else {
            return getMenu(menuName);
        }
    }

    /**
     * Attempt to retrieve the menu name that an ItemStack is contained in
     * @param itemStack The ItemStack to check
     * @return The menu name if the ItemStack contained the menu name, null if not. ItemStacks with null ItemMeta will always return null.
     */
    @Nullable
    public String getMenuName(@Nonnull ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            return meta.getPersistentDataContainer().get(JavaMenu.BUTTON_KEY, JavaMenu.BUTTON_KEY_TYPE);
        }
        return null;
    }

    @Override
    public boolean reload() {
        isEnabled = load();
        return true;
    }
}
