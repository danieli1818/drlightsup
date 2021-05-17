package drlightsup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import drlightsup.management.LightsManager;

public class DRLightsUpCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Error! Only players can run this command!");
			return false;
		}
		Player player = (Player)sender;
		boolean currentLightMode = LightsManager.getInstance().toggleLightMode(player.getUniqueId());
		player.sendMessage("Successfully toggled light mode to: " + currentLightMode);
		return true;
	}

}
