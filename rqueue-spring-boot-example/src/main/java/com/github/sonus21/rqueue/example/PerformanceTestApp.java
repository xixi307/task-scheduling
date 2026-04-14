package com.github.sonus21.rqueue.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceTestApp {

    private static final String BASE_URL = "http://localhost:8088";
    private static final int CONCURRENT_THREADS = 10;
    private static final int REQUESTS_PER_THREAD = 100;

    public static void main(String[] args) {
        System.out.println("Starting performance tests...");
        
        try {
            testSingleRequestPerformance();
            testTaskSubmissionPerformance();
            testTaskProcessing();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Performance tests completed.");
        }
    }

    public static void testTaskSubmissionPerformance() throws InterruptedException {
        System.out.println("\n=== Task Submission Performance Test ===");
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger duplicateCount = new AtomicInteger(0);
        List<String> messageIds = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
                        String message = "Task_" + threadId + "_" + j;
                        int statusCode = 0;
                        String responseBody = "";
                        
                        try {
                            URL url = new URL(BASE_URL + "/job?msg=" + message);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(5000);
                            connection.setReadTimeout(5000);
                            
                            statusCode = connection.getResponseCode();
                            
                            if (statusCode == 200) {
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(connection.getInputStream()));
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                                reader.close();
                                responseBody = sb.toString();
                            }
                            
                            connection.disconnect();
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                            continue;
                        }
                        
                        if (statusCode == 200) {
                            successCount.incrementAndGet();
                            // Check for duplicates
                            if (messageIds.contains(responseBody)) {
                                duplicateCount.incrementAndGet();
                            } else {
                                messageIds.add(responseBody);
                            }
                        } else {
                            failureCount.incrementAndGet();
                        }
                        
                        // Small delay to avoid overwhelming the server
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(2, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        int totalRequests = CONCURRENT_THREADS * REQUESTS_PER_THREAD;
        double qps = (totalRequests * 1000.0) / duration;
        double successRate = (successCount.get() * 100.0) / totalRequests;
        double failureRate = (failureCount.get() * 100.0) / totalRequests;
        double duplicateRate = (duplicateCount.get() * 100.0) / totalRequests;
        
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + failureCount.get());
        System.out.println("Duplicate Responses: " + duplicateCount.get());
        System.out.println("Test Duration: " + duration + " ms");
        System.out.println("QPS: " + String.format("%.2f", qps));
        System.out.println("Success Rate: " + String.format("%.2f", successRate) + "%");
        System.out.println("Failure Rate: " + String.format("%.2f", failureRate) + "%");
        System.out.println("Duplicate Rate: " + String.format("%.2f", duplicateRate) + "%");
        
        executorService.shutdown();
    }

    public static void testSingleRequestPerformance() {
        System.out.println("\n=== Single Request Performance Test ===");
        
        long startTime = System.currentTimeMillis();
        int statusCode = 0;
        String responseBody = "";
        
        try {
            URL url = new URL(BASE_URL + "/job?msg=test_single");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            statusCode = connection.getResponseCode();
            
            if (statusCode == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                responseBody = sb.toString();
            }
            
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        
        System.out.println("Request Latency: " + latency + " ms");
        System.out.println("Response Status: " + statusCode);
        System.out.println("Response Body: " + responseBody);
    }

    public static void testTaskProcessing() throws InterruptedException {
        System.out.println("\n=== Task Processing Test ===");
        
        // Submit multiple tasks
        for (int i = 0; i < 10; i++) {
            try {
                URL url = new URL(BASE_URL + "/job?msg=processing_test_" + i);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.getResponseCode();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Wait for processing
        System.out.println("Submitted 10 tasks for processing");
        System.out.println("Waiting for 30 seconds to allow tasks to process...");
        Thread.sleep(30000);
        System.out.println("Task processing test completed");
    }
}
