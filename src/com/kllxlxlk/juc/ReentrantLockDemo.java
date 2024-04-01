package com.kllxlxlk.juc;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1、
 */
public class ReentrantLockDemo {

    public static ReentrantLock lock = new ReentrantLock();

    public static int count = 0;

    /**
     * ReentrantLock 是可重入锁，同一个线程可以多次获取锁。
     * 获取多少次锁就要释放多少次锁。
     * 只有获取到锁时才可以释放锁，否则会抛出 IllegalMonitorStateException 异常。
     */
    @Test
    public void test01() throws InterruptedException {
        Runnable runnable = () -> {
            for (int i = 0; i < 100000; i++) {
                lock.lock();
                lock.lock();
                try {
                    count++;
                } finally {
                    lock.unlock();
                    lock.unlock();
                }
            }
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("result: " + count);
    }


    /**
     * tryLock() 方法会尝试获取锁，获取不到会立刻返回 false，不会阻塞当前线程。
     */
    @Test
    public void test02() throws InterruptedException {
        AtomicInteger getLockFailNum = new AtomicInteger(0);

        Runnable runnable = () -> {
            for (int i = 0; i < 100000; i++) {
                boolean isSuccessful = lock.tryLock();
                if (!isSuccessful) {
                    System.out.println("获取锁失败");
                    getLockFailNum.getAndIncrement();
                    continue;
                }

                try {
                    count++;
                } finally {
                    lock.unlock();
                }
            }
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("失败次数: " + getLockFailNum);
        System.out.println("result: " + count);
    }

    /**
     * tryLock(long timeout, TimeUnit unit) 方法会尝试获取锁，如果超过指定时间仍未能获取锁则会返回 false，不会因为获取不到锁一直阻塞。
     */
    @Test
    public void test03() throws InterruptedException {
        AtomicInteger getLockFailNum = new AtomicInteger(0);

        Runnable runnable = () -> {
            for (int i = 0; i < 100000; i++) {
                boolean isSuccessful;
                try {
                    isSuccessful = lock.tryLock(100, TimeUnit.MICROSECONDS);
                } catch (InterruptedException e) {
                    System.out.println("线程被中断，获取锁失败");
                    getLockFailNum.getAndIncrement();
                    continue;
                }

                if (!isSuccessful) {
                    System.out.println("获取锁失败");
                    getLockFailNum.getAndIncrement();
                    continue;
                }

                try {
                    count++;
                } finally {
                    lock.unlock();
                }
            }
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("失败次数: " + getLockFailNum);
        System.out.println("result: " + count);
    }

    /**
     * tryLock() 方法在获取锁的过程中，如果线程被中断，会触发中断异常。
     */
    @Test
    public void test04() throws InterruptedException {
        AtomicInteger getLockFailNum = new AtomicInteger(0);

        Runnable runnable = () -> {
            for (int i = 0; i < 100000; i++) {
                boolean isSuccessful;
                try {
                    isSuccessful = lock.tryLock(100, TimeUnit.MICROSECONDS);
                } catch (InterruptedException e) {
                    System.out.println("线程被中断，获取锁失败");
                    getLockFailNum.getAndIncrement();
                    continue;
                }

                if (!isSuccessful) {
                    System.out.println("获取锁失败");
                    getLockFailNum.getAndIncrement();
                    continue;
                }

                try {
                    count++;
                } finally {
                    lock.unlock();
                }
            }
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        Thread t3 = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                t1.interrupt();
                t2.interrupt();
            }
        });

        t3.start();
        t1.start();
        t2.start();

        t1.join();
        t2.join();
        t3.interrupt();

        System.out.println("失败次数: " + getLockFailNum);
        System.out.println("result: " + count);
    }

    /**
     * tryLock() 方法在获取锁的过程中，如果线程被中断，会触发中断异常。
     */
    @Test
    public void test05() throws InterruptedException {

    }


}
