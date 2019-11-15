package net.runelite.client.plugins.statcollector;

import com.google.common.collect.Lists;
import com.google.inject.Provides;
import java.time.Instant;
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

import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;


import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.SessionClose;
import net.runelite.client.game.XpDropEvent;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.statcollector.data.FocusChange;
import net.runelite.client.plugins.statcollector.data.PlayerInfo;
import net.runelite.client.plugins.statcollector.data.PlayerSession;
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

	private List<U> dataBuffer = Collections.synchronizedList(new ArrayList<>());

	private boolean collectData;

	@Getter
	private PlayerInfo playerInfo;

	@Getter
	private boolean isInFocus;

	@Getter
	private int isHuman; // 0 is human, 1 is bot

	@Getter
	private DatabaseManager databaseManager;

	private long loginMillis;

	@Getter
	private PlayerSession playerSession;

	@Provides
	StatCollectorConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StatCollectorConfig.class);
	}

	@Override
	protected void startUp()
	{
		collectData = false;
		isHuman = 0;
		eventBus.subscribe(FocusChanged.class, this, this::onFocusChanged);
		eventBus.subscribe(XpDropEvent.class, this, this::onXpDrop);
		eventBus.subscribe(ConfigChanged.class, this, this::onConfigChanged);
		eventBus.subscribe(GameStateChanged.class, this, this::onGameStateChanged);
		eventBus.subscribe(SessionClose.class, this, this::onSessionClose);
		mouseManager.registerMouseListener(mouseListener);
		keyManager.registerKeyListener(keyListener);
		System.out.println(statCollectorConfig.host());
		databaseManager = new DatabaseManager(statCollectorConfig.id(), statCollectorConfig.secret(), this, statCollectorConfig.host());
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			createPlayerSession();
			databaseManager.saveAll();
		}));
	}

	private <T> void onSessionClose(T t)
	{
		databaseManager.saveAll();
	}

	private void createPlayerSession()
	{
		PlayerSession playerSession = new PlayerSession();
		playerSession.setTime(Instant.now());
		playerSession.setSessionDuration(System.currentTimeMillis() - loginMillis);
		this.playerSession = playerSession;
	}

	private void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			loginMillis = System.currentTimeMillis();
			PlayerInfo playerInfo = new PlayerInfo();
			playerInfo.setUsername(String.valueOf(client.getUsername().hashCode()));
			playerInfo.setIsHuman(isHuman);

			this.playerInfo = playerInfo;
		}

		if (client.getGameState() == GameState.LOGIN_SCREEN && playerInfo != null)
		{
			createPlayerSession();
			databaseManager.saveAll();
			playerInfo = null;
		}
	}

	private void onConfigChanged(ConfigChanged event)
	{
		if (!"statcollector".equals(event.getGroup()))
		{
			return;
		}
		databaseManager = new DatabaseManager(statCollectorConfig.id(), statCollectorConfig.secret(), this, statCollectorConfig.host());
	}

	private void onXpDrop(XpDropEvent event)
	{
		if (client.getGameState() == GameState.LOGGED_IN && event.getExp() != 0)
		{
			PlayerXp playerXp = new PlayerXp();
			playerXp.setXpGainedAmount(event.getExp());
			playerXp.setSkill(event.getSkill().getName());
			playerXp.setTime(Instant.now());

			databaseManager.append(playerXp);
		}
	}

	private void onFocusChanged(FocusChanged event)
	{
		isInFocus = event.isFocused();
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			FocusChange focusChange = new FocusChange();
			focusChange.setTime(Instant.now());
			focusChange.setFocus(event.isFocused() ? 1 : 0);

			databaseManager.append(focusChange);
		}
	}

	@Override
	protected void shutDown()
	{
		databaseManager.saveAll();
		eventBus.unregister(this);
		mouseManager.unregisterMouseListener(mouseListener);
	}
}
