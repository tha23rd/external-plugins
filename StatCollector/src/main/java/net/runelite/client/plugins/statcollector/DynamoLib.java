package net.runelite.client.plugins.statcollector;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DynamoLib
{

	private static final int MAX_RETRY = 10;
	private static final int MAX_EXPONENTIAL_BACKOFF_TIME = 6000 * 3;

	private BasicAWSCredentials awsCreds;
	private AmazonDynamoDB client;
	private DynamoDBMapper mapper;

	private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(50);
	private ThreadPoolExecutor executorService = new ThreadPoolExecutor(50, 100, 10, TimeUnit.SECONDS, queue);

	private SentryClient sentry;

	public DynamoLib(String clientSecret, String clientId)
	{
		sentry = SentryClientFactory.sentryClient("https://cd27258d484d4bb28ab972f92ebffa6a@sentry.io/1815860");
		awsCreds = new BasicAWSCredentials(clientId, clientSecret);
		this.client = AmazonDynamoDBClientBuilder
			.standard().withRegion("us-east-1")
			.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
			.build();
		mapper = new DynamoDBMapper(client);
	}

	public <U> void batchWrite(List<U> data, String username)
	{
		if (data.size() > 25)
		{
			throw new RuntimeException("List size is too large!");
		}

		executorService.submit(() ->
		{
			Context context = sentry.getContext();
			// Record a breadcrumb in the current context. By default the last 100 breadcrumbs are kept.
			context.recordBreadcrumb(new BreadcrumbBuilder().setMessage("Attempting to save: " + data.size() + " records.").build());

			// Set the user in the current context.
			context.setUser(new UserBuilder().setUsername(username).build());

			int exponentialBackoffTime = 500;
			Map<String, List<WriteRequest>> unprocessedItems;
			try
			{
				List<DynamoDBMapper.FailedBatch> failedBatches = mapper.batchSave(data);
				if (failedBatches.size() == 0)
				{
					// all good
				}
				else
				{
					// time to start looping with exponential backoff
					unprocessedItems = failedBatches.get(0).getUnprocessedItems();
					while (unprocessedItems != null)
					{
						try
						{
							System.out.println("Unprocessed items, waiting " + exponentialBackoffTime / 1000. + " seconds before retrying");
							Thread.sleep(exponentialBackoffTime);
							unprocessedItems = client.batchWriteItem(unprocessedItems).getUnprocessedItems();
						}
						catch (InterruptedException ie)
						{
							return;
						}
						finally
						{
							exponentialBackoffTime *= 2;
							if (exponentialBackoffTime > MAX_EXPONENTIAL_BACKOFF_TIME)
							{
								exponentialBackoffTime = MAX_EXPONENTIAL_BACKOFF_TIME;
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				sentry.sendException(e);
			}
		});
	}
}
