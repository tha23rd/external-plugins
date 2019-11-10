package net.runelite.client.plugins.statcollector.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import lombok.Data;

@Data
@DynamoDBDocument
public abstract class AbstractData
{
	@DynamoDBRangeKey(attributeName = "username")
	private String username;
	@DynamoDBHashKey(attributeName = "timestamp")
	private long timestamp;
	@DynamoDBAttribute(attributeName = "isHuman")
	private int isHuman;
}
