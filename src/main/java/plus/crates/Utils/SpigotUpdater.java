package plus.crates.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.plugin.java.JavaPlugin;

public class SpigotUpdater {
	private String version;
	private String oldVersion;
	private SpigotUpdater.UpdateResult result = SpigotUpdater.UpdateResult.DISABLED;
	private HttpURLConnection connection;

	public enum UpdateResult {
		NO_UPDATE,
		DISABLED,
		FAIL_SPIGOT,
		SPIGOT_UPDATE_AVAILABLE,
		MAJOR_SPIGOT_UPDATE_AVAILABLE
	}

	public SpigotUpdater(JavaPlugin plugin) {
		String RESOURCE_ID = "5018";
		oldVersion = plugin.getDescription().getVersion().replaceAll("-SNAPSHOT-", ".");
		try {
			connection = (HttpURLConnection) new URL(
					"https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID).openConnection();
		} catch (IOException e) {
			result = UpdateResult.FAIL_SPIGOT;
			return;
		}

		runSpigot();
	}

	private void runSpigot() {
		connection.setDoOutput(true);
		String version;
		try {
			version = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
		} catch (Exception e) {
			result = UpdateResult.FAIL_SPIGOT;
			return;
		}
		if (version != null && version.length() <= 7) {
			this.version = version.replace("[^A-Za-z]", "").replace("|", "");
			spigotCheckUpdate();
			return;
		}
		result = UpdateResult.FAIL_SPIGOT;
	}

	private void spigotCheckUpdate() {
		Integer oldVersion = Integer.parseInt(this.oldVersion.replace(".", ""));
		Integer currentVersion = Integer.parseInt(version.replace(".", ""));
		if (oldVersion < currentVersion) {
			String[] localParts = this.oldVersion.split("\\.");
			String[] remoteParts = version.split("\\.");
			if (Integer.parseInt(localParts[0]) < Integer.parseInt(remoteParts[0])) {
				result = UpdateResult.MAJOR_SPIGOT_UPDATE_AVAILABLE;
			} else {
				result = UpdateResult.SPIGOT_UPDATE_AVAILABLE;
			}
		} else {
			result = UpdateResult.NO_UPDATE;
		}
	}

	public UpdateResult getResult() {
		return result;
	}

	public String getVersion() {
		return version;
	}

}