package io.gitlab.kyleafmine.worldeditor;


import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldEditorProfile extends JavaPlugin {
    public static WorldEditorProfile instance;

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("So this is a bad idea i don't know if hotloading is great");
        getCommand("worlds").setExecutor(new MainCommand());
        Bukkit.getPluginManager().registerEvents(new ReadOnlyWorldListener(), this);
    }
}
