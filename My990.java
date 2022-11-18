package com.example.kinesis;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class My990 {

	private UUID uuid = UUID.randomUUID();
	private String xmlText;
	private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	private String jsonText;

	public My990(UUID uuid, String xmlText, Timestamp timestamp, String jsonText) {
		super();
		this.uuid = uuid;
		this.xmlText = xmlText;
		this.timestamp = timestamp;
		this.jsonText = jsonText;
	}

	public My990() {

	}

	public String getJsonText() {
		return jsonText;
	}

	public void setJsonText(String jsonText) {
		this.jsonText = jsonText;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getXmlText() {
		return xmlText;
	}

	public void setXmlText(String xmlText) {
		this.xmlText = xmlText;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	private final static ObjectMapper JSON = new ObjectMapper();
	static {
		JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public byte[] toJsonAsBytes() {
		try {
			return JSON.writeValueAsBytes(this);
		} catch (IOException e) {
			return null;
		}
	}

}
