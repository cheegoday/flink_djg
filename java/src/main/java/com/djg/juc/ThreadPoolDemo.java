package com.djg.juc;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

class ThreadPoolTask implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(10000);
                System.out.println(Thread.currentThread().getName() + ":" + i);
            } catch (InterruptedException e) {
                throw new RuntimeException(Thread.currentThread().getName() + "被中断");
            }
        }
    }
}


public class ThreadPoolDemo {
    public static void main(String[] args) {
// 线程池
        ExecutorService pool = Executors.newFixedThreadPool(10);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) pool;

        // 开启线程
        executor.execute(new ThreadPoolTask());
        executor.execute(new ThreadPoolTask());
        executor.execute(new ThreadPoolTask());
        System.out.println(executor.shutdownNow());
    }
}