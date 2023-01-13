package ru.netology.valueinterval;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time
        ExecutorService es = Executors.newFixedThreadPool(texts.length);
        List<Future<Integer>> threads = new ArrayList<>();

        for (String text : texts) {
            Callable<Integer> myCallable = new MyCallable(text);
            Future<Integer> future = es.submit(myCallable);
            threads.add(future);
        }
        for (Future<Integer> future : threads) {
            int result = future.get();
            System.out.println(result);
        }
        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
        es.shutdown();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}

class MyCallable implements Callable<Integer> {

    private String text;
    public static int valueMax;

    public MyCallable(String text) {
        int maxSize = 0;
        for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < text.length(); j++) {
                if (i >= j) {
                    continue;
                }
                boolean bFound = false;
                for (int k = i; k < j; k++) {
                    if (text.charAt(k) == 'b') {
                        bFound = true;
                        break;
                    }
                }
                if (!bFound && maxSize < j - i) {
                    maxSize = j - i;
                }
            }
        }
        this.text = text.substring(0, 100) + " -> " + maxSize;
        if (maxSize > valueMax) {
            valueMax = maxSize;
        }
    }

    @Override
    public Integer call() throws Exception {
        return valueMax;
    }
}
