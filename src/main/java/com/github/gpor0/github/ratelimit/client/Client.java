package com.github.gpor0.github.ratelimit.client;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * author: gpor0@github.com
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) throws InterruptedException, IOException {

        if (args.length < 1) {
            System.err.println("at least one argument must be provided (number of threads)");
            return;
        }

        final Integer successHttpCode = 200;
        final Integer i = Integer.valueOf(args[0]);

        final String host = args.length <= 1 || args[1] == null || args[1].isBlank() ? "http://localhost:8080" : args[1];

        LOG.info(i + " clients will be scheduled in parallel using host " + host + ". Requests from each thread will be made in random of 1 to " +
                "3000ms");
        final Scheduler scheduler = Schedulers.io();

        Observable.range(1, i).flatMap(id -> Observable.just(id).observeOn(scheduler).map(f -> {
            while (true) {
                try {
                    long waitMs = ThreadLocalRandom.current().nextInt(1, 3000);
                    Thread.sleep(waitMs);
                    URL url = new URL(host + "/api?clientId=" + id);
                    HttpURLConnection urlConnection = (HttpURLConnection) (url.openConnection());
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(100); //timeout in 100 millis
                    urlConnection.connect();
                    int code = urlConnection.getResponseCode();
                    if (!successHttpCode.equals(200)) {
                        System.out.println("Remote service returned http " + code);
                    }
                    urlConnection.disconnect();
                } catch (Exception e) {
                    LOG.fine("Error on thread " + e.getMessage());
                }
            }
        })).subscribe();

        LOG.info(i + " threads spawned (press any char to end program)");
        System.in.read();
        LOG.info("canceled by user input");
        scheduler.shutdown(); //this stops all threads & cleanup
        LOG.info("scheduler stopped, program will end in 2 sec");
        Thread.sleep(2000);
        LOG.info("ended");
    }


}
