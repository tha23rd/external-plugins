package net.runelite.client.plugins.statcollector.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "MouseHovers")
public class MouseHover extends AbstractData
{
	@DynamoDBAttribute(attributeName = "x")
	private int x;
	@DynamoDBAttribute(attributeName = "y")
	private int y;
}
