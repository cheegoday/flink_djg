package com.djg.withoutflink.DirectByteBufferGC;

import sun.nio.ch.Util;

import java.lang.ref.*;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.LockSupport;

public class Student {
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Student 被回收了");
    }

    public void main1(String[] args) {
        // 强引用：只要某个对象有强引用与之关联，这个对象永远不会被回收，即使内存不足，JVM宁愿抛出OOM，也不会去回收
        Student student = new Student();
        student = null;
        System.gc();
    }

    public static void main2(String[] args) {
        // 软引用：发生Full GC后如果内存够用，不回收；如果内存仍然不够用，回收软引用指向的对象。
        // 其特性适合用来做缓存
        SoftReference<byte[]> softReference = new SoftReference<byte[]>(new byte[1024 * 1024 * 10]);
        System.out.println(softReference.get());
        System.gc();
        System.out.println(softReference.get());
        // 同时设置vm参数 -Xmx20M，迫使发生Full GC
        byte[] bytes = new byte[1024 * 1024 * 10];
        System.out.println(softReference.get());
    }


    public static void main3(String[] args) {
        // 弱引用：不管内存是否足够，只要发生GC，都会被回收
        WeakReference<Student> weakReference = new WeakReference<>(new Student());
        System.out.println(weakReference.get());
        System.gc();
        System.out.println(weakReference.get());
    }


    // 无法通过虚引用来获取对一个对象的真实引用，即其 get永远返回null
    // 当GC准备回收一个对象，如果发现它还有虚引用，就会在回收之前，把这个虚引用加入到与之关联的ReferenceQueue中
    public static void main4(String[] args) throws InterruptedException {
        Object obj = new Object();
        ReferenceQueue queue = new ReferenceQueue();
        PhantomReference reference = new PhantomReference(obj, queue);
        System.out.println("queue:" + queue.poll());
        obj = null;
        System.gc();
        // JVM将虚引用塞入pending队列需要一个时间，不是立马就塞进去
        Thread.sleep(2000);
        System.out.println("-----after gc-----------");
        System.out.println("queue:" + queue.poll());

    }

    // 直接内存持有Cleaner对象，Cleaner是一个虚引用
    // 详情请debug java.lang.ref.Reference#tryHandlePending
    public static void main(String[] args) throws InterruptedException {
        ReferenceQueue queue = new ReferenceQueue();
        ByteBuffer byteBuffer = Util.getTemporaryDirectBuffer(1024);
        byteBuffer = null;
        System.gc();
        LockSupport.park();
    }
}