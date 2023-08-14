package cn.hoyobot.devtools.command;

import cn.hoyobot.devtools.DevTools;
import cn.hoyobot.devtools.loader.SourcePluginLoader;
import cn.hoyobot.sdk.command.Command;
import cn.hoyobot.sdk.command.CommandSender;
import cn.hoyobot.sdk.command.CommandSettings;
import cn.hoyobot.sdk.command.ConsoleCommandSender;
import cn.hoyobot.sdk.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class CompilerCommand extends Command {

    private static final CommandSettings.Builder commandSetting = (new CommandSettings.Builder()).put("compiler make-plugin <action>", "开发者插件工具", new String[0]);

    public CompilerCommand(@NotNull String name) {
        super(name, commandSetting.build());
    }

    @Override
    public boolean onExecute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof ConsoleCommandSender)) return true;
        if ("make-plugin".equals(strings[0])) {
            try {
                if (strings.length < 2) {
                    return false;
                } else {
                    Plugin plugin = DevTools.getInstance().getBotProxy().getPluginManager().getPluginByName(strings[1]);
                    if (plugin == null) {
                        commandSender.sendMessage("[DevTool] 目标插件不存在!");
                    } else {
                        SourcePluginLoader loader = new SourcePluginLoader(DevTools.getInstance().getBotProxy());
                        File dir = loader.getPluginPath(plugin);
                        if (dir == null) {
                            commandSender.sendMessage("[DevTool] 无法找到插件路径");
                        } else {
                            File class_file = new File(dir.getAbsolutePath() + "/src_compile");
                            File jar_file = new File(DevTools.getInstance().getDataPath(), "packed/" + plugin.getName() + "_v" + plugin.getDescription().getVersion() + ".jar");
                            if (strings.length >= 3 && strings[2].equalsIgnoreCase("true")) {
                                commandSender.sendMessage("[DevTool] 反编译插件中...");
                            }
                            if (jar_file.exists()) {
                                if (!jar_file.isFile()) {
                                    commandSender.sendMessage("[DevTool] 插件已经存在但它是一个文件夹!");
                                    return true;
                                }
                                commandSender.sendMessage("[DevTool] 插件已经存在,将直接覆盖!");
                                if (!jar_file.delete()) {
                                    commandSender.sendMessage("[DevTool] 覆盖插件文件失败!");
                                    return true;
                                }
                            }
                            FileOutputStream file_stream = new FileOutputStream(jar_file);
                            Manifest manifest = new Manifest();
                            Attributes attr = manifest.getMainAttributes();
                            attr.putValue("Manifest-Version", "1.0.0");
                            attr.putValue("Signature-Version", "1.0.0");
                            attr.putValue("Created-By", "DevTool v" + DevTools.getInstance().getDescription().getVersion());
                            attr.putValue("Class-Path", plugin.getDescription().getMain());
                            JarOutputStream jar_stream = new JarOutputStream(file_stream, manifest);
                            jar_stream.setMethod(JarOutputStream.DEFLATED);
                            List<File> src_files = DevTools.listFolder(class_file, "class");
                            List<File> res_files = DevTools.listFolder(dir, "");
                            for (File file : src_files) {
                                String fileName = file.getAbsolutePath().replace("\\", "/").replaceFirst(class_file.toString().replace("\\", "/") + "/", "");
                                commandSender.sendMessage("[DevTool] 添加类文件 " + fileName + " 中...");
                                FileInputStream fis = new FileInputStream(file);
                                jar_stream.putNextEntry(new JarEntry(fileName));
                                int length;
                                byte[] buffer = new byte[4096];
                                while ((length = fis.read(buffer)) != -1) {
                                    jar_stream.write(buffer, 0, length);
                                }
                                fis.close();
                            }
                            for (File file : res_files) {
                                String fileName = file.getAbsolutePath().replace("\\", "/").replaceFirst(dir.toString().replace("\\", "/") + "/", ""), splited = fileName.split("/")[0];
                                if (splited.equals("src") || splited.equals(class_file.getName())) {
                                    continue;
                                }
                                commandSender.sendMessage("[DevTool] 添加资源文件 " + fileName + " 中...");
                                FileInputStream fis = new FileInputStream(file);
                                jar_stream.putNextEntry(new JarEntry(fileName));
                                int length;
                                byte[] buffer = new byte[4096];
                                while ((length = fis.read(buffer)) != -1) {
                                    jar_stream.write(buffer, 0, length);
                                }
                                fis.close();
                            }
                            jar_stream.close();
                            file_stream.close();
                            commandSender.sendMessage("[DevTool] 插件构建文件已存放至 " + jar_file);
                        }
                    }
                }
            } catch (Exception e) {
                DevTools.getInstance().getLogger().error(e);
            }
        } else {
            return false;
        }
        return true;
    }
}
