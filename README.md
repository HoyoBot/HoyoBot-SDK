<div align="center">

[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/HoyoBot/HoyoBot-SDK/blob/main/README.md)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/HoyoBot/HoyoBot-SDK/blob/main/LICENSE)

[English](README.md) | [简体中文](README_CH.md)

</div>


<div align="center">

# HoyoSDK

</div>

- HoyoSDK 是一个在全平台下运行，提供 米游社大别野 协议支持的高效率机器人库
- 本项目仍在开发中,请等待正式版再在生产环境中使用

- 如果你支持这个项目,请给我们一个star. 我们很欢迎社区的贡献

---------

## 特性

- 基于Netty|高性能|易开发
- 开源的|跨平台|快速开发插件

---------

## 相关链接

###### 下载

* [Jenkins (实时构建)](https://ci.lanink.cn/job/HoyoBot-SDK/)

###### 反馈问题

* [Issues/Tickets](https://github.com/HoyoBot/HoyoBot-SDK/issues)

###### Developers

* [License (GPLv3)](https://github.com/HoyoBot/HoyoBot-SDK/blob/main/LICENSE)

## 安装 & 运行

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

## 构建Jar文件

#### 环境: Kotlin | Java (8|17)

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
        <version>beta3</version>
    </dependency>
</dependencies>
```

## 机器人事件说明

HoyoBot将机器人发生的一切都处理为了事件,若你要开发其插件，只需要注册监听器，
就可以让事件触发时执行你的插件代码

你可以在 sdk-api 中查看样例代码

### 事件列表

- `ProxyBotStartEvent` - 机器人启动事件
- `ProxyBotStopEvent` - 机器人关闭事件
- `ProxyPluginEnableEvent` - 机器人插件加载事件

---------