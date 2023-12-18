package com.mexx.comfy.properties;

import io.smallrye.config.ConfigMapping;

/**
 * Description: 文件配置 .<br>
 * <p>Created Time: 2023/12/18 13:47 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@ConfigMapping(prefix = "file.storage")
public interface FileProperties {
    String type();

    String view();

    String disk();
}
