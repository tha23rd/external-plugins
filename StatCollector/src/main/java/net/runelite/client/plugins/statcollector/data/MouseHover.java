package net.runelite.client.plugins.statcollector.data;

import java.time.Instant;
import lombok.Data;

@Data
public class MouseHover
{
	private int x;

	private int y;

	private Instant time;
}
