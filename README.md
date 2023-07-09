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
- 下载最新版构建 `NoCheatPlus-1.0-SNAPSHOT-jar-with-dependencies.jar`
- (跳转链接): [CI](https://ci.lanink.cn/job/HoyoBot-SDK/)
- 将它放进你的服务器
- 使用命令 `java -jar (下载的文件名)` 即可运行

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

|  ArtifactId  |          Version           |
|:------------:|:--------------------------:|
|   HoyoBot    |            beta            |

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
        <artifactId>HoyoBot</artifactId>
        <version>beta</version>
    </dependency>
</dependencies>
```

---------