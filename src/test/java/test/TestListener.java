package test;

import com.mytomcat.ContainerEvent;
import com.mytomcat.ContainerListener;

/**
 * 监听测试
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public class TestListener implements ContainerListener {

    @Override
    public void containerEvent(ContainerEvent event) {
        System.out.println(event);
    }
}
