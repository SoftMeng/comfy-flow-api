package com.mexx.comfy.resource;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.Lists;
import com.mexx.comfy.entity.ComfyPromptTask;
import com.mexx.comfy.exception.BadException;
import com.mexx.comfy.model.ComfyImageVo;
import com.mexx.comfy.model.ComfyPromptResp;
import com.mexx.comfy.model.FlowVo;
import com.mexx.comfy.model.PushVo;
import com.mexx.comfy.model.Result;
import com.mexx.comfy.properties.ComfyProperties;
import com.mexx.comfy.service.FileStorage;
import com.mexx.comfy.utils.ClientUtils;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartEntityPartWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.benmanes.caffeine.cache.Caffeine.newBuilder;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Description:  .<br>
 * <p>Created Time: 2023/12/14 11:09 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@Path("/comfyui")
public class ComfyResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComfyResource.class);
    private static final Cache<String, List<FlowVo>> CACHE = newBuilder()
        .expireAfterWrite(30, MINUTES)
        .softValues()
        .build();
    @Inject ComfyProperties comfyProperties;
    @Inject FileStorage fileStorage;

    @GET
    @Path("/flow/list")
    @PermitAll
    @Operation(summary = "都有哪些工作流")
    @Produces(MediaType.APPLICATION_JSON)
    public Result<List<FlowVo>> list() {
        return Result.ok(getFlowVos());
    }

    @POST
    @Path("/allin")
    @PermitAll
    @Operation(summary = "全流程")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Result<List<ComfyImageVo>> allin(@MultipartForm PushVo pushVo) {
        Result<ComfyPromptResp> comfyPromptRespResult = push(pushVo);
        ComfyPromptResp comfyPromptResp = comfyPromptRespResult.getResult();
        String promptId = comfyPromptResp.getPrompt_id();
        PanacheQuery<ComfyPromptTask> panacheQuery = ComfyPromptTask.find("promptId=?1", promptId);
        ComfyPromptTask comfyPromptTask = panacheQuery.firstResult();
        final String comfyIp = comfyPromptTask.comfyIp;
        List<ComfyImageVo> comfyImageVos = JSONUtil.toList(comfyPromptTask.result, ComfyImageVo.class);
        for (int i = 0; i <= 80; i++) {
            ThreadUtil.sleep(2000);
            if (CollUtil.isEmpty(comfyImageVos)) {
                comfyImageVos = getComfyViews(comfyIp, promptId);
                if (CollUtil.isNotEmpty(comfyImageVos)) {
                    comfyPromptTask.result = JSONUtil.toJsonStr(comfyImageVos);
                    comfyPromptTask.persistAndFlush();
                    return Result.ok(comfyImageVos);
                }
            }
        }
        throw new BadException("超时啦");
    }


    @POST
    @Path("/push")
    @PermitAll
    @Operation(summary = "推送工作流任务到服务端")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Result<ComfyPromptResp> push(@MultipartForm PushVo pushVo) {
        // TODO 可以扩展限流的规则
        LOGGER.info("推送工作流任务到服务端:{},{}", pushVo.getOpenid(), pushVo.getFlowName());
        List<FlowVo> flowVos = getFlowVos();
        String json = getFlow(pushVo.getFlowName(), flowVos);
        final String comfyIp = getComfyIp();
        final long send = RandomUtil.randomLong(1L, 9_744_073_709_551_614L);
        final String image = uploadComfyFile(comfyIp, pushVo.getFile());
        final String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        final String localTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        json = StrUtil.replace(json, "___prompt___", pushVo.getPrompt());
        json = StrUtil.replace(json, "___negative_prompt___", pushVo.getNegativePrompt());
        json = StrUtil.replace(json, "___seed___", String.valueOf(send));
        json = StrUtil.replace(json, "___localDate___", localDate);
        json = StrUtil.replace(json, "___localTime___", localTime);
        json = StrUtil.replace(json, "___key___", localTime);
        if (Objects.nonNull(image)) {
            json = StrUtil.replace(json, "___image___", image);
        }
        String postJson = StrUtil.format(
            """
                {
                    "client_id": {},
                    "prompt": {}
                }
                """, pushVo.getOpenid(), json
        );
        LOGGER.info("ComfyUI-提交任务-请求:{}", postJson);
        Response response = null;
        try (Client client = ClientUtils.buildClient()) {
            Invocation.Builder builder = client.target("http://" + comfyIp + ":8188/prompt")
                .request(MediaType.APPLICATION_JSON);
            response = builder.post(Entity.entity(postJson, MediaType.APPLICATION_JSON));
            String str = response.readEntity(String.class);
            LOGGER.info("ComfyUI-提交任务-返回:{},{}", response.getStatus(), str);
            ClientUtils.check200(response);
            ComfyPromptResp resp = JSONUtil.toBean(str, ComfyPromptResp.class);
            ComfyPromptTask comfyPromptTask = new ComfyPromptTask();
            comfyPromptTask.comfyIp = comfyIp;
            comfyPromptTask.openid = pushVo.getOpenid();
            comfyPromptTask.flowName = pushVo.getFlowName();
            comfyPromptTask.prompt = pushVo.getPrompt();
            comfyPromptTask.image = image;
            comfyPromptTask.promptId = resp.getPrompt_id();
            comfyPromptTask.number = resp.getNumber();
            comfyPromptTask.persistAndFlush();
            return Result.ok(resp);
        } catch (Exception e) {
            LOGGER.error("推送工作流任务到服务端失败", e);
            throw new BadException("推送工作流任务到服务端失败:" + e.getMessage());
        } finally {
            if (Objects.nonNull(response)) {
                response.close();
            }
        }
    }

    private String getFlow(String flowName, List<FlowVo> flowVos) {
        FlowVo flow = flowVos.stream()
            .filter(flowVo -> StrUtil.equals(flowName, flowVo.getName()))
            .findFirst()
            .orElse(null);
        if (Objects.isNull(flow)) {
            throw new BadException("工作流不存在");
        }
        String flowPath = flow.getPath();
        File file = FileUtil.file(flowPath);
        if (FileUtil.exist(file)) {
            String json = FileUtil.readUtf8String(file);
            if (StrUtil.isBlank(json)) {
                throw new BadException("工作流文件内容为空");
            }
            return json;
        }
        return ResourceUtil.readUtf8Str(flowPath);
    }

    @GET
    @Path("/push/result")
    @PermitAll
    @Operation(summary = "推送工作流任务的返回结果")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Result<List<ComfyImageVo>> get(@QueryParam("prompt_id") String prompt_id) {
        PanacheQuery<ComfyPromptTask> panacheQuery = ComfyPromptTask.find("promptId=?1", prompt_id);
        ComfyPromptTask comfyPromptTask = panacheQuery.firstResult();
        final String comfyIp = comfyPromptTask.comfyIp;
        List<ComfyImageVo> comfyImageVos = JSONUtil.toList(comfyPromptTask.result, ComfyImageVo.class);
        if (CollUtil.isEmpty(comfyImageVos)) {
            comfyImageVos = getComfyViews(comfyIp, prompt_id);
            if (CollUtil.isNotEmpty(comfyImageVos)) {
                comfyPromptTask.result = JSONUtil.toJsonStr(comfyImageVos);
                comfyPromptTask.persistAndFlush();
            }
        }
        return Result.ok(comfyImageVos);
    }

    private List<ComfyImageVo> getComfyViews(String comfyIp, String prompt_id) {
        List<ComfyImageVo> comfyImageVos = Lists.newArrayList();
        Response response = null;
        try (Client client = ClientUtils.buildClient()) {
            Invocation.Builder builder = client.target("http://" + comfyIp + ":8188/history/" + prompt_id)
                .request(MediaType.APPLICATION_JSON);
            response = builder.get();
            String respStr = response.readEntity(String.class);
            LOGGER.info("ComfyUI-查询进度-返回:{},{}", response.getStatus(), respStr);
            ClientUtils.check200(response);
            JSONObject resp = JSONUtil.parseObj(respStr);
            if (resp.isEmpty()) {
                return Lists.newArrayList();
            }
            JSONObject body = resp.getJSONObject(prompt_id);
            if (Objects.isNull(body) || body.isEmpty()) {
                return Lists.newArrayList();
            }
            JSONObject outputs = body.getJSONObject("outputs");
            outputs.forEach(o -> {
                if (o.getValue() instanceof JSONObject imageBody) {
                    if (imageBody.containsKey("images")) {
                        imageBody.getJSONArray("images")
                            .stream()
                            .forEach(image -> {
                                if (image instanceof JSONObject imageJson) {
                                    final String subfolder = imageJson.getStr("subfolder");
                                    final String filename = imageJson.getStr("filename");
                                    final String type = imageJson.getStr("type");
                                    final String imageUrl = getImageUrl(comfyIp, subfolder, filename, type);
                                    final String viewUrl = fileStorage.upload(imageUrl, filename);
                                    ComfyImageVo comfyImageVo = new ComfyImageVo();
                                    comfyImageVo.setSubfolder(subfolder);
                                    comfyImageVo.setName(filename);
                                    comfyImageVo.setType(type);
                                    comfyImageVo.setComfyImageUrl(imageUrl);
                                    comfyImageVo.setViewUrl(viewUrl);
                                    comfyImageVos.add(comfyImageVo);
                                }
                            });
                    }
                }
            });
            return comfyImageVos;
        } catch (Exception e) {
            LOGGER.error("查询进度失败", e);
            throw new BadException("查询进度失败:" + e.getMessage());
        } finally {
            if (Objects.nonNull(response)) {
                response.close();
            }
        }
    }

    /**
     * 上传图片到ComfyUI.
     *
     * @param comfyIp .
     * @param file    .
     * @return .
     */
    private String uploadComfyFile(String comfyIp, InputPart file) {
        if (Objects.isNull(file) || StrUtil.isBlank(file.getFileName())) {
            LOGGER.info("无图片上传");
            return null;
        }
        LOGGER.info("图片: {}", file.getFileName());
        Response response = null;
        try (Client client = ClientUtils.buildClient()) {
            client.register(MultipartEntityPartWriter.class);
            WebTarget target = client.target("http://" + comfyIp + ":8188/upload/image");
            EntityPart entityPart = EntityPart.withName("image")
                .fileName(file.getFileName())
                .content(file.getBody())
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .build();
            GenericEntity genericEntity = new GenericEntity<>(Lists.newArrayList(entityPart)) {
            };
            Entity entity = Entity.entity(genericEntity, MediaType.MULTIPART_FORM_DATA);
            response = target.request(MediaType.MULTIPART_FORM_DATA).post(entity);
            String str = response.readEntity(String.class);
            LOGGER.info("ComfyUI-上传图片-返回:{},{}", response.getStatus(), str);
            ClientUtils.check200(response);
            ComfyImageVo comfyImageVo = JSONUtil.toBean(str, ComfyImageVo.class);
            LOGGER.info("上传文件成功:{}", comfyImageVo.getName());
            return comfyImageVo.getName();
        } catch (Exception e) {
            LOGGER.error("上传图片失败", e);
            throw new BadException("上传图片失败:" + e.getMessage());
        } finally {
            if (Objects.nonNull(response)) {
                response.close();
            }
        }
    }

    public List<FlowVo> getFlowVos() {
        // TODO 暂时用文件存储，如果有管理平台，可以基于管理平台进行更多的管理
        List<FlowVo> result = CACHE.get("_FLOW", k -> {
            String flows = ResourceUtil.readUtf8Str("comfyui.json");
            return JSONUtil.toList(flows, FlowVo.class);
        });
        return result;
    }

    private String getComfyIp() {
        // TODO 随机分配IP，如果需要更好的算力分配方法，可以在这里编写.
        List<String> ips = comfyProperties.ips();
        String ip = RandomUtil.randomEle(ips);
        return ip;
    }

    private String getImageUrl(String comfyIp, String subfolder, String filename, String type) {
        // TODO 这里的图片可以存储到云上
        return StrUtil.format(
            "http://{}:8188/view?filename={}&subfolder={}&type={}",
            comfyIp, filename, subfolder, type
        );
    }
}
