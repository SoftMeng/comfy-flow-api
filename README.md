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

这是一个ComfyUI的API聚合项目，针对ComfyUI的API进行了封装，比较适用的场景如下

1. 给微信小程序提供AI绘图的API;
2. 封装大模型的统一API调用平台,实现模型多台服务器的负载均衡;
3. 启用JOB，可以在本地自动生成AI图片，生成本地的图片展览馆;
4. 定制不同的工作流，通过API进行调用;

依赖：
JDK 17 +
Maven 3.8.6 +

## 配置文件解释

### application.properties 配置文件

我们需要修改如下配置

#### 微信小程序配置（如果需要小程序微信登陆时，需要配置）

```
wechat.appid=小程序 appId
wechat.secret=小程序 appSecret
```
#### ComfyUI服务的Ip(如果想使用ComfyUI进行图像生成时，需要配置)
```
comfy.ips=ComfyUI所在服务器的IP，比如`127.0.0.1`
```
#### 生成的图片的存储地址(默认是放到磁盘里，我的磁盘地址是`/Users/xiangyuanmeng/Documents/MyProjectSpace/comfy-flow-api/static/`，需要替换)
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

### comfyui.json 配置文件

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

工作流文件中，某些需要替换的参数需要替换，可以参考：[角色设计XL-简单的二次元.json](src/main/resources/comfy/%E8%A7%92%E8%89%B2%E8%AE%BE%E8%AE%A1XL-%E7%AE%80%E5%8D%95%E7%9A%84%E4%BA%8C%E6%AC%A1%E5%85%83.json)

目前支持：

1. ___seed___  随机种子
2. ___prompt___ 提示词
3. ___negative_prompt___ 反向提示词
4. ___image___ 图片
5. ___localDate___ 日期
6. ___localTime___ 时间
7. ___key___ 关键字

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

1. 模型: https://www.liblib.art/userpage/0f37258e861044879b8728309c0ac3b0/publish
2. 插件: https://github.com/SoftMeng/

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

![step4.png](doc%2Fstep4.png)

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

### 8. 访问本地生成的图片

访问: http://localhost:8189/lib.html 点击搜索

![lib.png](doc%2Flib.png)

