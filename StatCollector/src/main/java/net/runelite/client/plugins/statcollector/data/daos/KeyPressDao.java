package net.runelite.client.plugins.statcollector.data.daos;

import net.runelite.client.plugins.statcollector.data.KeyPress;
import net.runelite.client.plugins.statcollector.data.PlayerInfo;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;

public interface KeyPressDao
{
	@SqlBatch("insert into keypress (time, username, ishuman, keyCode) values (:keyPress.time, :playerInfo.username, :playerInfo.isHuman, :keyPress.keyCode)")
	void bulkInsert(@BindBean("playerInfo") PlayerInfo playerInfo, @BindBean("keyPress") KeyPress... keyPresses);
}
