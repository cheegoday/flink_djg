package com.djg.juc;

public class ThreadLocalDemo {
    /**
     * ThreadLocal变量，每个线程都有一个副本，互不干扰
     */
    public static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();


    public void threadLocalTest() throws Exception {
        // 主线程设置值
        THREAD_LOCAL.set("wupx");
        String v = THREAD_LOCAL.get();
        System.out.println("Thread-0线程执行之前，" + Thread.currentThread().getName() + "线程取到的值：" + v);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String v = THREAD_LOCAL.get();
                System.out.println(Thread.currentThread().getName() + "线程取到的值：" + v);
                // 设置 threadLocal
                THREAD_LOCAL.set("huxy");
                v = THREAD_LOCAL.get();
                System.out.println("重新设置之后，" + Thread.currentThread().getName() + "线程取到的值为：" + v);
                System.out.println(Thread.currentThread().getName() + "线程执行结束");
            }
        }).start();
        // 等待所有线程执行结束
        Thread.sleep(3000L);
        v = THREAD_LOCAL.get();
        System.out.println("Thread-0线程执行之后，" + Thread.currentThread().getName() + "线程取到的值：" + v);
    }

    public static void main(String[] args) throws Exception {
        new ThreadLocalDemo().threadLocalTest();
    }
}