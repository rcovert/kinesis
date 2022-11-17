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
			// Repeatedly send xml documents with a 100 milliseconds wait in between.

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
			int index = 5;
			for (int x = 0; x < index; x++) {
				My990 m9 = new My990();
				String data = null;
				String xmlString = null;
		
				// get next xml file and embed into the message
				theFile = listOfFiles[x];
				
				FileInputStream fis = new FileInputStream(theFile);
			    data = IOUtils.toString(fis, "UTF-8");
			    xmlString = data;
			    //data += "\n";
				m9.setXmlText(xmlString);
				byte[] bytes = m9.toJsonAsBytes();
				
				sendTestData(m9, kinesisClient, streamName);

				String s = new String(bytes, StandardCharsets.UTF_8);
				System.out.println(x + " message is: " + s);
				Thread.sleep(100);
			}
		} catch (KinesisException | InterruptedException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
	}

	private static void sendTestData(My990 xyz, KinesisClient kinesisClient, String streamName) {

		byte[] bytes = xyz.toJsonAsBytes();

// The bytes could be null if there is an issue with the JSON serialization by the Jackson JSON library.
		if (xyz == null) {
			System.out.println("Could not get bytes for test");
			return;
		}

		// System.out.println("Putting test: " + xyz);
		UUID uuid = UUID.randomUUID();
		PutRecordRequest request = PutRecordRequest.builder().partitionKey(uuid.toString()) // We use the ticker symbol
																							// as the partition key,
																							// explained in the
																							// Supplemental Information
																							// section below.
				.streamName(streamName).data(SdkBytes.fromByteArray(bytes)).build();
		try {
			// System.out.println("here in put record...");		
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			System.out.println(dtf.format(now)); //2016/11/16 12:08:43
			PutRecordResponse prr = kinesisClient.putRecord(request);
			System.out.println("response shard is: " + prr.shardId());
			System.out.println("response seq is: " + prr.sequenceNumber());
		} catch (KinesisException e) {
			e.getMessage();
		}
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