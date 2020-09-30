package cn.xxywithpq.lock;


import cn.xxywithpq.SimplifyLockSpringBootStarterApplication;
import cn.xxywithpq.limiter.token.bucket.TokenBucketLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimplifyLockSpringBootStarterApplication.class)
public class TokenBucketLimiterTest {

    @Autowired
    TokenBucketLimiter TokenBucketLimiter;

    /**
     * 带分布式锁
     */
    @Test
    public void main() throws InterruptedException {
        int num = 5000;
        CountDownLatch countDownLatch = new CountDownLatch(num);

        int[] count = new int[]{0};
        for (int i = 0; i < num; i++) {
//            Thread.sleep(1000);
            new Thread(() -> {
                try {
                    if (TokenBucketLimiter.isActionAllowed("test", "TokenBucketTest", 10, 999)) {
                        log.info("我被获准进来啦 {}", Thread.currentThread().getId());
                        int i1 = count[0];
                        Thread.sleep(500);
                    }

                } catch (Exception e) {
                    log.error("FunnelTest e {}", e);
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }

        countDownLatch.await();
    }


}
