package io.gitlab.kyleafmine.worldeditor;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import de.themoep.minedown.MineDown;
import io.gitlab.kyleafmine.worldeditor.menus.WorldListMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MainCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            SlimePropertyMap slp = new SlimePropertyMap();
            slp.setInt(SlimeProperties.SPAWN_Y, 64);
            slp.setInt(SlimeProperties.SPAWN_X, 0);
            slp.setInt(SlimeProperties.SPAWN_Z, 0);
            slp.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
            slp.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
            SlimePlugin  s = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
            SlimeLoader sl = s.getLoader("mongodb");
            try {
                SlimeWorld sw = s.createEmptyWorld(sl, args[1], false, slp);
                s.generateWorld(sw);
                player.spigot().sendMessage(MineDown.parse("&aCreated empty world &d" + args[1]));
            } catch (WorldAlreadyExistsException | IOException e) {
                e.printStackTrace();
            }

        } else {
            new WorldListMenu((Player) player);
        }
        return true;
    }
}
