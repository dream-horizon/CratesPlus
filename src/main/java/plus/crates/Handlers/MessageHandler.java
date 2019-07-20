package plus.crates.Handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import plus.crates.Crate;
import plus.crates.CratesPlus;
import plus.crates.Winning;

public class MessageHandler {
	private final CratesPlus cratesPlus;

	public MessageHandler(CratesPlus cratesPlus) {
		this.cratesPlus = cratesPlus;
	}

	public String getMessage(String messageName, Player player, Crate crate, Winning winning) {
		if (!cratesPlus.getMessagesConfig().isSet(messageName)) {
			return "Message \"" + messageName + "\" not configured";
		}
		String message = cratesPlus.getMessagesConfig().getString(messageName);
		message = doPlaceholders(message, player, crate, winning);
		message = ChatColor.translateAlternateColorCodes('&', message);
		if (isAprilFools()) {
			message = ChatColor.LIGHT_PURPLE + ChatColor.stripColor(message);
		}
		return message;
	}

	public String doPlaceholders(String message, Player player, Crate crate, Winning winning) {
		message = ChatColor.translateAlternateColorCodes('&', message);
		if (player != null) {
			message = message.replaceAll("%name%", player.getName())
					.replaceAll("%displayname%", player.getDisplayName())
					.replaceAll("%uuid%", player.getUniqueId().toString());
		}
		if (crate != null) {
			message = message.replaceAll("%crate%", crate.getName(true) + ChatColor.RESET);
		}
		if (winning != null) {
			ItemStack winItem = winning.getWinningItemStack();
			String winningItemName = winItem.getType().toString();
			if (winItem.hasItemMeta() && winItem.getItemMeta().hasDisplayName()) {
				winningItemName = winItem.getItemMeta().getDisplayName();
			}

			message = message
					.replace("%prize%", winningItemName + ChatColor.RESET)
					.replace("%winning%", winningItemName + ChatColor.RESET)
					.replace("%percentage%", String.valueOf(winning.getPercentage()));
		}
		return message;
	}

	public boolean isAprilFools() {
		DateFormat df = new SimpleDateFormat("dd/MM");
		Date dateobj = new Date();
		return df.format(dateobj).equals("01/04");
	}

}
