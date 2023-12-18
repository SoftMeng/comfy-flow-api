package com.mexx.comfy.utils;

import com.mexx.comfy.exception.BadException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 * Description:  .<br>
 * <p>Created Time: 2023/12/14 17:23 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
public class ClientUtils {
    /**
     * 创建Client.
     *
     * @return .
     */
    public static Client buildClient() {
        ClientBuilder clientBuilder = ResteasyClientBuilder.newBuilder()
            .connectTimeout(10000, TimeUnit.SECONDS);
        return clientBuilder.build();
    }

    public static void check200(Response response) {
        // Comfy返回异常的校验
        if (!Objects.equals(response.getStatus(), 200)) {
            throw new BadException("HTTP 调用返回错误信息");
        }
    }
}
