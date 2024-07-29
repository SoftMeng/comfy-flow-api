package com.mexx.comfy.job;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mexx.comfy.exception.BadException;
import com.mexx.comfy.model.ComfyImageVo;
import com.mexx.comfy.model.FlowVo;
import com.mexx.comfy.model.PushVo;
import com.mexx.comfy.model.Result;
import com.mexx.comfy.properties.ComfyProperties;
import com.mexx.comfy.resource.ComfyResource;
import com.mexx.comfy.utils.ClientUtils;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:  .<br>
 * <p>Created Time: 2023/10/15 19:14 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
@ApplicationScoped
public class RandomPromptJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomPromptJob.class);
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);
    private static boolean running = false;
    @Inject
    private ComfyResource comfyResource;
    @Inject
    private ComfyProperties comfyProperties;

    @Scheduled(every = "5s")
    public void runRandomPrompt() {
        if (!comfyProperties.job()) {
            return;
        }
        if (running) {
            return;
        }
        running = true;
        Response response = null;
        List<FlowVo> flowVos = comfyResource.getFlowVos();
        try (Client client = ClientUtils.buildClient()) {
            Invocation.Builder builder = client.target("https://jmai.art/api/prompt/make")
                .request(MediaType.APPLICATION_JSON);
            response = builder.get();
            String str = response.readEntity(String.class);
            LOGGER.info("Random AI Prompt-返回:{},{}", response.getStatus(), str);
            ClientUtils.check200(response);
            JSONObject resp = JSONUtil.parseObj(str);
            JSONArray jsonArray = resp.getJSONArray("data");
            jsonArray.forEach(o -> {
                if (o instanceof JSONObject object) {
                    int no = atomicInteger.getAndAdd(1);
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    String prompt = object.getStr("prompt");
                    PushVo pushVo = new PushVo();
                    pushVo.setFlowName(RandomUtil.randomEle(flowVos).getName());
                    pushVo.setPrompt(prompt);
                    LOGGER.info("[No.{}] - Prompt: {}", no, pushVo.getPrompt());
                    LOGGER.info("[No.{}] - 工作流: {}", no, pushVo.getFlowName());
                    Result<List<ComfyImageVo>> listResult = comfyResource.allin(pushVo);
                    stopWatch.stop();
                    listResult.getResult().forEach(comfyImageVo -> {
                        LOGGER.info("[No.{}] - View Url: {}", no, comfyImageVo.getViewUrl());
                    });
                    LOGGER.info("[No.{}] - 耗时: {}", no, stopWatch.getTotalTimeSeconds());
                }
            });
        } catch (Exception e) {
            LOGGER.error("Random AI Prompt", e);
            throw new BadException("Random AI Prompt:" + e.getMessage());
        } finally {
            running = false;
            if (Objects.nonNull(response)) {
                response.close();
            }
        }
    }
}
