package me.desht.chesscraft.commands;

import me.desht.chesscraft.chess.ChessGame;
import me.desht.chesscraft.chess.ChessGameManager;
import me.desht.chesscraft.exceptions.ChessException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class OfferDrawCommand extends ChessAbstractCommand {

	public OfferDrawCommand() {
		super("chess offer draw", 0, 1);
		setPermissionNode("chesscraft.commands.offer.draw");
		setUsage("/chess offer draw");
	}

	@Override
	public boolean execute(Plugin plugin, CommandSender player, String[] args) throws ChessException {
		notFromConsole(player);

		ChessGame game = ChessGameManager.getManager().getCurrentGame(player.getName(), true);
		game.offerDraw(player.getName());

		return true;
	}

}

