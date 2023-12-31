package cn.hoyobot.example;

import cn.hoyobot.sdk.event.proxy.ProxyBotStartEvent;
import cn.hoyobot.sdk.event.proxy.ProxyBotStopEvent;
import cn.hoyobot.sdk.network.BotEntry;
import cn.hoyobot.sdk.network.protocol.mihoyo.Message;
import cn.hoyobot.sdk.network.protocol.mihoyo.MsgContentInfo;
import cn.hoyobot.sdk.network.protocol.type.TextType;
import cn.hoyobot.sdk.plugin.Plugin;
import cn.hoyobot.sdk.scheduler.Task;
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

        //建立一个延时执行任务,20为一秒
        //scheduleDelayedRepeatingTask: 延时定时循环任务
        //scheduleRepeatingTask: 定时循环任务
        //scheduleDelayedTask: 延时任务
        this.getBotProxy().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                Message senderMessage = new MsgContentInfo(message);
                getBotProxy().getBot().sendMessage(roomID, senderMessage, TextType.MESSAGE);
                getLogger().info(message);
            }
        }, 20 * 5);

        //注册一个监听器,监听SDK发来的事件
        this.getBotProxy().getEventManager().subscribe(ProxyBotStartEvent.class, this::onBotEnabled);

        //为机器人注册一个命令
        this.getBotProxy().getCommandMap().registerCommand(new ExampleCommand("example"));
    }

    public void onBotEnabled(ProxyBotStartEvent event) {
        //使用Bot在机器人启动时发送一个消息
        Message senderMessage = new MsgContentInfo(this.message);
        this.getBotProxy().getBot().sendMessage(this.roomID, senderMessage, TextType.MESSAGE);
    }

}
