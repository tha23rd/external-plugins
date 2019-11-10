package net.runelite.client.plugins.statcollector;

import java.awt.event.KeyEvent;
import net.runelite.api.GameState;
import net.runelite.client.input.KeyListener;
import net.runelite.client.plugins.statcollector.data.KeyPress;

public class StatKeyListener implements KeyListener
{

	private StatCollectorPlugin statCollectorPlugin;

	public StatKeyListener(StatCollectorPlugin statCollectorPlugin)
	{
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
			KeyPress keyPress = new KeyPress();
			keyPress.setIsHuman(statCollectorPlugin.getIsHuman());
			keyPress.setTimestamp(System.currentTimeMillis());
			keyPress.setUsername(String.valueOf(statCollectorPlugin.getClient().getUsername().hashCode()));
			keyPress.setKeyCode(e.getKeyCode());

			statCollectorPlugin.appendToDataBuffer(keyPress);
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}
}
