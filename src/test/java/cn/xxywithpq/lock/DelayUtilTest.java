package cn.xxywithpq.lock;


import cn.xxywithpq.delay.DelayUtil;
import cn.xxywithpq.delay.DelayUtilFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class DelayUtilTest {

    @Test
    public void test() throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            DelayUtil instance = DelayUtilFactory.getInstance(String.valueOf(finalI), 10, 10, 3, (x) -> test((Integer) x));
            instance.put(1);
        }

        Thread.sleep(1000000000);
    }


    public void test(Integer i) {
        log.info("delayUtil result {}", i);
    }


}
