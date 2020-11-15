package com.eigenholser.outrider.spdrgldparser;

import java.io.IOException;

public class SpdrGldParser {

	public static void main(String[] args) throws InterruptedException {
		
		Thread t = 
				new Thread(() -> {
			SpdrGldProcessor processor = new SpdrGldProcessor();
			processor.getLatestSpdrGld();
			try {
				processor.parseSpdrGld();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		t.start();
		t.join();
	}
	
}
