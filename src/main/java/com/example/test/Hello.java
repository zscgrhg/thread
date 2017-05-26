package com.example.test;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by think on 5/16/17.
 */
public class Hello {
    static final Semaphore available = new Semaphore(100, true);

    public static void dosleep(long dura){
        long start=System.currentTimeMillis();
        try {
            Thread.sleep(dura);
        } catch (InterruptedException e) {
            long left = dura - (System.currentTimeMillis() - start);
            dosleep(left);
        }
    }

    public static void sleepandwait(long dura)  {
        available.release();
        dosleep(dura);
        available.acquireUninterruptibly();
    }
    public static void main(String[] args) throws InterruptedException {


        ExecutorService service= Executors.newFixedThreadPool(5000);
        for (int i = 0; i < 5000; i++) {
            System.out.println(i);
            service.execute(new Runnable() {
                public void run() {
                    available.acquireUninterruptibly();
                    while (true){
                        try {
                            for (int k = 0; k < 2; k++) {
                                Document document =
                                        Jsoup.connect("http://127.0.0.1:8080").get();
                                String location = document.location();
                                System.out.println(location);
                            }
                        } catch (IOException e) {
                            System.out.println(e.getLocalizedMessage());
                        }
                        int dura = new Random().nextInt(30000);
                        sleepandwait(dura);

                    }
                }
            });

        }

    }
}
