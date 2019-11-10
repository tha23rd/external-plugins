package net.runelite.client.plugins.statcollector.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "FocusChanges")
public class FocusChange extends AbstractData
{
	@DynamoDBAttribute(attributeName = "focus")
	int focus; // 0 = loss of focus, 1 = gained focus
}
