package com.mexx.comfy.config;

import io.smallrye.config.ConfigMapping;

/**
 * Description: C端DNS的信息 .<br>
 * <p>Created Time: 2022/8/31 10:29 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@ConfigMapping(prefix = "wechat")
public interface WechatProperties {
    String appid();

    String secret();
}
