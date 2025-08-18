package Global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件读写工具类。
 * 负责将GlobalTableInfo中的路径配置持久化到.properties文件中，并在启动时加载。
 */
public class FileConfigurationReadingUtils {

    // 定义配置文件的名称和路径（存储在程序运行的根目录下）
    private static final String CONFIG_FILE_NAME = "generator_config.properties";

    /**
     * 保存当前配置到.properties文件。
     * 从GlobalTableInfo单例中获取数据并写入文件。
     */
    public static void saveConfiguration() {
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        Properties props = new Properties();

        // 将全局配置的字段放入Properties对象
        // 为防止null值，进行检查
        props.setProperty("projectPath", globalInfo.projectPath != null ? globalInfo.projectPath : "");
        props.setProperty("entityOrdomainPackage", globalInfo.entityOrdomainPackage != null ? globalInfo.entityOrdomainPackage : "");
        props.setProperty("mapperPackage", globalInfo.mapperPackage != null ? globalInfo.mapperPackage : "");
        props.setProperty("servicePackage", globalInfo.servicePackage != null ? globalInfo.servicePackage : "");
        props.setProperty("controllerPackage", globalInfo.controllerPackage != null ? globalInfo.controllerPackage : "");
        props.setProperty("implPackage", globalInfo.implPackage != null ? globalInfo.implPackage : "");

        // 使用try-with-resources语句确保流被正确关闭
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE_NAME)) {
            props.store(fos, "MyBatis Plus Generator Configuration");
            System.out.println("配置已成功保存到 " + new File(CONFIG_FILE_NAME).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("保存配置文件时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 从.properties文件加载配置。
     * 将读取到的数据设置到GlobalTableInfo单例中。
     */
    public static void loadConfiguration() {
        File configFile = new File(CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            System.out.println("配置文件不存在，将使用默认设置。");
            return;
        }

        Properties props = new Properties();
        // 使用try-with-resources语句确保流被正确关闭
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);

            GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();

            // 从Properties对象读取数据并设置到全局配置中
            // getProperty的第二个参数是默认值，如果文件中没有该键，则使用默认值
            globalInfo.projectPath = props.getProperty("projectPath", "");
            globalInfo.entityOrdomainPackage = props.getProperty("entityOrdomainPackage", "");
            globalInfo.mapperPackage = props.getProperty("mapperPackage", "");
            globalInfo.servicePackage = props.getProperty("servicePackage", "");
            globalInfo.controllerPackage = props.getProperty("controllerPackage", "");
            globalInfo.implPackage = props.getProperty("implPackage", "");

            System.out.println("配置已从 " + configFile.getAbsolutePath() + " 加载。");
        } catch (IOException e) {
            System.err.println("加载配置文件时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}