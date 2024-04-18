package com.kllxlxlk.juc;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


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
     * tryLock(long timeout, TimeUnit unit) 方法在获取锁的过程中，如果线程被中断，会触发中断异常。
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
     * lockInterruptibly() 与 tryLock() 方法的不同点在于，tryLock() 不会对中断做出反应，lockInterruptibly() 会对中断做出反应
     */
    @Test
    public void test05() throws InterruptedException {
        // 先开启一个线程占用锁
        new Thread(() -> {
            boolean getLockResult = lock.tryLock();
            if (getLockResult) {
                System.out.println("获取锁成功");
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException ignored) {
                }
                lock.unlock();
            } else {
                System.out.println("获取锁失败");
            }
        }).start();

        // 等待 1s，确保新线程已经成功占用了锁
        Thread.sleep(1000);

        // 开启一个新的线程使用 lockInterruptibly() 方法获取线程
        Thread thread = new Thread(() -> {
            try {
                lock.lockInterruptibly();
                System.out.println("thread 取锁成功");
                lock.unlock();
            } catch (InterruptedException e) {
                System.out.println("thread 在获取锁的过程中被中断");
            }
        });
        thread.start();

        // 主线程等待 1s，确保线程 thread 已经开始运行
        Thread.sleep(1000);
        thread.interrupt();
    }

    /**
     * 公平锁
     */
    @Test
    public void testFairLock() {
        ReentrantLock fairLock = new ReentrantLock(true);
        System.out.println("是否是公平锁：" + fairLock.isFair());
    }

    /**
     * 测试 isLocked() 方法
     */
    @Test
    public void testIsLocked() {
        System.out.println("锁是否被持有：" + lock.isLocked());
        lock.lock();
        System.out.println("锁是否被持有：" + lock.isLocked());
        lock.unlock();
        System.out.println("锁是否被持有：" + lock.isLocked());
    }

    /**
     * getHoldCount() 方法：返回当前线程获取锁的次数
     */
    @Test
    public void testGetHoldCount() {
        System.out.println("获取锁的次数：" + lock.getHoldCount());
        lock.lock();
        System.out.println("获取锁的次数：" + lock.getHoldCount());
        lock.lock();
        System.out.println("获取锁的次数：" + lock.getHoldCount());
        lock.unlock();
        System.out.println("获取锁的次数：" + lock.getHoldCount());
        lock.unlock();
        System.out.println("获取锁的次数：" + lock.getHoldCount());
    }

    /**
     * getQueueLength()：正在等待获取此锁的线程数量，能够反应资源竞争的激烈程度，是估计值，并不精确。
     */
    @Test
    public void testGetQueueLength() throws InterruptedException {
        Runnable getLock = () -> {
            lock.lock();
            lock.unlock();
        };

        // 首先，主线程先把锁给占了
        lock.lock();
        Thread.sleep(1000);
        System.out.println("等待获取锁的线程数：" + lock.getQueueLength());

        new Thread(getLock).start();
        Thread.sleep(1000);
        System.out.println("等待获取锁的线程数：" + lock.getQueueLength());

        new Thread(getLock).start();
        Thread.sleep(1000);
        System.out.println("等待获取锁的线程数：" + lock.getQueueLength());

        new Thread(getLock).start();
        Thread.sleep(1000);
        System.out.println("等待获取锁的线程数：" + lock.getQueueLength());

        lock.unlock();
        Thread.sleep(1000);
        System.out.println("等待获取锁的线程数：" + lock.getQueueLength());
    }

    /**
     * getWaitQueueLength()，获取在某个条件上等待的线程数
     */
    @Test
    public void testGetWaitQueueLength() throws InterruptedException {
        Condition condition = lock.newCondition();

        Runnable getCondition = () -> {
            try {
                lock.lock();
                condition.await();
                lock.unlock();
            } catch (InterruptedException e) {
            }
        };

        printWaitQueueLength(lock, condition);

        new Thread(getCondition).start();
        Thread.sleep(500);
        printWaitQueueLength(lock, condition);

        new Thread(getCondition).start();
        Thread.sleep(500);
        printWaitQueueLength(lock, condition);

        new Thread(getCondition).start();
        Thread.sleep(500);
        printWaitQueueLength(lock, condition);

        new Thread(getCondition).start();
        Thread.sleep(500);
        printWaitQueueLength(lock, condition);

        // 唤醒所有线程
        lock.lock();
        condition.signalAll();
        lock.unlock();
        Thread.sleep(500);
        printWaitQueueLength(lock, condition);

        // 等待线程执行完毕
        Thread.sleep(10 * 1000);
    }

    private void printWaitQueueLength(ReentrantLock lock, Condition condition) {
        lock.lock();
        System.out.println("等待获取 Condition 的线程数：" + lock.getWaitQueueLength(condition));
        lock.unlock();
    }

    /**
     * isHeldByCurrentThread()：判断当前线程是否持有这个锁
     */
    @Test
    public void testIsHeldByCurrentThread() {
        System.out.println("当前线程是否持有锁：" + lock.isHeldByCurrentThread());
        lock.lock();
        System.out.println("当前线程是否持有锁：" + lock.isHeldByCurrentThread());
        lock.unlock();
        System.out.println("当前线程是否持有锁：" + lock.isHeldByCurrentThread());
    }

    /**
     * hasQueuedThread(Thread thread)，判断指定的线程是否在等待这个锁
     */
    @Test
    public void testHasQueuedThread() throws InterruptedException {
        // 主线程先把锁给占了
        lock.lock();

        Thread thread = new Thread(() -> {
            lock.lock();
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
            }
            lock.unlock();
        });

        System.out.println("线程 thread 是否在等待获取锁：" + lock.hasQueuedThread(thread));

        thread.start();
        Thread.sleep(500);
        System.out.println("线程 thread 是否在等待获取锁：" + lock.hasQueuedThread(thread));

        lock.unlock();
        Thread.sleep(500);
        System.out.println("线程 thread 是否在等待获取锁：" + lock.hasQueuedThread(thread));
    }

    /**
     * hasQueuedThreads()，判断是否有线程在等待这个锁
     */
    @Test
    public void testHasQueuedThreads() throws InterruptedException {
        // 主线程先把锁给占了
        lock.lock();

        Thread thread = new Thread(() -> {
            lock.lock();
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
            }
            lock.unlock();
        });

        System.out.println("是否有线程在等待获取锁：" + lock.hasQueuedThreads());

        thread.start();
        Thread.sleep(500);
        System.out.println("是否有线程在等待获取锁：" + lock.hasQueuedThreads());

        lock.unlock();
        Thread.sleep(500);
        System.out.println("是否有线程在等待获取锁：" + lock.hasQueuedThreads());
    }

    /**
     * hasWaiters()，判断是否有线程在等待指定的条件
     */
    @Test
    public void testHasWaiters() throws InterruptedException {
        Condition condition = lock.newCondition();

        Runnable getCondition = () -> {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
            }
            lock.unlock();
        };

        Thread thread = new Thread(getCondition);

        printIfConditionHasWaiters(lock, condition);

        thread.start();
        Thread.sleep(500);
        printIfConditionHasWaiters(lock, condition);

        lock.lock();
        condition.signalAll();
        lock.unlock();
        Thread.sleep(500);
        printIfConditionHasWaiters(lock, condition);
    }

    private void printIfConditionHasWaiters(ReentrantLock lock, Condition condition) {
        lock.lock();
        System.out.println("是否有线程在等待 condition：" + lock.hasWaiters(condition));
        lock.unlock();
    }


}
