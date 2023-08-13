<div align="center">

[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/HoyoBot/HoyoBot-SDK/blob/main/README.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/HoyoBot/HoyoBot-SDK/blob/main/LICENSE)
[![Maven](https://jitpack.io/v/HoyoBot/HoyoBot-SDK.svg)](https://jitpack.io/#HoyoBot/HoyoBot-SDK)

</div>


<div align="center">

![HoyoBot-SDK](https://socialify.git.ci/HoyoBot/HoyoBot-SDK/image?description=1&descriptionEditable=%E5%85%A8%E5%B9%B3%E5%8F%B0%E7%B1%B3%E6%B8%B8%E7%A4%BE%E5%A4%A7%E5%88%AB%E9%87%8E%E5%8D%8F%E8%AE%AE%E6%94%AF%E6%8C%81%E9%AB%98%E6%95%88%E7%8E%87%E6%9C%BA%E5%99%A8%E4%BA%BA%E5%BA%93&font=Jost&forks=1&issues=1&language=1&logo=https%3A%2F%2Favatars.githubusercontent.com%2Fu%2F138961612%3Fs%3D400%26u%3Dd484bee5b3297f446682d6ec90c6e8d07fc86759%26v%3D4&name=1&owner=1&pattern=Plus&pulls=1&stargazers=1&theme=Light)

</br>

----

# HoyoSDK

HoyoSDK 是一个在全平台下运行，提供 米游社大别野 协议支持的高效率机器人库

这个项目的名字来源于
<p><a href = "https://www.mihoyo.com/">米哈游</a>英文名<a href = "https://www.mihoyo.com/?page=product">《miHoYo》</a>的后两部分(mi <b>HoYo</b>)</p>
<p>其含义是为米哈游旗下软件<a href = "https://www.miyoushe.com/">米游社</a>创造的项目<a href = "https://github.com/HoyoBot/HoyoBot-SDK">(<b>HoyoSDK</b>)</a></p>


</div>

- HoyoSDK 是一个在全平台下运行，提供 米游社大别野 协议支持的高效率机器人库
- 本项目仍在开发中,请等待正式版再在生产环境中使用

- 如果你支持这个项目,请给我们一个star. 我们很欢迎社区的贡献

---------

## 特性

- 基于Netty | 高性能| 易开发
- 开源的 |跨平台 | 快速开发插件

---------

## 相关链接

###### 开发者文档

* [HoyoBot-SDK 官方文档](https://sdk.catrainbow.me)

###### 下载

* [Jenkins (实时构建)](https://ci.lanink.cn/job/HoyoBot-SDK/)

###### 反馈问题

* [Issues/Tickets](https://github.com/HoyoBot/HoyoBot-SDK/issues)

###### 开发相关

* [License (GPLv3)](https://github.com/HoyoBot/HoyoBot-SDK/blob/main/LICENSE)
* [说明文档 (Docs)](https://github.com/HoyoBot/HoyoBot-SDK/blob/main/docs/README.md)

###### 官方插件
* [hoyo-sdk-mollyai](https://github.com/HoyoBot/hoyo-sdk-mollyai)

## 安装 & 运行

注意: 本框架仅支持 Java17 及以上版本的环境

- 从Java CI: https://ci.lanink.cn/job/HoyoBot-SDK/
- 下载最新版构建 `sdk-main-1.0.0-jar-with-dependencies.jar`
- (跳转链接): [CI](https://ci.lanink.cn/job/HoyoBot-SDK/)
- 将它放进你的服务器
- 使用命令 `java -jar (下载的文件名)` 即可运行

## 原生命令

HoyoBot自带的命有这些,当然你也可以通过插件注册自定义机器命令.你可以在sdk-api中学习怎么注册一个命令

- `version` - 查看机器人及HoyoSDK-Protocol协议版本
- `help` - 查看命令帮助
- `plugins` - 列出当前机器人安装的插件
- `reload` - 热重载机器人插件
- `getvilla` - 获取大别野信息
- `getmember` - 获取用户信息
- `getroom` - 获取房间信息
- `send` - 发送自定义消息

## 构建Jar文件

#### 环境: Kotlin | Java (17)

- `git clone https://github.com/HoyoBot/HoyoBot-SDK.git`
- `cd HoyoBot-SDK`
- `git submodule update --init`
- `./mvnw clean package`

* 构建好的文件能在目录 target/ directory 中找到.

## 部署开发环境

- HoyoBot的插件非常容易开发,这给你的机器人带来了无限的可能性
- 前往 sdk-api 查看 示例插件

### GroupId

- `com.github.HoyoBot.HoyoBot-SDK`

### Repository可用版本

| ArtifactId | Version |
|:----------:|:-------:|
|  sdk-main  |  beta   |
|  sdk-main  |  beta3  |
|  sdk-main  |  beta4  |
|  sdk-main  |  beta5  |
|  sdk-main  |  beta6  |
|  sdk-main  |  beta7  |
|  sdk-main  |  beta8  |
|  sdk-main  |  beta9  |
|  sdk-main  |  beta10 |

### Gradle:

```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.HoyoBot.HoyoBot-SDK:HoyoBot:beta'
	}
```

### Maven:

##### Repository:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

##### Dependencies:

```xml

<dependencies>
    <dependency>
        <groupId>com.github.HoyoBot.HoyoBot-SDK</groupId>
        <artifactId>ExamplePlugin</artifactId>
        <version>beta5</version>
    </dependency>
</dependencies>
```

## 协议支持

<details>

<summary>支持的协议列表</summary>

**米游社回调事件**

- 消息发送
- 图片发送
- 帖子发送
- 成员信息及列表获取
- 大别野信息及列表获取
- 踢除用户
- 消息回复
- 消息置顶
- 消息撤回

</details>

## 机器人事件说明

HoyoBot将机器人发生的一切都处理为了事件,若你要开发其插件，只需要注册监听器，
就可以让事件触发时执行你的插件代码

你可以在 sdk-api 中查看样例代码

### 事件列表

- `ProxyBotStartEvent` - 机器人启动事件
- `ProxyBotStopEvent` - 机器人关闭事件
- `ProxyPluginEnableEvent` - 机器人插件加载事件
- `VillaMemberJoinEvent` - 新成员加入频道事件
- `VillaSendMessageEvent` - 频道成员聊天事件

---------

[![Star History Chart](https://api.star-history.com/svg?repos=HoyoBot/HoyoBot-SDK&type=Date)](https://star-history.com/#HoyoBot/HoyoBot-SDK&Date)
