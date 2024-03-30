package com.mexx.comfy.resource;

import cn.hutool.core.io.FileUtil;
import com.mexx.comfy.properties.FileProperties;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

/**
 * Description: 静态资源 .<br>
 * <p>Created Time: 2023/12/18 11:55 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Path("/static")
public class StaticResource {
    @Inject FileProperties fileProperties;

    @GET
    @Path("{file}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getFile(@PathParam("file") String file) {
        try {
            File targetFile = FileUtil.file(fileProperties.disk() + file);
            return Response.ok(targetFile).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
