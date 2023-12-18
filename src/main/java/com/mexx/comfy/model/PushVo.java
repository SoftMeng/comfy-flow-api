package com.mexx.comfy.model;

import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

/**
 * Description: 上传 .<br>
 * <p>Created Time: 2023/12/14 11:24 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Setter
@Getter
public class PushVo {
    @FormParam
    @SchemaProperty(name = "用户ID")
    @PartType(MediaType.TEXT_PLAIN)
    private String openid;
    @FormParam
    @SchemaProperty(name = "工作流的名称")
    @PartType(MediaType.TEXT_PLAIN)
    private String flowName;
    @FormParam
    @SchemaProperty(name = "参数")
    @PartType(MediaType.TEXT_PLAIN)
    private String prompt;
    @FormParam("file")
    @SchemaProperty(name = "图片文件")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputPart file;
}
