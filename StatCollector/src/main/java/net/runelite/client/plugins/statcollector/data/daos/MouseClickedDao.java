package net.runelite.client.plugins.statcollector.data.daos;

import net.runelite.client.plugins.statcollector.data.MouseClicked;
import net.runelite.client.plugins.statcollector.data.PlayerInfo;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;

public interface MouseClickedDao
{
	@SqlBatch("insert into MouseClick (time, username, ishuman, x, y, timeSinceLastClick) values (:mouseClick.time, :playerInfo.username, :playerInfo.isHuman, :mouseClick.x, :mouseClick.y, :mouseClick.timeSinceLastClick)")
	void bulkInsert(@BindBean("playerInfo") PlayerInfo playerInfo, @BindBean("mouseClick") MouseClicked... mouseClicks);
}
