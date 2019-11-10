package net.runelite.client.plugins.statcollector.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@DynamoDBTable(tableName = "PlayerXp")
@Data
public class PlayerXp extends AbstractData
{
	@DynamoDBAttribute(attributeName = "skill")
	private String skill;
	@DynamoDBAttribute(attributeName = "xpGainedAmount")
	private int xpGainedAmount;
}
