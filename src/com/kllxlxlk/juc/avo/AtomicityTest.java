package com.kllxlxlk.juc.avo;

/**
 * 测试 java 语言的原子性
 * 此程序只有在 32 位虚拟机上才可以复现异常
 *
 * cpu 需要将修改过的数据刷新到内存中，但每次刷新要写入内存两次，这就破坏了原子性的要求。
 * 假设这个数据是从 111 改为 -999。
 * 另一个线程在刷新数据的过程中从内存读取该数据，可能会读到 111 的高 32 位和 -999 的低 32 位。
 */
public class AtomicityTest {

    public static long aLong = 0;

    public static class Write implements Runnable {
        private long to;

        public Write(long to) {
            this.to = to;
        }

        @Override
        public void run() {
            AtomicityTest.aLong = to;
        }
    }

    public static class Check implements Runnable {
        @Override
        public void run() {
            while (true) {
                long temp = AtomicityTest.aLong;
                if (temp != 0 && temp != 111 && temp != -999 && temp != 333 && temp != -444) {
                    System.out.println(temp);
                }
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new Check()).start();
        new Thread(new Write(111)).start();
        new Thread(new Write(-999)).start();
        new Thread(new Write(333)).start();
        new Thread(new Write(-444)).start();
    }
}
