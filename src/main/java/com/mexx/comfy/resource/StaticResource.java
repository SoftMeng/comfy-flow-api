package com.mexx.comfy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

/**
 * Description: 静态资源 .<br>
 * <p>Created Time: 2023/12/18 11:55 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Path("/")
public class StaticResource {
    @GET
    @Path("{file}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getFile(@PathParam("file") String file) {
        try {
            java.nio.file.Path path = Paths.get("static/" + file);
            byte[] fileData = Files.readAllBytes(path);
            return Response.ok(fileData).build();
        } catch (IOException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
