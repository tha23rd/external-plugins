package net.runelite.client.plugins.statcollector.data;

import java.time.Instant;
import lombok.Data;

@Data
public class PlayerSession
{
	private long sessionDuration;

	private Instant time;
}
