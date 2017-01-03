/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.DMoRanSky;

import java.util.HashMap;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 *
 * @author HardyXiang
 */
public class event implements Listener {

    public static final HashMap SplitInv = new HashMap();
    public static final HashMap isSplit = new HashMap();
    public static ItemStack barr;

    private final SplitMachine plugin;

    public event(SplitMachine plugin) {
        this.plugin = plugin;
        barr = buildItem(Material.STAINED_GLASS_PANE, 1, (short) 7, "§3屏障");
    }

    @EventHandler
    public void closeEvent(InventoryCloseEvent event) {
        if (event.getInventory().getTitle().equals("拆解台")) {

            if ((Boolean) isSplit.get(event.getPlayer().getUniqueId())) {

                if (event.getInventory().getItem(10) != null) {
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), event.getInventory().getItem(10));

                }
                if (event.getInventory().getItem(19) != null) {
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), event.getInventory().getItem(19));

                }
                isSplit.put(event.getPlayer().getUniqueId(), false);
            }

        }

    }

    @EventHandler
    public void clickEvent(InventoryClickEvent event) {

        if ("拆解台".equals(event.getInventory().getTitle())) {

            if (event.getRawSlot() != 10 && event.getRawSlot() != 19 && event.getRawSlot() < 36) {
                event.setCancelled(true);
            } else if (event.getRawSlot() >= 36) {
                if (event.isShiftClick()) {
                    event.setCancelled(true);
                    if (event.getInventory().getItem(10) == null) {
                        event.getInventory().setItem(10, event.getCurrentItem());
                        event.getWhoClicked().getInventory().clear(event.getSlot());

                    } else if (event.getInventory().getItem(19) == null) {
                        if (event.getCurrentItem().getType() == Material.BOOK || event.getCurrentItem().getType() == Material.ENCHANTED_BOOK) {
                            if (event.getCurrentItem().getAmount() == 1) {
                                event.getInventory().setItem(19, event.getCurrentItem());
                                event.getWhoClicked().getInventory().clear(event.getSlot());

                            } else if (event.getCurrentItem().getAmount() > 1) {
                                ItemStack bt1 = event.getCurrentItem();
                                ItemStack bt2 = bt1;
                                bt1.setAmount(bt1.getAmount() - 1);
                                event.getWhoClicked().getInventory().setItem(event.getSlot(), bt1);
                                bt2.setAmount(1);
                                event.getInventory().setItem(19, bt2);

                            }

                        }
                    }

                }
            }
            if (event.getCurrentItem() != null) {
                if (event.getCurrentItem().hasItemMeta()) {
                    if (event.getCurrentItem().getItemMeta().getDisplayName() != null) {
                        switch (event.getCurrentItem().getItemMeta().getDisplayName()) {
                            case "§6预览":
                                event.setCancelled(true);
                                if (event.getInventory().getItem(10) != null) {
                                    //  this.plugin.getConfig().getBoolean("CanSplitDisplayNameItem") 
                                    if (this.plugin.getConfig().getBoolean("CanSplitDisplayNameItem") || (!this.plugin.getConfig().getBoolean("CanSplitDisplayNameItem") && event.getInventory().getItem(10).getItemMeta().getDisplayName() == null)) {
                                        if (!event.getInventory().getItem(10).hasItemMeta() || (event.getInventory().getItem(10).hasItemMeta() && event.getInventory().getItem(10).getItemMeta().getDisplayName() == null) || this.plugin.getConfig().getStringList("NotSplitNameList").indexOf(event.getInventory().getItem(10).getItemMeta().getDisplayName()) == -1) {
                                            if (event.getInventory().getItem(19) == null || ((event.getInventory().getItem(19).getType() == Material.BOOK || event.getInventory().getItem(19).getType() == Material.ENCHANTED_BOOK) && event.getInventory().getItem(19).getAmount() == 1)) {

                                                if (plugin.getServer().getRecipesFor(event.getInventory().getItem(10)).toArray().length > 0 || !plugin.getServer().getRecipesFor(event.getInventory().getItem(10)).isEmpty()) {
                                                    Recipe rec = null;
                                                    for (Object toArray : plugin.getServer().getRecipesFor(event.getInventory().getItem(10)).toArray()) {
                                                        if (toArray instanceof ShapedRecipe || toArray instanceof ShapelessRecipe) {
                                                            rec = (Recipe) toArray;
                                                            break;

                                                        }

                                                    }

                                                    if (rec != null && (rec instanceof ShapedRecipe || rec instanceof ShapelessRecipe)) {
                                                        isSplit.put(event.getWhoClicked().getUniqueId(), false);

                                                        openSplitInventory2((Player) event.getWhoClicked(), rec, event.getInventory().getItem(10), event.getInventory().getItem(19));
                                                        isSplit.put(event.getWhoClicked().getUniqueId(), true);
                                                    } else {
                                                        event.getWhoClicked().sendMessage("§6该物品没有拆解配方");
                                                    }

                                                } else {
                                                    event.getWhoClicked().sendMessage("§6该物品没有拆解配方");
                                                }

                                            } else {
                                                event.getWhoClicked().sendMessage("§6附魔栏必须是空或者是书且数量为1");
                                            }
                                        }
                                    }
                                } else {
                                    event.getWhoClicked().sendMessage("§6没有东西可以拆解");
                                }
                                break;
                            case "§6取回":
                                event.setCancelled(true);
                                event.getWhoClicked().closeInventory();
                                break;
                            case "§6拆解":
                                event.setCancelled(true);
                                if (event.getInventory().getItem(10) != null) {
                                    if (this.plugin.getConfig().getBoolean("CanSplitDisplayNameItem") || (!this.plugin.getConfig().getBoolean("CanSplitDisplayNameItem") && event.getInventory().getItem(10).getItemMeta().getDisplayName() == null)) {
                                        if (event.getInventory().getItem(19) == null || ((event.getInventory().getItem(19).getType() == Material.BOOK || event.getInventory().getItem(19).getType() == Material.ENCHANTED_BOOK) && event.getInventory().getItem(19).getAmount() == 1)) {
                                            if (!event.getInventory().getItem(10).hasItemMeta() || (event.getInventory().getItem(10).hasItemMeta() && event.getInventory().getItem(10).getItemMeta().getDisplayName() == null) || this.plugin.getConfig().getStringList("NotSplitNameList").indexOf(event.getInventory().getItem(10).getItemMeta().getDisplayName()) == -1) {
                                                if (plugin.getServer().getRecipesFor(event.getInventory().getItem(10)).toArray().length > 0) {
                                                    Recipe rec = null;
                                                    for (Object toArray : plugin.getServer().getRecipesFor(event.getInventory().getItem(10)).toArray()) {
                                                        if (toArray instanceof ShapedRecipe || toArray instanceof ShapelessRecipe) {
                                                            rec = (Recipe) toArray;
                                                        }
                                                    }
                                                    if (rec != null) {
                                                        Player p = (Player) event.getWhoClicked();
                                                        if (p.getLevel() >= this.plugin.getConfig().getInt("SubtractLevel")) {
                                                            ItemStack item = event.getInventory().getItem(10);
                                                            Inventory inv = event.getInventory();
                                                            for (int a = 1; a <= Math.floor(item.getAmount() / rec.getResult().getAmount()); a++) {
                                                                if (rec instanceof ShapedRecipe) {

                                                                    for (ItemStack value : ((ShapedRecipe) rec).getIngredientMap().values()) {
                                                                        if (value != null && value.getType() != Material.AIR) {

                                                                            if (value.getDurability() == 32767) {
                                                                                ItemStack ne = value;
                                                                                ne.setDurability((short) 0);

                                                                                if (event.getWhoClicked().getInventory().firstEmpty() == -1) {

                                                                                    event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), ne);
                                                                                } else {

                                                                                    event.getWhoClicked().getInventory().addItem(ne);
                                                                                }
                                                                            } else {

                                                                                if (event.getWhoClicked().getInventory().firstEmpty() == -1) {
                                   
                                                                                    event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), value);
                                                                                } else {

                                                                                    event.getWhoClicked().getInventory().addItem(value);
                                                                                }
                                                                            }

                                                                        }
                                                                    }
                                                                } else if (rec instanceof ShapelessRecipe) {
                                                                    for (Object toArray : ((ShapelessRecipe) rec).getIngredientList().toArray()) {
                                                                        ItemStack value = (ItemStack) toArray;
                                                                        if ((ItemStack) toArray != null && value.getType() != Material.AIR)  {
                                                                            
                                                                            if (value.getDurability() == 32767) {
                                                                                ItemStack ne = value;
                                                                                ne.setDurability((short) 0);

                                                                                if (event.getWhoClicked().getInventory().firstEmpty() == -1) {

                                                                                    event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), ne);
                                                                                } else {

                                                                                    event.getWhoClicked().getInventory().addItem(ne);
                                                                                }
                                                                            } else {

                                                                                if (event.getWhoClicked().getInventory().firstEmpty() == -1) {

                                                                                    event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), value);
                                                                                } else {

                                                                                    event.getWhoClicked().getInventory().addItem(value);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            ItemStack book = event.getInventory().getItem(19);
                                                            if (book != null) {
                                                                ItemStack bk = book;
                                                                if (item.getEnchantments().values().toArray().length > 0 && book.getType() == Material.BOOK) {
                                                                    bk.setType(Material.ENCHANTED_BOOK);
                                                                }
                                                                item.getEnchantments().entrySet().forEach((entry) -> {
                                                                    bk.addUnsafeEnchantment(entry.getKey(), entry.getValue());

                                                                });
                                                                if (event.getWhoClicked().getInventory().firstEmpty() == -1) {
                                                                    event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), bk);

                                                                } else {

                                                                    event.getWhoClicked().getInventory().addItem(bk);

                                                                }
                                                            }
                                                            p.setLevel(p.getLevel() - this.plugin.getConfig().getInt("SubtractLevel"));
                                                            isSplit.put(event.getWhoClicked().getUniqueId(), false);
                                                            openSplitInventory1((Player) event.getWhoClicked());
                                                            isSplit.put(event.getWhoClicked().getUniqueId(), true);
                                                        } else {
                                                            event.getWhoClicked().sendMessage("§6等级不足，还差" + (this.plugin.getConfig().getInt("SubtractLevel") - p.getLevel()) + "等级");
                                                        }

                                                    } else {
                                                        event.getWhoClicked().sendMessage("§6该物品没有拆解配方");
                                                    }

                                                } else {
                                                    event.getWhoClicked().sendMessage("§6该物品没有拆解配方");

                                                }
                                            }
                                        } else {
                                            event.getWhoClicked().sendMessage("§6附魔栏必须是空或者是书且数量为1");
                                        }

                                    }

                                } else {
                                    event.getWhoClicked().sendMessage("§6没有东西可以拆解");
                                }
                                break;
                            default:
                                break;
                        }

                    }
                }
            }

        }
    }

    @EventHandler
    public void openSplitInventoryEvent(PlayerInteractEvent event) {
        if (event.hasBlock()) {
            if (event.getClickedBlock().getType() == Material.WORKBENCH) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                    if (event.getPlayer().isSneaking()) {
                        if (this.plugin.getConfig().getBoolean("EnableSplit")) {
                            event.setCancelled(true);
                            openSplitInventory1(event.getPlayer());
                            isSplit.put(event.getPlayer().getUniqueId(), true);
                        } else {
                            event.getPlayer().sendMessage("禁用拆解台");

                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param player
     */
    public void openSplitInventory1(Player player) {
        SplitInv.put(player.getUniqueId(), this.plugin.getServer().createInventory(player, 36, "拆解台"));
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

    public void openSplitInventory2(Player player, Recipe rec, ItemStack item, ItemStack book) {

        SplitInv.put(player.getUniqueId(), this.plugin.getServer().createInventory(player, 36, "拆解台"));
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

        inv.setItem(12, buildItem(Material.EMERALD, 1, (short) 0, "§6拆解"));
        inv.setItem(13, buildItem(Material.REDSTONE, 1, (short) 0, "§6取回"));
        inv.setItem(28, buildItem(Material.WORKBENCH, 1, (short) 0, "§6预览"));
        inv.setItem(10, item);

        for (int a = 1; a <= Math.floor(item.getAmount() / rec.getResult().getAmount()); a++) {
            if (rec instanceof ShapedRecipe) {

                ((ShapedRecipe) rec).getIngredientMap().values().stream().filter((value) -> (value != null && value.getType() != Material.AIR)).forEachOrdered((value) -> {
                    if (value.getDurability() == 32767) {
                        ItemStack ne = value;
                        ne.setDurability((short) 0);
                        inv.addItem(ne);
                    } else {
                        inv.addItem(value);
                    }
                });
            } else if (rec instanceof ShapelessRecipe) {
                for (Object toArray : ((ShapelessRecipe) rec).getIngredientList().toArray()) {
                    ItemStack value = (ItemStack) toArray;
                    if ((ItemStack) toArray != null && value.getType() != Material.AIR) {
                        
                        if (value.getDurability() == 32767) {
                            ItemStack ne = value;
                            ne.setDurability((short) 0);
                            inv.addItem(ne);
                        } else {
                            inv.addItem(value);
                        }
                    }
                }
            }
        }

        if (book != null) {

            ItemStack bk = book.clone();
            if (item.getEnchantments().values().toArray().length > 0 && book.getType() == Material.BOOK) {
                bk.setType(Material.ENCHANTED_BOOK);
            }
            item.getEnchantments().entrySet().forEach((entry) -> {
                bk.addUnsafeEnchantment(entry.getKey(), entry.getValue());

            });
            inv.setItem(inv.firstEmpty(), bk);
        }

        inv.clear(19);

        if (book != null) {
            inv.setItem(19, book);
        }

        player.openInventory(inv);

    }

    /**
     *
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
