package net.runelite.client.plugins.statcollector.data.daos;

import net.runelite.client.plugins.statcollector.data.PlayerInfo;
import net.runelite.client.plugins.statcollector.data.PlayerXp;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;

public interface PlayerXpDao
{
	@SqlBatch("insert into playerxp (time, username, ishuman, skill, xpGainedAmount) values (:playerXp.time, :playerInfo.username, :playerInfo.isHuman, :playerXp.skill, :playerXp.xpGainedAmount)")
	void bulkInsert(@BindBean("playerInfo") PlayerInfo playerInfo, @BindBean("playerXp") PlayerXp... playerXps);
}
