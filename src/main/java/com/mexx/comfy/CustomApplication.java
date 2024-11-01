package com.mexx.comfy;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Description: 启动 .<br>
 * <p>Created Time: 2024/7/29 上午11:33 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@QuarkusMain
public class CustomApplication {

    public static void main(String... args) {
        Quarkus.run(args);
    }
}
