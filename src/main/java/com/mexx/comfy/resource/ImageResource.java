package com.mexx.comfy.resource;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.mexx.comfy.entity.ComfyPromptTask;
import com.mexx.comfy.model.ComfyImageVo;
import com.mexx.comfy.model.ImageVo;
import com.mexx.comfy.model.Result;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * Description:  .<br>
 * <p>Created Time: 2024/3/30 10:39 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Path("/api/images")
public class ImageResource {

    @GET
    @PermitAll
    @Operation(summary = "图片搜索")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Result<List<ImageVo>> get(@QueryParam("keyword") String keyword) {
        if (StrUtil.isBlank(keyword)) {
            keyword = "";
        }
        PanacheQuery<ComfyPromptTask> panacheQuery = ComfyPromptTask.find(
            "prompt like ?1", Sort.descending("id"), "%" + keyword + "%");
        List<ComfyPromptTask> comfyPromptTasks = panacheQuery.page(Page.of(0, 65)).list();
        List<ImageVo> images = Lists.newArrayList();
        comfyPromptTasks.forEach(comfyPromptTask -> {
            List<ComfyImageVo> comfyImageVos = JSONUtil.toList(comfyPromptTask.result, ComfyImageVo.class);
            comfyImageVos.forEach(comfyImageVo -> {
                ImageVo image = new ImageVo();
                image.setId(comfyPromptTask.id);
                image.setFlowName(comfyPromptTask.flowName);
                image.setPrompt(comfyPromptTask.prompt);
                image.setName(comfyImageVo.getName());
                image.setViewUrl(comfyImageVo.getViewUrl());
                if (StrUtil.isNotBlank(image.getViewUrl())) {
                    images.add(image);
                }
            });
        });
        return Result.ok(images);
    }
}
