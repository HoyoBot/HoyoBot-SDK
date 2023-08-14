package cn.hoyobot.devtools.loader;

import cn.hoyobot.devtools.DevTools;
import cn.hoyobot.sdk.HoyoBot;
import cn.hoyobot.sdk.event.proxy.ProxyPluginEnableEvent;
import cn.hoyobot.sdk.plugin.Plugin;
import cn.hoyobot.sdk.plugin.PluginLoader;
import cn.hoyobot.sdk.plugin.PluginYAML;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SourcePluginLoader extends PluginLoader {

    private final DevTools tools;
    private final HoyoBot botProxy;
    private final JavaCompiler compiler;
    private final StandardJavaFileManager fileManager;
    private final Map<String, Class<?>> classes = new HashMap<>();
    private final Map<Plugin, File> pluginPath = new HashMap<>();
    private final Map<String, SourcePluginClassLoader> classLoaders = new HashMap<>();

    public SourcePluginLoader(HoyoBot botProxy) {
        super();
        this.botProxy = botProxy;
        this.tools = DevTools.getInstance();
        this.compiler = DevTools.getInstance().packageCompiler.compiler;
        this.fileManager = this.compiler.getStandardFileManager(null, null, null);
    }

    public Plugin loadPlugin(File dir) throws Exception {
        PluginYAML description = this.getPluginDescription(dir);
        if (description != null) {
            if (this.botProxy.getPluginManager().getPluginByName(description.getName()) != null) {
                this.tools.getLogger().pluginWarn("无法加载插件 " + description.getName() + ", 插件已存在!");
                return null;
            } else {
                this.tools.getLogger().pluginWarn("加载插件 " + description.getName() + " 中...");
                File class_file = new File(dir.getAbsolutePath() + "/src_compile");
                this.compilePlugin(dir, class_file);
                SourcePluginClassLoader classLoader = new SourcePluginClassLoader(this, this.getClass().getClassLoader(), class_file, dir.getAbsolutePath());
                this.classLoaders.put(description.getName(), classLoader);
                File dataFolder = new File(dir.getParentFile(), description.getName());
                if (dataFolder.exists() && !dataFolder.isDirectory()) {
                    throw new IllegalStateException("编译对象文件 \"" + dataFolder + " " + description.getName() + " 存在且不是文件夹");
                } else {
                    try {
                        Plugin plugin = classLoader.loadClass(description.getMain()).asSubclass(Plugin.class).newInstance();
                        this.initPlugin(plugin, description, dir);
                        this.pluginPath.put(plugin, dir);
                        return plugin;
                    } catch (ClassCastException e) {
                        throw new Exception("插件主类 \"" + description.getMain() + "\" 不是一个HoyoSDK Plugin!");
                    } catch (ClassNotFoundException e) {
                        throw new Exception("无法加载插件 " + description.getName() + ": 无法寻找到主类!");
                    }
                }
            }
        } else {
            return null;
        }
    }

    public PluginYAML getPluginDescription(File dir) {
        try {
            File yml = new File(dir.getAbsolutePath(), "plugin.yml");
            return dir.isDirectory() && yml.isFile() ? tools.getBotProxy().getPluginManager().loadPluginConfig(Paths.get(dir.getPath())) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Pattern[] getPluginFilters() {
        return new Pattern[]{Pattern.compile(".+")};
    }

    public void compilePlugin(File dir, File output) throws Exception {
        if (output.isFile()) {
            throw new Exception("编译输出对象已存在且不是一个文件夹");
        } else {
            DevTools.removeFolder(output, "class");
            if (!output.mkdirs()) {
                throw new Exception("创建编译输出文件夹失败");
            } else {
                List<File> files = DevTools.listFolder(new File(dir.getAbsolutePath() + "/src"), "java");
                JavaCompiler.CompilationTask task = this.compiler.getTask(null, this.fileManager, null, Arrays.asList("-d", output.getAbsolutePath(), "-encoding", "utf-8"), null, this.fileManager.getJavaFileObjects(files.toArray(new File[0])));
                task.call();
            }
        }
    }

    public File getPluginPath(Plugin p) {
        return this.pluginPath.getOrDefault(p, null);
    }

    public PluginYAML getPluginDescription(String filename) {
        return this.getPluginDescription(new File(filename));
    }

    public Plugin loadPlugin(String filename) throws Exception {
        return this.loadPlugin(new File(filename));
    }

    private void initPlugin(Plugin plugin, PluginYAML description, File file) {
        plugin.init(description, this.botProxy, file);
    }

    public void enablePlugin(Plugin plugin) {
        if (plugin != null && !plugin.isEnabled()) {
            this.tools.getLogger().info("插件 " + plugin.getDescription() + " 启动中...");
            plugin.setEnabled(true);
            plugin.onEnable();
            ProxyPluginEnableEvent proxyPluginEnableEvent = new ProxyPluginEnableEvent(plugin);
            this.botProxy.getEventManager().callEvent(proxyPluginEnableEvent);
        }
    }

    public void disablePlugin(Plugin plugin) {
        if (plugin != null && plugin.isEnabled()) {
            plugin.setEnabled(false);
        }
    }

    public Class<?> getClassByName(String name) {
        Class<?> cachedClass = classes.get(name);
        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (SourcePluginClassLoader loader : this.classLoaders.values()) {
                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException e) {
                    this.tools.getLogger().error(e);
                }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
            return null;
        }
    }

    public void setClass(String name, Class<?> clazz) {
        if (!this.classes.containsKey(name)) {
            this.classes.put(name, clazz);
        }

    }

    private void removeClass(String name) {
        this.classes.remove(name);
    }

}
