package net.runelite.client.plugins.statcollector;

import java.awt.event.KeyEvent;
import java.time.Instant;
import net.runelite.api.GameState;
import net.runelite.client.input.KeyListener;
import net.runelite.client.plugins.statcollector.data.KeyPress;

public class StatKeyListener implements KeyListener
{

	private StatCollectorPlugin statCollectorPlugin;
	private Instant lastKeyPress;

	public StatKeyListener(StatCollectorPlugin statCollectorPlugin)
	{
		lastKeyPress = Instant.now();
		this.statCollectorPlugin = statCollectorPlugin;
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (statCollectorPlugin.getClient().getGameState() == GameState.LOGGED_IN && statCollectorPlugin.isInFocus())
		{
			if (Instant.now().toEpochMilli() - lastKeyPress.toEpochMilli() >= 20)
			{
				Instant now = Instant.now();
				lastKeyPress = now;
				KeyPress keyPress = new KeyPress();
				keyPress.setTime(now);
				keyPress.setKeyCode(e.getKeyCode());

				statCollectorPlugin.getDatabaseManager().append(keyPress);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}
}
