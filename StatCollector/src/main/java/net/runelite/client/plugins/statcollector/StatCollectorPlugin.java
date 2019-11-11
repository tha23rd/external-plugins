package net.runelite.client.plugins.statcollector;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.FocusChanged;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.XpDropEvent;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.statcollector.data.FocusChange;
import net.runelite.client.plugins.statcollector.data.PlayerXp;

@PluginDescriptor(
	name = "StatCollector",
	description = "",
	tags = {"Stat"},
	type = PluginType.UTILITY,
	enabledByDefault = false
)
@Singleton
@Slf4j
public class StatCollectorPlugin<U> extends Plugin
{

	@Inject
	@Getter
	private Client client;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private StatCollectorConfig statCollectorConfig;

	private StatMouseListener mouseListener = new StatMouseListener(this);

	private StatKeyListener keyListener = new StatKeyListener(this);

	@Getter
	private DynamoLib dynamoLib;

	private List<U> dataBuffer = Collections.synchronizedList(new ArrayList<>());

	private boolean collectData;

	@Getter
	private boolean isInFocus;

	@Getter
	private int isHuman; // 0 is human, 1 is bot

	@Provides
	StatCollectorConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StatCollectorConfig.class);
	}

	@Override
	protected void startUp()
	{
		collectData = true;
		isHuman = 0;
		dynamoLib = new DynamoLib(statCollectorConfig.id(), statCollectorConfig.secret());
		eventBus.subscribe(FocusChanged.class, this, this::onFocusChanged);
		eventBus.subscribe(XpDropEvent.class, this, this::onXpDrop);
		eventBus.subscribe(ConfigChanged.class, this, this::onConfigChanged);
		mouseManager.registerMouseListener(mouseListener);
		keyManager.registerKeyListener(keyListener);
	}

	private <T> void onConfigChanged(T t)
	{
		dynamoLib = new DynamoLib(statCollectorConfig.id(), statCollectorConfig.secret());
	}

	private void onXpDrop(XpDropEvent event)
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			PlayerXp playerXp = new PlayerXp();
			playerXp.setXpGainedAmount(event.getExp());
			playerXp.setSkill(event.getSkill().getName());
			playerXp.setTimestamp(System.currentTimeMillis());
			playerXp.setUsername(String.valueOf(client.getUsername().hashCode()));
			playerXp.setIsHuman(isHuman);

			appendToDataBuffer((U) playerXp);
		}
	}

	private void onFocusChanged(FocusChanged event)
	{
		isInFocus = event.isFocused();
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			FocusChange focusChange = new FocusChange();
			focusChange.setTimestamp(System.currentTimeMillis());
			focusChange.setUsername(String.valueOf(client.getUsername().hashCode()));
			focusChange.setFocus(event.isFocused() ? 1 : 0);
			focusChange.setIsHuman(isHuman);

			appendToDataBuffer((U) focusChange);
		}
	}

	@Override
	protected void shutDown()
	{
		eventBus.unregister(this);
		mouseManager.unregisterMouseListener(mouseListener);
	}

	public void appendToDataBuffer(U dataItem)
	{
		dataBuffer.add(dataItem);
		System.out.println("New item added to buffer, size: " + dataBuffer.size());
		if (dataBuffer.size() == 25)
		{
			List<U> dataBufferClone = new ArrayList<>(dataBuffer);
			if (collectData)
				dynamoLib.batchWrite(dataBufferClone);
			dataBuffer.clear();
		}
	}

}
