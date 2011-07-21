package me.desht.chesscraft;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;

public class TerrainBackup {
	private static String schematicDir = "schematics";
	private ChessCraft plugin;
	private Player player;
	private WorldEditPlugin wep;
	private WorldEdit we;
	private LocalSession localSession;
	private EditSession editSession;
	private LocalPlayer localPlayer;
	private CuboidClipboard clipboard;
	private File saveFile;
	private Vector min, max;

	TerrainBackup(ChessCraft plugin, Player player, BoardView view) /*throws FilenameException*/ {
		this.plugin = plugin;
		this.player = player;

		wep = plugin.getWorldEdit();
		if (wep == null)
			return;
		we = wep.getWorldEdit();

		Cuboid bounds = view.getOuterBounds();
		Location l1 = bounds.getUpperSW();
		Location l2 = bounds.getLowerNE();
		max = new Vector(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ());
		min = new Vector(l2.getBlockX(), l2.getBlockY(), l2.getBlockZ());

		localPlayer = wep.wrapPlayer(player);
		localSession = we.getSession(localPlayer);
		editSession = localSession.createEditSession(localPlayer);

		File dir = new File(plugin.getDataFolder(), schematicDir);
                
                try {
                    saveFile = we.getSafeSaveFile(localPlayer, dir, view.getName(), "schematic", new String[]{"schematic"});
                } catch (FilenameException ex) {
                    plugin.log(Level.WARNING, ex.getMessage());
                }
	}

	void saveTerrain() {
		if (wep == null)
			return;

		try {
			editSession.enableQueue();
			clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
			clipboard.copy(editSession);
			clipboard.saveSchematic(saveFile);
			editSession.flushQueue();
		} catch (DataException e) {
			plugin.errorMessage(player, "Terrain backup could not be written: " + e.getMessage());
		} catch (IOException e) {
			plugin.errorMessage(player, "Terrain backup could not be written: " + e.getMessage());
		}
	}

	void reloadTerrain() {
		if (wep == null)
			return;

		try {
			editSession.enableQueue();
			localSession.setClipboard(CuboidClipboard.loadSchematic(saveFile));
			Vector pos = localSession.getClipboard().getOrigin();
			localSession.getClipboard().place(editSession, pos, false);
			editSession.flushQueue();
			we.flushBlockBag(localPlayer, editSession);
			if (!saveFile.delete())
				plugin.log(Level.WARNING, "Could not delete " + saveFile);
		} catch (DataException e) {
			plugin.errorMessage(player, "Terrain backup could not be restored: " + e.getMessage());
		} catch (IOException e) {
			plugin.errorMessage(player, "Terrain backup could not be restored: " + e.getMessage());
		} catch (EmptyClipboardException e) {
			plugin.errorMessage(player, "Terrain backup could not be restored: " + e.getMessage());
		} catch (MaxChangedBlocksException e) {
			plugin.errorMessage(player, "Terrain backup could not be restored: " + e.getMessage());
		}
	}

}
