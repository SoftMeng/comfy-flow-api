package com.mexx.comfy.model;

import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

/**
 * Description: 文件 .<br>
 * <p>Created Time: 2022/8/10 11:43 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
public class FormData {
    @FormParam("fileName")
    @PartType(MediaType.TEXT_PLAIN)
    public String fileName;
    @FormParam
    @PartType(MediaType.TEXT_PLAIN)
    public String description;
}
