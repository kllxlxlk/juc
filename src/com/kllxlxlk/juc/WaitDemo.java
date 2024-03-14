package com.kllxlxlk.juc;


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
 *
 * Object.notifyAll()
 */
public class WaitDemo {

    public static final Object object = new Object();

    /**
     *
     * @throws InterruptedException
     */
    @Test
    public void test0331() {
        Thread thread1 = new Thread(() -> {

                System.out.println(Thread.currentThread().getName() + " 开始");
                try {
                    System.out.println(Thread.currentThread().getName() + " 等待");
                    object.wait(3 * 1000);
                    System.out.println(Thread.currentThread().getName() + " 重新开始执行");
                } catch (InterruptedException e) {

                }
                System.out.println(Thread.currentThread().getName() + " 结束");

        }, "线程一");

        thread1.start();
        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test01() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            synchronized (object) {
                System.out.println(Thread.currentThread().getName() + " 开始");
                try {
                    System.out.println(Thread.currentThread().getName() + " 等待");
                    object.wait();
                    System.out.println(Thread.currentThread().getName() + " 重新开始执行");
                } catch (InterruptedException e) {

                }
                System.out.println(Thread.currentThread().getName() + " 结束");
            }
        }, "线程一");

        Thread thread2 = new Thread(() -> {
            synchronized (object) {
                System.out.println(Thread.currentThread().getName() + " 开始");
                object.notify();
                System.out.println(Thread.currentThread().getName() + " 唤醒一个线程");
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {

                }
                System.out.println(Thread.currentThread().getName() + " 结束");
            }
        }, "线程二");

        thread1.start();
        thread2.start();

        thread1.join();
        thread1.join();
    }

}
