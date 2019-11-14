package net.runelite.client.plugins.statcollector.data;

import java.time.Instant;
import lombok.Data;

@Data
public class KeyPress
{
	private int keyCode;
	private Instant time;
}
