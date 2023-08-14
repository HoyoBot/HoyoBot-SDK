package cn.hoyobot.devtools.loader;

import cn.hoyobot.devtools.DevTools;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class PackageCompiler {

    public JavaCompiler compiler = null;

    public JavaCompiler checkCompiler() {
        this.compiler = ToolProvider.getSystemJavaCompiler();
        if (this.compiler == null) {
            DevTools.getInstance().getLogger().pluginInfo("无法找到编译器,正在启用第三方编译器!");
            File jar_file = new File(DevTools.getInstance().getDataPath(), "tools.jar");
            if (!jar_file.isFile()) {
                DevTools.getInstance().getLogger().error("第三方编译器缺失,请自行下载 tools.jar 放入 " + DevTools.getInstance().getDataPath());
                return null;
            }
            try {
                URLClassLoader loader = new URLClassLoader(new URL[]{jar_file.toURI().toURL()});
                Class<?> tools = loader.loadClass("com.sun.tools.javac.api.JavacTool");
                this.compiler = tools.asSubclass(JavaCompiler.class).newInstance();
            } catch (Exception e) {
                DevTools.getInstance().getLogger().error(e);
                return null;
            }
        }
        return this.compiler;
    }

}
