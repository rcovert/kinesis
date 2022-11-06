package com.example.kinesis;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamResponse;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;

import java.io.*;
import com.example.kinesis.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MyKDS {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String streamName = "Test990-kds";
		Region region = Region.US_EAST_1;
		KinesisClient kinesisClient = KinesisClient.builder().region(region)
				.credentialsProvider(ProfileCredentialsProvider.create()).build();

		// Ensure that the Kinesis Stream is valid.
		validateStream(kinesisClient, streamName);
		sendTestData(kinesisClient, streamName);
		kinesisClient.close();

	}

	public static void sendTestData(KinesisClient kinesisClient, String streamName) {

		try {
			// Repeatedly send stock trades with a 100 milliseconds wait in between.

			// Put in 50 Records for this example.
			int index = 2;
			for (int x = 0; x < index; x++) {
				String xyz = "this is a test: " + x;
				sendTestDataString(xyz, kinesisClient, streamName);
				Thread.sleep(100);
			}

		} catch (KinesisException | InterruptedException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("Done");
	}

	private static void sendTestDataString(String xyz, KinesisClient kinesisClient, String streamName) {
		byte[] bytes = xyz.getBytes();

// The bytes could be null if there is an issue with the JSON serialization by the Jackson JSON library.
		if (bytes == null) {
			System.out.println("Could not get bytes for test");
			return;
		}

		System.out.println("Putting test: " + xyz);
		UUID uuid = UUID.randomUUID();
		PutRecordRequest request = PutRecordRequest.builder()
	            .partitionKey(uuid.toString()) // We use the ticker symbol as the partition key, explained in the Supplemental Information section below.
	            .streamName(streamName)
	            .data(SdkBytes.fromByteArray(bytes))
	            .build();
		try {
			System.out.println("here in put record...");
			PutRecordResponse prr = kinesisClient.putRecord(request);
			System.out.println("response is: " + prr.shardId());
		} catch (KinesisException e) {
			e.getMessage();
		}
	}

	private static void validateStream(KinesisClient kinesisClient, String streamName) {
		try {
			DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder().streamName(streamName)
					.build();

			DescribeStreamResponse describeStreamResponse = kinesisClient.describeStream(describeStreamRequest);

			if (!describeStreamResponse.streamDescription().streamStatus().toString().equals("ACTIVE")) {
				System.err.println("Stream " + streamName + " is not active. Please wait a few moments and try again.");
				System.exit(1);
			}

		} catch (KinesisException e) {
			System.err.println("Error found while describing the stream " + streamName);
			System.err.println(e);
			System.exit(1);
		}
	}

}
