package com.eigenholser.outrider.spdrgldparser;

import java.io.IOException;

public class SpdrGldParser {

	public static void main(String[] args) throws InterruptedException {

		new Thread(() -> {
			try {
				new SpdrGldProcessor().parseSpdrGld();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		System.out.println("Started Processing");
	}

}
