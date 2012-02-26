package com.blockmovers.plugins.donationvault;

import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Donationvault extends JavaPlugin implements Listener {

    static final Logger log = Logger.getLogger("Minecraft"); //set up our logger
    public static Economy economy = null;

    public void onDisable() {
        PluginDescriptionFile pdffile = this.getDescription();

        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is disabled.");
    }

    public void onEnable() {
        PluginDescriptionFile pdffile = this.getDescription();
        if (!setupEconomy()) {
            log.info(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is enabled.");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        //String playerCount = Integer.toString(playerCount());
        //String message = replaceText(uniqueCountString, "", playerCount);
        //cs.sendMessage();
        if (args.length == 2) {
            if (cs instanceof Player) {
                if (!cs.hasPermission("donationvault.admin")) {
                    cs.sendMessage(ChatColor.RED + "You don't have permssion to do that.");
                    return false;
                }
            }
            if (getServer().getPlayer(args[0]) != null) {
                Player player = getServer().getPlayer(args[0]);
                if (player instanceof Player) {
                    //cs.sendMessage("giving " + player.getName() + " " + donateToEcon(Double.valueOf(args[1])));
                    try {
                        Double.valueOf(args[1]);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    EconomyResponse r = economy.depositPlayer(player.getName(), donateToEcon(Double.valueOf(args[1])));
                    if (r.transactionSuccess()) {
                        return true;
                    } else {
                        cs.sendMessage(ChatColor.RED + "Error occured crediting to account.");
                        return false;
                    }
                }
            } else {
                cs.sendMessage(ChatColor.RED + "Player not online.");
                return false;
            }
        } else if (args.length == 1) {
            if (cs instanceof Player) {
                if (!cs.hasPermission("donationvault.check")) {
                    cs.sendMessage(ChatColor.RED + "You don't have permssion to do that.");
                    return false;
                }
            }
            
            try {
                Double.valueOf(args[0]);
            } catch (NumberFormatException e) {
                return false;
            }
            cs.sendMessage("Donating $" + args[0] + " would give you $" + donateToEcon(Double.valueOf(args[1])) + " in-game.");
        }
        return false;
    }

    private Double donateToEcon(Double amount) {
        Double amt = 0.0;
        if (amount >= 0 && amount <= 5) {
            amt = (amount * 150);
        } else if (amount > 5 && amount <= 10) {
            amt = (amount * 200);
        } else if (amount > 10 && amount <= 30) {
            amt = (amount * 300);
        } else if (amount > 30 && amount <= 50) {
            amt = (amount * 400);
        } else {
            amt = (amount * 500);
        }


        return amt;

    }

    private boolean setupEconomy() {
        {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

            if (economyProvider
                    != null) {
                economy = economyProvider.getProvider();
            }

            return (economy
                    != null);
        }
    }
}
