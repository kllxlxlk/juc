package com.kllxlxlk.juc;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 1、
 */
public class ReentrantLockDemo {

    public static ReentrantLock lock = new ReentrantLock();

    public static int count = 0;

    public static void main(String[] args) throws InterruptedException {
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

}
