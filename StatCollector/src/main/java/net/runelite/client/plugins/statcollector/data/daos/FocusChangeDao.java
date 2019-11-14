package net.runelite.client.plugins.statcollector.data.daos;

import net.runelite.client.plugins.statcollector.data.FocusChange;
import net.runelite.client.plugins.statcollector.data.PlayerInfo;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;

public interface FocusChangeDao
{
	@SqlBatch("insert into focuschange (time, username, ishuman, focus) values (:focusChange.time, :playerInfo.username, :playerInfo.isHuman, :focusChange.focus)")
	void bulkInsert(@BindBean("playerInfo") PlayerInfo playerInfo, @BindBean("focusChange") FocusChange... focusChanges);
}
