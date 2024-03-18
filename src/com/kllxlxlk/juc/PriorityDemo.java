package com.kllxlxlk.juc;

/**
 * 当出现资源竞争的时候，高优先级会更容易获取到资源，但这并不是一定的。
 * 比如下面的代码，经常会出现低优先级先执行完毕的情况。
 */
public class PriorityDemo {

    public static void main(String[] args) {
        Runnable runnable = () -> {
            int count = 0;
            while (true) {
                synchronized (PriorityDemo.class) {
                    count++;
                    if (count > 100000000) {
                        System.out.println(Thread.currentThread().getName() + " is complete");
                        break;
                    }
                }
            }
        };

        Thread highPriority = new Thread(runnable, "HighPriority");
        Thread mediumPriority = new Thread(runnable, "MediumPriority");
        Thread lowPriority = new Thread(runnable, "LowPriority");
        highPriority.setPriority(Thread.MAX_PRIORITY);
        mediumPriority.setPriority(Thread.NORM_PRIORITY);
        lowPriority.setPriority(Thread.MIN_PRIORITY);

        lowPriority.start();
        mediumPriority.start();
        highPriority.start();
    }

}
