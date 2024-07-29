package com.mexx.comfy.properties;

import io.smallrye.config.ConfigMapping;
import java.util.List;

/**
 * Description: C端DNS的信息 .<br>
 * <p>Created Time: 2022/8/31 10:29 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@ConfigMapping(prefix = "comfy")
public interface ComfyProperties {
    List<String> ips();

    Boolean job();
}
