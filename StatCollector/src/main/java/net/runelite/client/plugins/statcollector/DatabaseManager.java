package net.runelite.client.plugins.statcollector;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.runelite.client.plugins.statcollector.data.FocusChange;
import net.runelite.client.plugins.statcollector.data.KeyPress;
import net.runelite.client.plugins.statcollector.data.MouseClicked;
import net.runelite.client.plugins.statcollector.data.MouseHover;
import net.runelite.client.plugins.statcollector.data.PlayerInfo;
import net.runelite.client.plugins.statcollector.data.PlayerXp;
import net.runelite.client.plugins.statcollector.data.daos.FocusChangeDao;
import net.runelite.client.plugins.statcollector.data.daos.KeyPressDao;
import net.runelite.client.plugins.statcollector.data.daos.MouseClickedDao;
import net.runelite.client.plugins.statcollector.data.daos.MouseHoversDao;
import net.runelite.client.plugins.statcollector.data.daos.PlayerXpDao;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class DatabaseManager
{

	private Jdbi jdbi;

	private List<MouseClicked> mouseClicks = Collections.synchronizedList(new ArrayList<>());
	private List<MouseHover> mouseHovers = Collections.synchronizedList(new ArrayList<>());
	private List<KeyPress> keyPresses = Collections.synchronizedList(new ArrayList<>());
	private List<FocusChange> focusChanges = Collections.synchronizedList(new ArrayList<>());
	private List<PlayerXp> playerXps = Collections.synchronizedList(new ArrayList<>());

	private MouseHoversDao mouseHoversDao;
	private PlayerXpDao playerXpDao;
	private MouseClickedDao mouseClickedDao;
	private FocusChangeDao focusChangeDao;
	private KeyPressDao keyPressDao;

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(50);
	private ThreadPoolExecutor executorService = new ThreadPoolExecutor(10, 100, 10, TimeUnit.SECONDS, queue);

	private StatCollectorPlugin statCollectorPlugin;

	private String dbURL2 = "jdbc:postgresql://ec2-52-91-233-236.compute-1.amazonaws.com/rs_stats";
	private String username;
	private String password;

	public DatabaseManager(String user, String password, StatCollectorPlugin statCollectorPlugin)
	{
		try
		{
			Class.forName("org.postgresql.Driver");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		this.username = user;
		this.password = password;
		jdbi = Jdbi.create(dbURL2, user, password);
		jdbi.installPlugin(new SqlObjectPlugin());
		mouseHoversDao = jdbi.onDemand(MouseHoversDao.class);
		playerXpDao = jdbi.onDemand(PlayerXpDao.class);
		mouseClickedDao = jdbi.onDemand(MouseClickedDao.class);
		focusChangeDao = jdbi.onDemand(FocusChangeDao.class);
		keyPressDao = jdbi.onDemand(KeyPressDao.class);

		this.statCollectorPlugin = statCollectorPlugin;
	}

	public void saveAll()
	{
		System.out.println("Saving all");
		mouseClickedDao.bulkInsert(statCollectorPlugin.getPlayerInfo(), mouseClicks.toArray(new MouseClicked[0]));
		focusChangeDao.bulkInsert(statCollectorPlugin.getPlayerInfo(), focusChanges.toArray(new FocusChange[0]));
		keyPressDao.bulkInsert(statCollectorPlugin.getPlayerInfo(), keyPresses.toArray(new KeyPress[0]));
		playerXpDao.bulkInsert(statCollectorPlugin.getPlayerInfo(), playerXps.toArray(new PlayerXp[0]));
		mouseHoversDao.bulkInsert(statCollectorPlugin.getPlayerInfo(), mouseHovers.toArray(new MouseHover[0]));
		mouseClicks.clear();
		focusChanges.clear();
		keyPresses.clear();
		playerXps.clear();
		mouseHovers.clear();
	}

	public void append(MouseClicked mouseClicked)
	{
		mouseClicks.add(mouseClicked);
		if(mouseClicks.size() >= 100)
		{
			System.out.println("About to insert mouseClicks");
			List<MouseClicked> mouseClicksClone = new ArrayList<>(mouseClicks);
			mouseClicks.clear();
			executorService.submit(() -> mouseClickedDao.bulkInsert(statCollectorPlugin.getPlayerInfo(), mouseClicksClone.toArray(new MouseClicked[0])));
		}
	}

	public void append(FocusChange focusChange)
	{
		focusChanges.add(focusChange);
		if(focusChanges.size() >= 10)
		{
			System.out.println("About to insert focusChanges");
			List<FocusChange> focusChangesClone = new ArrayList<>(focusChanges);
			focusChanges.clear();
			executorService.submit(() -> focusChangeDao.bulkInsert(statCollectorPlugin.getPlayerInfo(), focusChangesClone.toArray(new FocusChange[0])));
		}
	}

	public void append(KeyPress keyPress)
	{
		keyPresses.add(keyPress);
		if(keyPresses.size() >= 100)
		{
			System.out.println("About to insert keyPresses");
			List<KeyPress> keyPressesClone = new ArrayList<>(keyPresses);
			keyPresses.clear();
			executorService.submit(() -> keyPressDao.bulkInsert(statCollectorPlugin.getPlayerInfo(), keyPressesClone.toArray(new KeyPress[0])));
		}
	}

	public void append(PlayerXp playerXp)
	{
		playerXps.add(playerXp);
		if(playerXps.size() >= 50)
		{
			System.out.println("About to insert playerXps");
			List<PlayerXp> playerXpClone = new ArrayList<>(playerXps);
			playerXps.clear();
			executorService.submit(() -> playerXpDao.bulkInsert(statCollectorPlugin.getPlayerInfo(), playerXpClone.toArray(new PlayerXp[0])));
		}
	}

	public void append(MouseHover mouseHover)
	{
		mouseHovers.add(mouseHover);
		if (mouseHovers.size() >= 250)
		{
			System.out.println("About to insert mouseHovers");
			List<MouseHover> mouseHoversClone = new ArrayList<>(mouseHovers);
			mouseHovers.clear();
			executorService.submit(() -> mouseHoversDao.bulkInsert(statCollectorPlugin.getPlayerInfo(), mouseHoversClone.toArray(new MouseHover[0])));
		}
	}

	private void testDaos()
	{
		MouseHoversDao mouseHoversDao = jdbi.onDemand(MouseHoversDao.class);
		PlayerXpDao playerXpDao = jdbi.onDemand(PlayerXpDao.class);
		MouseClickedDao mouseClickedDao = jdbi.onDemand(MouseClickedDao.class);
		FocusChangeDao focusChangeDao = jdbi.onDemand(FocusChangeDao.class);
		KeyPressDao keyPressDao = jdbi.onDemand(KeyPressDao.class);
		MouseHover m = new MouseHover();
		m.setTime(Instant.now());
		m.setY(234);
		m.setX(345);

		PlayerInfo playerInfo = new PlayerInfo();
		playerInfo.setIsHuman(0);
		playerInfo.setUsername("that23rdddd");

		mouseHoversDao.bulkInsert(playerInfo, m);

		PlayerXp p = new PlayerXp();
		p.setTime(Instant.now());
		p.setXpGainedAmount(43);
		p.setSkill("Hunter");

		playerXpDao.bulkInsert(playerInfo, p);

		MouseClicked mouseClicked = new MouseClicked();
		mouseClicked.setTimeSinceLastClick(3244);
		mouseClicked.setY(34);
		mouseClicked.setX(454);
		mouseClicked.setTime(Instant.now());

		mouseClickedDao.bulkInsert(playerInfo, mouseClicked);

		FocusChange focusChange = new FocusChange();
		focusChange.setFocus(0);
		focusChange.setTime(Instant.now());

		focusChangeDao.bulkInsert(playerInfo, focusChange);

		KeyPress keyPress = new KeyPress();
		keyPress.setKeyCode(4);
		keyPress.setTime(Instant.now());

		keyPressDao.bulkInsert(playerInfo, keyPress);
	}

}
