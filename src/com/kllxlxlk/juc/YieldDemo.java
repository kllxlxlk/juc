package com.kllxlxlk.juc;

import org.junit.Test;

/**
 * 线程放弃对 CPU 的占用，然后重新和别的线程一起竞争 CPU。
 * 如果线程处理时间比较长，可以在执行的过程中让出 CPU，给别的线程一个执行的机会。
 */
public class YieldDemo {

    @Test
    public void test01() {
        Runnable runnable = () -> {
            for (int i = 0; i < 100; i++) {
                // 一个比较长的处理逻辑
                System.out.println("假装我是一个耗时比较长的处理逻辑");
                // 每次处理完一个耗时比较长的处理逻辑后都让出 cpu，给别的线程一个抢占 cpu 的机会。
                Thread.yield();
            }
        };

        new Thread(runnable).start();
    }
}
