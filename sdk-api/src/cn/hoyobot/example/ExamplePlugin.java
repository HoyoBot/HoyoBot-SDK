package cn.hoyobot.example;

import cn.hoyobot.sdk.event.proxy.ProxyBotStartEvent;
import cn.hoyobot.sdk.network.BotEntry;
import cn.hoyobot.sdk.network.protocol.mihoyo.Message;
import cn.hoyobot.sdk.network.protocol.mihoyo.MsgContentInfo;
import cn.hoyobot.sdk.network.protocol.type.TextType;
import cn.hoyobot.sdk.plugin.Plugin;
import cn.hoyobot.sdk.utils.Config;

import java.io.File;

public class ExamplePlugin extends Plugin {

    public BotEntry botEntry;
    private int roomID;
    private String message;

    @Override
    public void onEnable() {
        //获取SDK中的Bot
        this.botEntry = this.getBotProxy().getBot();

        //为你的插件生成一个配置文件
        this.saveResource("config.yml");
        Config config = new Config(this.getBotProxy().getPluginPath() + File.separator + this.getDescription().getName() + "/config.yml", Config.YAML);
        //从配置文件获取值
        this.roomID = config.getInt("room_id");
        this.message = config.getString("message");

        //注册一个监听器,监听SDK发来的事件
        this.getBotProxy().getEventManager().subscribe(ProxyBotStartEvent.class, this::onBotEnabled);
    }

    public void onBotEnabled(ProxyBotStartEvent event) {
        //使用Bot在机器人启动时发送一个消息
        Message senderMessage = new MsgContentInfo(this.message);
        this.getBotProxy().getBot().sendMessage(this.roomID, senderMessage, TextType.MESSAGE);
    }

}
