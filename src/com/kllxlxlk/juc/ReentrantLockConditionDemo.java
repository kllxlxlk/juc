package com.kllxlxlk.juc;

import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockConditionDemo {

    @Test
    public void test01() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition conditionA = lock.newCondition();
        Condition conditionB = lock.newCondition();

        Thread thread = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaa111");
                conditionB.await();
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaa222");
            } catch (InterruptedException e) {
                // 中断
            }
            lock.unlock();
        });

        thread.start();

        Thread thread2 = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("bbbbbbbbbbbbbbbbbbbbbbb111");
                conditionA.await();
                System.out.println("bbbbbbbbbbbbbbbbbbbbbbb222");
            } catch (InterruptedException e) {
                // 中断
            }
            lock.unlock();
        });

        thread2.start();

        Thread.sleep(3000);
        lock.lock();
        conditionA.signalAll();
        lock.unlock();

        thread.join();
        thread2.join();
    }
}
