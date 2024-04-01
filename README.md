<div align="center">
    <img src="./doc/icon.jpg" width="256px"/>
</div>
<div align="center">
    富强、民主、文明、和谐、合规
</div>
<div align="center">
    自由、平等、公正、法治、合法
</div>
<div align="center">
    爱国、敬业、诚信、友善、合理
</div>

# comfy-flow-api

这是一个ComfyUI的API聚合项目

1. 提供了工作流的API调用；
2. 提供了微信小程序的授权API；

## 配置

### 在 application.properties 文件中

我们需要修改如下配置

#### 微信小程序配置

```
wechat.appid=小程序 appId
wechat.secret=小程序 appSecret
```
#### ComfyUI服务的Ip
```
comfy.ips=ComfyUI所在服务器的IP，比如`127.0.0.1`
```
#### 生成的图片的存储地址
```
file.storage.type=disk
file.storage.disk=/Users/xiangyuanmeng/Documents/MyProjectSpace/comfy-flow-api/static/
file.storage.view=http://127.0.0.1:8189/static/
```
其中

1. 类型目前只支持本地磁盘，即`file.storage.type=disk`;
2. 生成的图片存储在磁盘上，通过`file.storage.disk=/xxx/xxx/`配置磁盘文件夹;
3. 生成的图片可以进行访问，通过`file.storage.view=`进行配置；

PS：若需要使用阿里云的对象存储、Aws的S3存储、SM.MS等其他存储方式，需要扩展[FileStorage.java](src%2Fmain%2Fjava%2Fcom%2Fmexx%2Fcomfy%2Fservice%2FFileStorage.java)进行实现。

### 在 comfyui.json 文件中

我们需要修改如下配置

```json
[
  {
    "name": "工作流的名称，比如动漫风格转换",
    "path": "在服务器上，动漫风格转换的workflow-api.json所在的位置，比如/comfyui/workflow-api.json"
  }
]
```

## 工作流修改

工作流文件中，某些需要替换的参数需要替换，可以参考：[二次元转真人_workflow_api.json](src%2Fmain%2Fresources%2Fcomfy%2F%E4%BA%8C%E6%AC%A1%E5%85%83%E8%BD%AC%E7%9C%9F%E4%BA%BA_workflow_api.json)

目前支持：seed、prompt、image

## 本地测试

### 使用到的组件清单

可以访问地址: http://127.0.0.1:8189/q/dev-ui/extensions

![img.png](doc%2Fimg_0.png)

### 接口API清单

可以访问地址: http://127.0.0.1:8189/q/swagger-ui/

![img_1.png](doc%2Fimg_1.png)

### 简单使用

可以访问地址: http://127.0.0.1:8189/drawNow.html

![img_3.png](doc%2Fimg_3.png)

这个工作流使用到的资源如下

1. 模型: https://civitai.com/models/217692/mexxldimsdxllcm2?modelVersionId=245340
2. 插件1: https://github.com/SoftMeng/ComfyUI_Mexx_Styler

只是个流程测试，没有进度条。

## 部署（Jar包形式）

对项目进行打包

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

打完包的程序在`target`中，安装JDK17或者21后，可以使用命令行启动

```shell
java -jar target/comfy-flow-api-runner.jar
```

## 部署（Native形式）

这需要你安装GraalVM

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

打包后可以使用下面命令启动

```shell
./target/comfy-flow-api-1.0.0-SNAPSHOT-runner
```

## 使用步骤

### 1. 导出ComfyUI的API格式工作流

[ComfyUI导出API格式工作流.mp4](doc%2Fvedio%2FComfyUI%E5%AF%BC%E5%87%BAAPI%E6%A0%BC%E5%BC%8F%E5%B7%A5%E4%BD%9C%E6%B5%81.mp4)

### 2. 放入到工程文件夹中

![step2.png](doc%2Fstep2.png)

### 3. 将工作流文件和名称的对应关系写入到comfyui.json文件中

![step3.png](doc%2Fstep3.png)

### 4. 在drawNow.html中修改为“我的工作流AAA”

![step4.png](doc%2Fstep4.png)![img.png](img.png)

### 5. 修改工作流API的参数

![step5.png](doc%2Fstep5.png)
![step6.png](doc%2Fstep6.png)

### 6. 启动工程

访问: http://localhost:8189/drawNow.html

![drawNow.png](doc%2FdrawNow.png)

### 7. 开启JOB

配置文件开启JOB, 在配置文件里开启

![job.png](doc%2Fjob.png)

启动JOB后可以看到日志，正在执行生图的任务

![jobresult.png](doc%2Fjobresult.png)

### 8. 访问生成的图片

访问: http://localhost:8189/lib.html

![lib.png](doc%2Flib.png)

