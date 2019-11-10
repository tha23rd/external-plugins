package net.runelite.client.plugins.statcollector.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "MouseClicks")
public class MouseClicked extends AbstractData
{
	@DynamoDBAttribute(attributeName = "timeSinceLastClick")
	private long timeSinceLastClick;
	@DynamoDBAttribute(attributeName = "x")
	private int x;
	@DynamoDBAttribute(attributeName = "y")
	private int y;
	@DynamoDBAttribute(attributeName = "isLeft")
	private int isLeft;
}
