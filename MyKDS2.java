package com.example.kinesis;
// kinesis putRecords

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamResponse;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.kinesis.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.json.*;

public class MyKDS2 {

	public static void main(String[] args) {
		String streamName = "Test990-kds";
		Region region = Region.US_EAST_1;
		KinesisClient kinesisClient = KinesisClient.builder().region(region)
				.credentialsProvider(ProfileCredentialsProvider.create()).build();

		// Ensure that the Kinesis Stream is valid.
		validateStream(kinesisClient, streamName);
		sendTestData(kinesisClient, streamName);
		kinesisClient.close();
	}

	private static void sendTestData(KinesisClient kinesisClient, String streamName) {

		List<PutRecordsRequestEntry> theCollection = new ArrayList<PutRecordsRequestEntry>();
		File folder = new File("./input");
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
		// create some json and send to kinesis
		File theFile;
		int index = 2;
		for (int x = 0; x < index; x++) {
			My990 m9 = new My990();
			String data = null;
			String xmlString = null;

			// get next xml file and embed into the message
			theFile = listOfFiles[x];
			FileInputStream fis;
			try {
				fis = new FileInputStream(theFile);
				data = IOUtils.toString(fis, "UTF-8");
				data += "\n"; // add delimiter to demarcate records
				xmlString = data;
				m9.setXmlText(xmlString);
				byte[] bytes = m9.toJsonAsBytes();

				UUID uuid = UUID.randomUUID();
				PutRecordsRequestEntry prre = createPutRecordsRequestEntry(SdkBytes.fromByteArray(bytes),
						uuid.toString());
				theCollection.add(prre);
				String s = new String(bytes, StandardCharsets.UTF_8);
				System.out.println("message is: " + s);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now)); // 2016/11/16 12:08:43
		PutRecordsRequest myPutRecordsRequest = createPutRecordsRequest(theCollection);
		PutRecordsResponse putRecordsResponse = kinesisClient.putRecords(myPutRecordsRequest);
		System.out.println("Response is " + putRecordsResponse.records());
	}

	private static PutRecordsRequestEntry createPutRecordsRequestEntry(SdkBytes theData, String partitionKey) {
		return PutRecordsRequestEntry.builder().data(theData).partitionKey(partitionKey).build();
	}

	private static PutRecordsRequest createPutRecordsRequest(final List<PutRecordsRequestEntry> batch) {
		return PutRecordsRequest.builder().streamName("Test990-kds").records(batch).build();
	}

	private static void validateStream(KinesisClient kinesisClient, String streamName) {
		try {
			DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder().streamName(streamName)
					.build();

			DescribeStreamResponse describeStreamResponse = kinesisClient.describeStream(describeStreamRequest);

			// System.out.println("status of the stream is: " +
			// describeStreamResponse.responseMetadata());
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
