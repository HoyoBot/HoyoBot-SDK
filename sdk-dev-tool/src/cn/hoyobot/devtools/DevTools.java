package cn.hoyobot.devtools;

import cn.hoyobot.devtools.command.CompilerCommand;
import cn.hoyobot.devtools.loader.PackageCompiler;
import cn.hoyobot.devtools.loader.SourcePluginLoader;
import cn.hoyobot.sdk.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DevTools extends Plugin {

    private static DevTools instance;

    public static DevTools getInstance() {
        return instance;
    }

    public final PackageCompiler packageCompiler = new PackageCompiler();
    public final SourcePluginLoader sourcePluginLoader = new SourcePluginLoader(this.getBotProxy());

    @Override
    public void onEnable() {
        instance = this;
        File data = new File(this.getDataPath(), "packed");
        if (!data.isDirectory() && !data.mkdirs()) {
            this.getLogger().error("在初始化创建文件夹的时候出现错误!");
        }
        for (File file : Objects.requireNonNull((new File(this.getDataPath() + "/")).listFiles())) {
            try {
                this.sourcePluginLoader.loadPlugin(file);
            } catch (Exception e) {
                this.getLogger().error("加载插件文件" + file.getName() + "时出错!", e);
            }
        }
        this.getBotProxy().getCommandMap().registerCommand(new CompilerCommand("compiler"));
    }

    public static List<File> listFolder(File input, String filter) {
        List<File> result = new ArrayList<>();
        if (input != null) {
            if (!input.isDirectory()) {
                if (filter.equals("") || input.toString().endsWith("." + filter)) result.add(input);
                return result;
            }
            for (File f : Objects.requireNonNull(input.listFiles()))
                result.addAll(listFolder(f, filter));
        }
        return result;
    }

    public static void removeFolder(File input, String filter) {
        if (input != null) {
            if (input.isFile()) {
                if (filter.equals("") || input.toString().endsWith("." + filter))
                    if (!input.delete()) getInstance().getLogger().pluginError("删除文件夹时出现错误!");
            } else if (input.isDirectory()) {
                for (File f : Objects.requireNonNull(input.listFiles()))
                    removeFolder(f, filter);
                if (!input.delete()) getInstance().getLogger().pluginError("删除文件夹时出现错误!");
            }
        }
    }

}
