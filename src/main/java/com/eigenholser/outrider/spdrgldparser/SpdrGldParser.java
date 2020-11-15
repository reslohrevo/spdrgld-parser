package com.eigenholser.outrider.spdrgldparser;

import java.io.IOException;

public class SpdrGldParser {

	public static void main(String[] args) throws IOException {
		SpdrGldProcessor processor = new SpdrGldProcessor();
		
		processor.getLatestSpdrGld();
		processor.parseSpdrGld();
		
	}
	
}
