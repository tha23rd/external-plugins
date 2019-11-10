package net.runelite.client.plugins.statcollector.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "KeyPresses")
public class KeyPress extends AbstractData
{
	@DynamoDBAttribute(attributeName = "keyCode")
	private int keyCode;
}
