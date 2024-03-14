package com.kllxlxlk.juc.avo.visibility;

/**
 * 此例子用来说明 java 语言的可见性
 *
 * 由于 cpu 缓存的问题，一个变量被修改后并不一定能立刻被其它线程发现。
 * 比如一个字符串变量已经被赋值为 banana，另一个线程在读取的时候，可能还是原来的值 apple。
 * 这是因为变量被修改后并不一定会立刻写回内存，可能从寄存器写到了 cpu 的缓存里，还没来得及刷新到内存中。
 * 这时另一个 cpu 在执行另一个线程的代码，从内存中读取到的仍然是 apple。
 *
 * 总的来说，一个线程修改变量后，别的线程并不一定能够立刻看见，这就是 cpu 缓存导致的可见性问题。
 *
 * 本实例在声明 instance 变量时加了 volatile 关键字。volatile 会告诉 cpu，如果修改了 instance 变量要立刻刷新到内存中，这样就
 * 解决了可见性问题。如果不加 volatile 关键字，有可能线程一已经完成了 instance 的创建，但线程二调用 getInstance() 方法，判断
 * instance 仍然等于 null。然后再次进行初始化。
 */
public class CpuCacheExample {

    private static volatile CpuCacheExample instance;

    private CpuCacheExample() {}

    public CpuCacheExample getInstance() {
        if (instance == null) {
            synchronized (CpuCacheExample.class) {
                if (instance == null) {
                    instance = new CpuCacheExample();
                }
            }
        }
        return instance;
    }

}
