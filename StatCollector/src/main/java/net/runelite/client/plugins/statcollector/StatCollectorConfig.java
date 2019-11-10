package net.runelite.client.plugins.statcollector;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("statcollector")
public interface StatCollectorConfig extends Config
{
	@ConfigItem(
		keyName = "clientSecret",
		name = "AWS Client Secret",
		description = "The provided AWS client secret",
		position = 1
	)
	default String secret()
	{
		return "";
	}

	@ConfigItem(
		keyName = "clientId",
		name = "AWS Client Id",
		description = "The provided AWS client id",
		position = 2
	)
	default String id()
	{
		return "";
	}
}
