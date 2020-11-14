package com.eigenholser.outrider.spdrgldparser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class SpdrGldParser {
	public enum Headers {
		date, gldClosePrice, lbmaGoldPrice, navPerGldInGold, navPerShare, indicativePriceGld, midpointSpread,
		gldPremium, dailyShareVolume, navOunces, navTonnes, navUsd;
	}

	public static void main(String[] args) throws IOException {
		File csvData = new File("/home/sover/dev1/outrider/GLD_US_archive_EN.csv");
		Charset charset = Charset.forName("UTF-8");
		CSVParser parser = CSVParser.parse(csvData, charset, CSVFormat.RFC4180.withHeader(Headers.class));
		
		Spliterator<CSVRecord> splitItr = Spliterators.spliteratorUnknownSize(parser.iterator(), Spliterator.ORDERED);
		Stream<CSVRecord> stream = StreamSupport.stream(splitItr, false);
		
		stream.forEach(System.out::println);
	}
}
