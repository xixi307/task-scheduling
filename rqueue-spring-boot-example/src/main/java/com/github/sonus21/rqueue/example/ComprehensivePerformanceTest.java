package com.github.sonus21.rqueue.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ComprehensivePerformanceTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final int CONCURRENT_THREADS = 20;
    private static final int REQUESTS_PER_THREAD = 200;
    private static final int REPEAT_REQUESTS = 50;

    public static void main(String[] args) {
        System.out.println("Starting comprehensive performance tests...");
        
        try {
            testQPSPerformance();
            testResponseTimeDistribution();
            testTaskProcessingThroughput();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Comprehensive performance tests completed.");
        }
    }

    // 测试QPS性能
    public static void testQPSPerformance() throws InterruptedException {
        System.out.println("\n=== QPS Performance Test ===");
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
                        try {
                            URL url = new URL(BASE_URL + "/job?msg=qps_test_" + threadId + "_" + j);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(3000);
                            connection.setReadTimeout(3000);
                            
                            int statusCode = connection.getResponseCode();
                            if (statusCode == 200) {
                                successCount.incrementAndGet();
                            } else {
                                failureCount.incrementAndGet();
                            }
                            
                            connection.disconnect();
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(1, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        int totalRequests = CONCURRENT_THREADS * REQUESTS_PER_THREAD;
        double qps = (totalRequests * 1000.0) / duration;
        double successRate = (successCount.get() * 100.0) / totalRequests;
        
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests: " + failureCount.get());
        System.out.println("Test Duration: " + duration + " ms");
        System.out.println("QPS: " + String.format("%.2f", qps));
        System.out.println("Success Rate: " + String.format("%.2f", successRate) + "%");
        
        executorService.shutdown();
    }

    // 测试响应时间分布（P99）
    public static void testResponseTimeDistribution() {
        System.out.println("\n=== Response Time Distribution Test ===");
        
        List<Long> responseTimes = new ArrayList<>();
        int successCount = 0;
        
        for (int i = 0; i < 500; i++) {
            long startTime = System.currentTimeMillis();
            
            try {
                URL url = new URL(BASE_URL + "/job?msg=response_time_test_" + i);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                
                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    successCount++;
                }
                connection.disconnect();
            } catch (Exception e) {
                // 忽略错误，继续测试
            }
            
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            responseTimes.add(responseTime);
        }
        
        // 计算P99响应时间
        responseTimes.sort(Long::compareTo);
        int p99Index = (int) (responseTimes.size() * 0.99);
        long p99ResponseTime = responseTimes.get(p99Index);
        long avgResponseTime = (long) responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        long maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        
        System.out.println("Total Requests: " + responseTimes.size());
        System.out.println("Successful Requests: " + successCount);
        System.out.println("Average Response Time: " + avgResponseTime + " ms");
        System.out.println("Max Response Time: " + maxResponseTime + " ms");
        System.out.println("P99 Response Time: " + p99ResponseTime + " ms");
    }

    // 测试任务处理吞吐量
    public static void testTaskProcessingThroughput() throws InterruptedException {
        System.out.println("\n=== Task Processing Throughput Test ===");
        
        // 提交大量任务
        int taskCount = 500;
        List<String> taskIds = new ArrayList<>();
        
        System.out.println("Submitting " + taskCount + " tasks...");
        long submitStartTime = System.currentTimeMillis();
        
        for (int i = 0; i < taskCount; i++) {
            try {
                URL url = new URL(BASE_URL + "/job?msg=throughput_test_" + i);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                
                int statusCode = connection.getResponseCode();
                if (statusCode == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    String responseBody = reader.lines().collect(Collectors.joining());
                    reader.close();
                    taskIds.add(responseBody);
                }
                
                connection.disconnect();
            } catch (Exception e) {
                // 忽略错误，继续测试
            }
        }
        
        long submitEndTime = System.currentTimeMillis();
        long submitDuration = submitEndTime - submitStartTime;
        double submitThroughput = (taskCount * 1000.0) / submitDuration;
        
        System.out.println("Tasks submitted: " + taskIds.size());
        System.out.println("Submission duration: " + submitDuration + " ms");
        System.out.println("Submission throughput: " + String.format("%.2f", submitThroughput) + " tasks/sec");
        
        // 等待任务处理
        System.out.println("Waiting for task processing...");
        Thread.sleep(30000); // 等待30秒让任务处理完成
        
        System.out.println("Task processing throughput test completed");
    }
}
