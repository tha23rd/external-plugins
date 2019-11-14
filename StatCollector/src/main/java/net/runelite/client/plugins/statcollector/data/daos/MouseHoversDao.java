package net.runelite.client.plugins.statcollector.data.daos;

import net.runelite.client.plugins.statcollector.data.MouseHover;
import net.runelite.client.plugins.statcollector.data.PlayerInfo;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;

public interface MouseHoversDao
{
	@SqlBatch("insert into MouseHovers (time, username, ishuman, x, y) values (:mouseHover.time, :playerInfo.username, :playerInfo.isHuman, :mouseHover.x, :mouseHover.y)")
	void bulkInsert(@BindBean("playerInfo") PlayerInfo playerInfo, @BindBean("mouseHover") MouseHover... mouseHovers);
}
