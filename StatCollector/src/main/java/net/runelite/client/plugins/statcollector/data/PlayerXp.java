package net.runelite.client.plugins.statcollector.data;

import java.time.Instant;
import lombok.Data;

@Data
public class PlayerXp
{
	private String skill;

	private int xpGainedAmount;

	private Instant time;
}
