package com.kllxlxlk.juc.basic;


import org.junit.Test;

/**
 * Object.wait(long timeout)
 * 1、使当前线程进入等待状态
 * 2、释放对象锁
 * 3、如果当前线程并没有获取到此对象的对象锁，调用 wait() 方法会抛出 IllegalMonitorStateException
 * 4、在等待过程中，如果线程被调用 interrupt() 方法，会抛出中断异常，并且会清除线程的中断状态
 * 5、如果设置了等待时间，时间到了后会自动被唤醒
 *
 * Object.notify()
 * 1、随机唤醒一个在此对象上等待的线程，被唤醒的线程要重新获取对象锁才能继续运行
 * 2、如果没有提前获取对象锁，调用此方法会抛出 IllegalMonitorStateException
 *
 * Object.notifyAll()
 * 1、唤醒所有在此对象上等待的线程，被唤醒的线程要重新获取对象锁才能继续运行
 * 2、如果没有提前获取对象锁，调用此方法会抛出 IllegalMonitorStateException
 */
public class WaitDemo {

    private final Object object = new Object();

    private Runnable wait = () -> {
        synchronized (object) {
            System.out.println(Thread.currentThread().getName() + " 开始");
            try {
                System.out.println(Thread.currentThread().getName() + " 进入等待");
                object.wait();
                System.out.println(Thread.currentThread().getName() + " 重新开始执行");
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " 等待期间被要求中断");
            }
            System.out.println(Thread.currentThread().getName() + " 结束");
        }
    };

    // 3s
    private Runnable timedWait = () -> {
        synchronized (object) {
            System.out.println(Thread.currentThread().getName() + " 开始");
            try {
                System.out.println(Thread.currentThread().getName() + " 进入等待");
                object.wait(3 * 1000);
                System.out.println(Thread.currentThread().getName() + " 重新开始执行");
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " 等待期间被要求中断");
            }
            System.out.println(Thread.currentThread().getName() + " 结束");
        }
    };

    private Runnable notify = () -> {
        synchronized (object) {
            System.out.println(Thread.currentThread().getName() + " 开始");
            object.notify();
            System.out.println(Thread.currentThread().getName() + " 唤醒一个线程");
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 结束");
        }
    };

    private Runnable notifyAll = () -> {
        synchronized (object) {
            System.out.println(Thread.currentThread().getName() + " 开始");
            object.notifyAll();
            System.out.println(Thread.currentThread().getName() + " 唤醒全部线程");
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 结束");
        }
    };

    /**
     * 一个线程进入睡眠，然后等待唤醒后继续执行
     *
     * 线程一在运行过程中主动进入等待状态并释放对象锁
     * 线程二获取到对象锁开始运行
     * 线程二唤醒线程一
     * 线程一被唤醒，尝试获取对象锁
     * 线程二睡眠 2s 后运行结束，释放对象锁
     * 线程一获取到了线程锁，继续运行
     */
    @Test
    public void test01() throws InterruptedException {
        Thread thread1 = new Thread(wait, "线程一");
        Thread thread2 = new Thread(notify, "线程二");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }


    /**
     * 两个线程进入睡眠，然后只唤醒一次。没有被唤醒的线程将会永久等待
     */
    @Test
    public void test02() throws InterruptedException {
        Thread thread1 = new Thread(wait, "线程一");
        Thread thread2 = new Thread(wait, "线程二");
        thread1.start();
        thread2.start();

        Thread.sleep(500);
        Thread thread3 = new Thread(notify, "线程三");
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
    }


    /**
     * 两个线程进入睡眠，然后最后一个线程唤醒所有等待线程。
     * 如果出现线程三唤醒所有线程时线程一或线程二还没有进入等待状态，那么线程一或线程二后面进入等待状态后将会一直持续。
     */
    @Test
    public void test03() throws InterruptedException {
        Thread thread1 = new Thread(wait, "线程一");
        Thread thread2 = new Thread(wait, "线程二");
        thread1.start();
        thread2.start();

        Thread.sleep(500);
        Thread thread3 = new Thread(notifyAll, "线程三");
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
    }


    /**
     * 计时等待的线程可以被立刻唤醒，如果超过时间没有被唤醒，会自己结束等待状态
     */
    @Test
    public void test04() throws InterruptedException {
        Thread thread1 = new Thread(timedWait, "线程一");
        thread1.start();

        Thread.sleep(500);
        synchronized (object) {
            System.out.println("主线程 唤醒所有线程");
            object.notifyAll();
        }

        Thread thread2 = new Thread(timedWait, "线程二");
        thread2.start();

        thread1.join();
        thread2.join();
    }


    /**
     * 等待状态的线程要做好响应中断异常的准备
     */
    @Test
    public void test05() throws InterruptedException {
        Thread thread1 = new Thread(wait, "线程一");
        thread1.start();

        Thread.sleep(500);
        thread1.interrupt();

        thread1.join();
    }


    /**
     * 无论是调用 wait 方法，还是调用 notify 或 notifyAll 方法，如果没提前获取到对象锁，调用会抛出 IllegalMonitorStateException 异常
     */
    @Test
    public void test06() {
        try {
            object.wait();
        } catch (Exception e) {
            e.printStackTrace();
        }

        object.notify();
    }

}
