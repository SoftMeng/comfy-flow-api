package com.mexx.comfy.config;

import com.mexx.comfy.exception.AuthException;
import com.mexx.comfy.model.Result;
import jakarta.annotation.Priority;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 异常处理 .<br>
 * <p>Created Time: 2022/1/29 下午2:02 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Provider
@Priority(20)
public class ExceptionMappers implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionMappers.class);
    @Context UriInfo info;

    @Override
    public Response toResponse(Throwable exception) {
        String query = info.getRequestUri().getQuery();
        String path = info.getRequestUri().getPath();
        String url = path + (Objects.isNull(query) ? "" : "?" + query);
        LOGGER.error("API Error:" + url + "\n", exception);
        Result<Object> result = Result.fail(exception);
        if (exception instanceof AuthException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(result)
                .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(result)
            .build();
    }
}
