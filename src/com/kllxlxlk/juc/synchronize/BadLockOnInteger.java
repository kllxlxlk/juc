package com.kllxlxlk.juc.synchronize;

/**
 * count++ 实际上是创建了一个新的 Integer 对象赋值给 count
 * 所以两个线程多次获取对象锁时，根本不是锁的同一个对象
 * 最简单的避免错误的方式：给你要锁住的对象加上 final 修饰符
 */
public class BadLockOnInteger {

    public static Integer count = 0;

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> {
            for (int i = 0; i < 100000; i++) {
                synchronized (count) {
                    count++;
                }
            }
        };

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("result: " + count);
    }
}
