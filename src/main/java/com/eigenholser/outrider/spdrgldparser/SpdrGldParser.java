package com.eigenholser.outrider.spdrgldparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class SpdrGldParser {
	private static final ObjectMapper mapper = new ObjectMapper() //
			.registerModule(new JavaTimeModule()) //
			.enable(SerializationFeature.INDENT_OUTPUT);

	private static final HttpClient httpClient = HttpClient.newBuilder() //
			.version(HttpClient.Version.HTTP_1_1) //
			.connectTimeout(Duration.ofSeconds(10)) //
			.build();

	private static final String navHistoryUri = "https://www.spdrgoldshares.com/assets/dynamic/GLD/GLD_US_archive_EN.csv";

	public enum Headers {
		date, gldClosePrice, lbmaGoldPrice, navPerGldInGold, navPerShare, indicativePriceGld, midpointSpread,
		gldPremium, dailyShareVolume, navOunces, navTonnes, navUsd;
	}

	public static void main(String[] args) throws IOException {
		// Process command-line args
		// SpdrGldProcessor proc = new SpdrGldProcessor();
		// proc.run()
		SpdrGld spdrGldLatest = getLatestSpdrGldFromApi();
		LocalDate mostRecentNavDate = spdrGldLatest.getDate();
		System.out.println("Most recent date: " + mostRecentNavDate);

		//System.exit(0);
		BufferedReader csvData = getSpdrGldNavHistory();

		/*
		 * The first seven lines of this file is boilerplate and header. Discard.
		 */
		IntStream.range(1, 8).forEach(i -> {
			try {
				csvData.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		// File csvData = new File("/home/sover/dev1/outrider/GLD_US_archive_EN.csv");
		// Charset charset = Charset.forName("UTF-8");
		CSVParser parser = CSVParser.parse(csvData, CSVFormat.RFC4180 //
				.withHeader(Headers.class) //
				.withIgnoreSurroundingSpaces());

		/*
		 * Convert parser iterator to stream.
		 */
		Spliterator<CSVRecord> splitItr = Spliterators.spliteratorUnknownSize(parser.iterator(), Spliterator.ORDERED);
		Stream<CSVRecord> csvRecordStream = StreamSupport.stream(splitItr, false);

		// TODO: leave this here for now.
		final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

		csvRecordStream //
				.filter(x -> !x.get(Headers.gldClosePrice).contentEquals("HOLIDAY")) // Any holiday
				.filter(x -> !x.get(Headers.gldClosePrice).contentEquals("NYSE Closed")) // October 29/30 2012 Hurricane
																							// Sandy
				.filter(x -> !x.get(Headers.gldClosePrice).contentEquals("AWAITED")) // New record not yet ready and
																						// 2018-07-03
				.filter(x -> !x.get(Headers.gldPremium).contentEquals("AWAITED")) // 2018-07-24 bad value
				.filter(x -> LocalDate.parse(x.get(Headers.date), dateTimeFormatter).compareTo(mostRecentNavDate) > 0) //
				.forEach((x) -> {
					SpdrGld spdrGld = new SpdrGld(//
							LocalDate.parse(x.get(Headers.date), dateTimeFormatter), //
							new BigDecimal(x.get(Headers.gldClosePrice)), //
							new BigDecimal(x.get(Headers.lbmaGoldPrice).substring(1)), //
							new BigDecimal(x.get(Headers.navPerGldInGold)), //
							new BigDecimal(x.get(Headers.navPerShare)), //
							new BigDecimal(x.get(Headers.indicativePriceGld)), //
							new BigDecimal(x.get(Headers.midpointSpread).substring(1)), //
							new BigDecimal(
									x.get(Headers.gldPremium).substring(0, x.get(Headers.gldPremium).length() - 1)), //
							Long.parseLong(x.get(Headers.dailyShareVolume)), //
							new BigDecimal(x.get(Headers.navOunces)), //
							new BigDecimal(x.get(Headers.navTonnes)), //
							new BigDecimal(x.get(Headers.navUsd)) //
					);

					System.out.println(spdrGld);
					String spdrGldJson;
					try {
						spdrGldJson = mapper.writeValueAsString(spdrGld);
						System.out.println(spdrGldJson);
						int statusCode = createSpdrGld(spdrGldJson);
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
		
	}

	/**
	 * Fetch SPDR GLD NAV history file. Return Reader for parsing.
	 * 
	 * @return Reader
	 */
	public static BufferedReader getSpdrGldNavHistory() {
		HttpRequest request = HttpRequest.newBuilder() //
				.GET() //
				.uri(URI.create(navHistoryUri)) //
				.setHeader("User-Agent", "SPDR GLD Robot") //
				.build();
		CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request,
				HttpResponse.BodyHandlers.ofString());
		try {
			String body = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
			Reader navHistoryFile = new StringReader(body);
			return new BufferedReader(navHistoryFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static SpdrGld getLatestSpdrGldFromApi() {
		String spdrGldLatestUri = "http://localhost:8080/spdrgld/latest";
		HttpRequest request = HttpRequest.newBuilder() //
				.GET() //
				.uri(URI.create(spdrGldLatestUri)) //
				.setHeader("User-Agent", "SPDR GLD Robot") //
				.build();
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			String body = response.body();
			SpdrGld spdrGld = mapper.readValue(body, SpdrGld.class);
			return spdrGld;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static int createSpdrGld(String spdrGldJson) {
		String spdrGldUri = "http://localhost:8080/spdrgld";
		HttpRequest request = HttpRequest.newBuilder() //
				.POST(BodyPublishers.ofString(spdrGldJson)) //
				.uri(URI.create(spdrGldUri)) //
				.header("Content-Type", "application/json") //
				.setHeader("User-Agent", "SPDR GLD Robot") //
				.build();
		try {
			HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
			return response.statusCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 500;
	}
}
