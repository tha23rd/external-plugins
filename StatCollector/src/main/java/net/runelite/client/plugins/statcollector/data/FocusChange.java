package net.runelite.client.plugins.statcollector.data;

import java.time.Instant;
import lombok.Data;

@Data
public class FocusChange
{
	int focus; // 0 = loss of focus, 1 = gained focus

	private Instant time;
}
