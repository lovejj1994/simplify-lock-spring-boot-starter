package cn.xxywithpq.lock;


import cn.xxywithpq.SimplifyLockSpringBootStarterApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimplifyLockSpringBootStarterApplication.class)
public class WithOutLockTest {

    /**
     * 带分布式锁
     */
    @Test
    public void main() throws InterruptedException {
        int num = 20;
        CountDownLatch countDownLatch = new CountDownLatch(num);

        int[] count = new int[]{0};
        for (int i = 0; i < num; i++) {
            new Thread(() -> {
                try {
                    int i1 = count[0];
                    Thread.sleep(100);
                    count[0] = i1 + 1;
                } catch (Exception e) {
                    log.error("SimplifyLockTest e {}", e);
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }

        countDownLatch.await();
        log.info("result {}", count[0]);
        assertEquals(num, count[0]);
    }
}
