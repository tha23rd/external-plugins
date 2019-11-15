package net.runelite.client.plugins.statcollector.data.daos;

import net.runelite.client.plugins.statcollector.data.PlayerInfo;
import net.runelite.client.plugins.statcollector.data.PlayerSession;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;

public interface PlayerSessionDao
{
	@SqlBatch("insert into playersession (time, username, ishuman, sessionduration) values (:playerSession.time, :playerInfo.username, :playerInfo.isHuman, :playerSession.sessionDuration)")
	void bulkInsert(@BindBean("playerInfo") PlayerInfo playerInfo, @BindBean("playerSession") PlayerSession... playerSessions);
}
