package com.mexx.comfy.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * Description: InfoResource.java .<br>
 * <p>Created Time: 2023/12/14 10:06 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Path("/comfy-flow-api")
public class InfoResource {

    @GET
    @PermitAll
    @Operation(summary = "这是个项目描述")
    @Produces(MediaType.TEXT_PLAIN)
    public String info() {
        return "This project is a set of ComfyUI API";
    }
}
