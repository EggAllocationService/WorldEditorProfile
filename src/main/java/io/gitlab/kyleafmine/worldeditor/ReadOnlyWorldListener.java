package io.gitlab.kyleafmine.worldeditor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

public class ReadOnlyWorldListener implements Listener {
    public static ArrayList<String> readOnlyWorlds = new ArrayList<>();
    @EventHandler
    public void place(BlockPlaceEvent e) {
        if (readOnlyWorlds.contains(e.getBlock().getWorld().getName())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void destroy(BlockBreakEvent e) {
        if (readOnlyWorlds.contains(e.getBlock().getWorld().getName())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void interact(PlayerInteractEvent e ) {
        if (readOnlyWorlds.contains(e.getPlayer().getWorld().getName())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void bucket(PlayerBucketEmptyEvent e) {
        if (readOnlyWorlds.contains(e.getPlayer().getWorld().getName())) {
            e.setCancelled(true);
        }
    }
}
