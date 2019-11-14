package net.runelite.client.plugins.statcollector;

import java.awt.event.MouseEvent;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.SwingUtilities;
import net.runelite.api.GameState;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.plugins.statcollector.data.MouseClicked;
import net.runelite.client.plugins.statcollector.data.MouseHover;

public class StatMouseListener extends MouseAdapter
{

	private StatCollectorPlugin statCollectorPlugin;
	private Instant lastTimeMouseHoverRecorded;
	private long lastTimeMouseClicked;

	public StatMouseListener(StatCollectorPlugin statCollectorPlugin)
	{
		this.statCollectorPlugin = statCollectorPlugin;
		lastTimeMouseClicked = 0;
		lastTimeMouseHoverRecorded = Instant.now();
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
			mouseClicked.setTime(Instant.now());
			mouseClicked.setX(x);
			mouseClicked.setY(y);
			mouseClicked.setTimeSinceLastClick(timeClicked - lastTimeMouseClicked);

			lastTimeMouseClicked = timeClicked;

			statCollectorPlugin.getDatabaseManager().append(mouseClicked);

		}
		return event;
	}

	@Override
	public MouseEvent mouseMoved(final MouseEvent event)
	{
		if (Instant.now().toEpochMilli() - lastTimeMouseHoverRecorded.toEpochMilli() >= 20 && statCollectorPlugin.getClient().getGameState() == GameState.LOGGED_IN)
		{
			Instant now = Instant.now();
			lastTimeMouseHoverRecorded = now;
			MouseHover mouseHover = new MouseHover();
			mouseHover.setTime(now);
			int x = statCollectorPlugin.getClient().getMouseCanvasPosition().getX();
			int y = statCollectorPlugin.getClient().getMouseCanvasPosition().getY();
			mouseHover.setX(x);
			mouseHover.setY(y);

			statCollectorPlugin.getDatabaseManager().append(mouseHover);

		}
		return event;
	}
}
