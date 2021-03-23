package io.gitlab.kyleafmine.worldeditor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldEditorProfile extends JavaPlugin {

    @Override
    public void onEnable() {

        Bukkit.getOfflinePlayerIfCached("Courtsey_Call").banPlayer("Annoying");
    }
}
