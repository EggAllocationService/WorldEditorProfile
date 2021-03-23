package io.gitlab.kyleafmine.worldeditor.menus;

import com.google.common.collect.Lists;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import de.themoep.minedown.MineDown;
import io.gitlab.kyleafmine.worldeditor.ReadOnlyWorldListener;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldListMenu implements Listener {
    Player p;
    Inventory i;
    SlimePlugin sp;
    SlimeLoader sl;
    public HashMap<Integer, String> currentPageMap = new HashMap<>();
    int requiredPages;
    int currentPage = 0;
    List<List<String>> pages;
    public WorldListMenu(Player ps) {
        p = ps;
        i = Bukkit.createInventory(null, 54, "Worlds");
        sp = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        assert sp != null;
        sl = sp.getLoader("mongodb");
        init();
    }
    public void init() {
        List<String> worlds;
        try {
            worlds = sl.listWorlds();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        requiredPages = (int) Math.ceil(worlds.size() / 45);
        pages = Lists.partition(worlds, 45);
        assert pages.size() == requiredPages;

    }
    public void render() {
        i.clear();
        currentPageMap.clear();
        List<String> toRender = pages.get(currentPage);
        int index = 0;
        for (String s : toRender) {
            currentPageMap.put(index, s);
            i.setItem(index, iconHelper(s, Bukkit.getWorld(s) != null));
        }
    }

    public ItemStack iconHelper(String s, Boolean b) {


        ItemStack h = new ItemStack(Material.GRASS_BLOCK, 1);
        ItemMeta hm = h.getItemMeta();
        hm.setDisplayNameComponent(MineDown.parse("&#6bc900&" + s));
        ArrayList<BaseComponent[]> lore = new ArrayList<>();
        if (b) {
            lore.add(MineDown.parse("&#919191&World is currently loaded."));
            lore.add(MineDown.parse("&#ff3721&Q to unload world"));
            h.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            h.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        } else {
            lore.add(MineDown.parse("&#7ce35d&Left-Click to edit world"));
            lore.add(MineDown.parse("&#dfe312&Right-Click to view world"));
        }

        hm.setLoreComponents(lore);

        h.setItemMeta(hm);
        return h;
    }

    public void loadWorld(String s, Player p, Boolean lock) {
        try {
            SlimePropertyMap slp = new SlimePropertyMap();
            slp.setInt(SlimeProperties.SPAWN_Y, 64);
            slp.setInt(SlimeProperties.SPAWN_X, 0);
            slp.setInt(SlimeProperties.SPAWN_Z, 0);
            slp.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
            slp.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
            SlimeWorld sw = sp.loadWorld(sl, s, lock, slp);
            sp.generateWorld(sw);
            p.closeInventory();
            p.teleport(Bukkit.getWorld(s).getSpawnLocation());

        } catch (UnknownWorldException e) {
            e.printStackTrace();
            p.spigot().sendMessage(MineDown.parse("&#fc2d4c&There was an error loading the world."));
        } catch (WorldInUseException e) {
            p.spigot().sendMessage(MineDown.parse("&#fc2d4c&World is in use by another server! You can still view read-only"));
            return;
        } catch (IOException e) {
            e.printStackTrace();
            p.spigot().sendMessage(MineDown.parse("&#fc2d4c&There was an error loading the world."));
        } catch (NewerFormatException e) {
            e.printStackTrace();
            p.spigot().sendMessage(MineDown.parse("&#fc2d4c&There was an error loading the world."));
        } catch (CorruptedWorldException e) {
            e.printStackTrace();
            p.spigot().sendMessage(MineDown.parse("&#fc2d4c&The world is corrupted."));
        } catch (NullPointerException e) {
            e.printStackTrace();
            p.spigot().sendMessage(MineDown.parse("&#fc2d4c&There was an error loading the world."));
        }
    }


    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getClickedInventory() == i) {
            e.setCancelled(true);
        }
        if (e.getClick() == ClickType.LEFT) {
            //left click on shit
            String clicked = currentPageMap.get(e.getSlot());
            if (clicked == null) {
                return;
            }
            if (Bukkit.getWorld(clicked) == null) {
                // world is unloaded, time to load!
                loadWorld(clicked, p, true);
            } else {
                return;
            }
        } else if (e.getClick() == ClickType.RIGHT) {
            //right click: load read-only
            String clicked = currentPageMap.get(e.getSlot());
            if (clicked == null) {
                return;
            }
            if (Bukkit.getWorld(clicked) == null) {
                // world is unloaded, time to load! (but load read-only)
                ReadOnlyWorldListener.readOnlyWorlds.add(clicked);
                loadWorld(clicked, p, false);

            } else {
                return;
            }
        } else if (e.getClick() == ClickType.DROP) {
            // drop key was pressed, check if world is loaded
            String clicked = currentPageMap.get(e.getSlot());
            if (Bukkit.getWorld(clicked) != null) {
                // world exists, unload
                // make sure everyone is safe
                List<Player> psw = Bukkit.getWorld(clicked).getPlayers();
                if (psw.size() != 0) {
                    // there are players in the world, teleport before unload!
                    for (Player c: psw) {
                        c.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
                    }
                }
                if (ReadOnlyWorldListener.readOnlyWorlds.contains(clicked)) {
                    Bukkit.unloadWorld(clicked, false);
                    ReadOnlyWorldListener.readOnlyWorlds.remove(clicked);
                } else {
                    Bukkit.unloadWorld(clicked, true);
                }

            }
        }
    }
}
