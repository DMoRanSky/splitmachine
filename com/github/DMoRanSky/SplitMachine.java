/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.DMoRanSky;

import static com.github.DMoRanSky.event.SplitInv;
import static com.github.DMoRanSky.event.barr;
import static com.github.DMoRanSky.event.isSplit;
import java.io.File;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author HardyXiang
 */
public class SplitMachine extends JavaPlugin {

    @Override
    public void onEnable() {
        Eventstart();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "config.yml");
        if (!(file.exists())) {
            saveDefaultConfig();
        }
        reloadConfig();
        getLogger().info("拆解台插件已加载 作者MoRan_Sky");

    }

    @Override
    public void onDisable() {
        getLogger().info("拆解台插件已卸载");
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("splitmachine")) {
           
            if (sender.hasPermission("SplitMachine.splitmachine")) {
               
                if (getConfig().getBoolean("EnableSplitCommand")) {
                   if(sender instanceof Player){
                    openSplitInventory1((Player) sender);
                    Player p = (Player) sender;
                    isSplit.put(p.getUniqueId(), true);
                    return true;
                   }else{
                   sender.sendMessage("只有玩家才能使用拆解台命令");
                   }
                }
                return false;
            } else {
                sender.sendMessage("你没有权限");
                return false;

            }

        }
        return false;

    }

    public void openSplitInventory1(Player player) {
        SplitInv.put(player.getUniqueId(), getServer().createInventory(player, 36, "拆解台"));
        Inventory inv = (Inventory) SplitInv.get(player.getUniqueId());
        for (int i = 0; i <= 35; i++) {
            inv.setItem(i, barr);
        }
        for (int i = 15; i <= 17; i++) {
            inv.clear(i);
        }
        for (int i = 24; i <= 26; i++) {
            inv.clear(i);
        }
        for (int i = 33; i <= 35; i++) {
            inv.clear(i);
        }
        inv.clear(10);
        inv.clear(19);
        inv.setItem(12, buildItem(Material.EMERALD, 1, (short) 0, "§6拆解"));
        inv.setItem(13, buildItem(Material.REDSTONE, 1, (short) 0, "§6取回"));
        inv.setItem(28, buildItem(Material.WORKBENCH, 1, (short) 0, "§6预览"));
        player.openInventory(inv);

    }

    public void Eventstart() {
        getServer().getPluginManager().registerEvents(new event(this), this);
    }

    /**
     * @param type
     * @param amount
     * @param damage
     * @param name
     * @return
     */
    public ItemStack buildItem(Material type, int amount, short damage, String name) {

        ItemStack itemStack = new ItemStack(type, amount);
        if (damage != 0) {
            itemStack.setDurability(damage);
        }
        if (name != null) {
            ItemMeta Meta = itemStack.getItemMeta();
            Meta.setDisplayName(name);
            itemStack.setItemMeta(Meta);
        }

        return itemStack;
    }
}
