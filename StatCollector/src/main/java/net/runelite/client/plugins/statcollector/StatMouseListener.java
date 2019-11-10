package net.runelite.client.plugins.statcollector;

import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import net.runelite.api.GameState;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.plugins.statcollector.data.MouseClicked;
import net.runelite.client.plugins.statcollector.data.MouseHover;

public class StatMouseListener extends MouseAdapter
{

	private StatCollectorPlugin statCollectorPlugin;
	private long lastTimeMouseHoverRecorded;
	private long lastTimeMouseClicked;

	public StatMouseListener(StatCollectorPlugin statCollectorPlugin)
	{
		this.statCollectorPlugin = statCollectorPlugin;
		lastTimeMouseClicked = 0;
		lastTimeMouseHoverRecorded = System.currentTimeMillis();
	}

	@Override
	public MouseEvent mousePressed(final MouseEvent event)
	{
		if (statCollectorPlugin.getClient().getGameState() == GameState.LOGGED_IN)
		{
			if (lastTimeMouseClicked == 0)
			{
				// discard the very first click to initialize this value
				lastTimeMouseClicked = System.currentTimeMillis();
				return event;
			}

			long timeClicked = System.currentTimeMillis();

			// record

			int x = statCollectorPlugin.getClient().getMouseCanvasPosition().getX();
			int y = statCollectorPlugin.getClient().getMouseCanvasPosition().getY();

			MouseClicked mouseClicked = new MouseClicked();
			mouseClicked.setTimestamp(timeClicked);
			mouseClicked.setUsername(String.valueOf(statCollectorPlugin.getClient().getUsername().hashCode()));
			mouseClicked.setX(x);
			mouseClicked.setY(y);
			mouseClicked.setTimeSinceLastClick(timeClicked - lastTimeMouseClicked);
			mouseClicked.setIsHuman(statCollectorPlugin.getIsHuman());
			mouseClicked.setIsLeft(SwingUtilities.isLeftMouseButton(event) ? 0 : 1);
			lastTimeMouseClicked = timeClicked;

			statCollectorPlugin.appendToDataBuffer(mouseClicked);

		}
		return event;
	}

	@Override
	public MouseEvent mouseMoved(final MouseEvent event)
	{
		if (System.currentTimeMillis() - lastTimeMouseHoverRecorded >= 20 && statCollectorPlugin.getClient().getGameState() == GameState.LOGGED_IN)
		{
			long timeClicked = System.currentTimeMillis();
			int x = statCollectorPlugin.getClient().getMouseCanvasPosition().getX();
			int y = statCollectorPlugin.getClient().getMouseCanvasPosition().getY();
			MouseHover mouseHover = new MouseHover();
			mouseHover.setTimestamp(timeClicked);
			mouseHover.setUsername(String.valueOf(statCollectorPlugin.getClient().getUsername().hashCode()));
			mouseHover.setX(x);
			mouseHover.setY(y);
			mouseHover.setIsHuman(statCollectorPlugin.getIsHuman());
			lastTimeMouseHoverRecorded = timeClicked;
			statCollectorPlugin.appendToDataBuffer(mouseHover);
		}
		return event;
	}
}
