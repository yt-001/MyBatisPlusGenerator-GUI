package utils;

import Global.GlobalTableInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动搜索项目路径并直接更新全局配置的工具类。
 * 搜索逻辑按优先级进行：先搜索第一个别名，如果找到，则停止；否则，继续搜索下一个别名。
 */
public class AutomaticSearchPathUtils {

    // 定义标准名称到其别名列表的映射, 数组顺序代表优先级
    private static final Map<String, String[]> TARGET_ALIASES = new HashMap<>();
    static {
        TARGET_ALIASES.put("entity", new String[]{"entity", "domain"});
        TARGET_ALIASES.put("mapper", new String[]{"mapper", "dao"});
        TARGET_ALIASES.put("service", new String[]{"service", "services"});
        TARGET_ALIASES.put("controller", new String[]{"controller", "controllers"});
        TARGET_ALIASES.put("impl", new String[]{"Impl"});
    }

    /**
     * 查找项目中的最佳路径，并将它们转换为包名，直接更新到GlobalTableInfo单例中。
     *
     * @param projectPath 项目的根路径。
     */
    public static void findAndSetGlobalPaths(String projectPath) {
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        File projectDir = new File(projectPath);
        File srcDir = new File(projectDir, "src");

        // 1. 设置项目绝对路径
        globalInfo.projectPath = projectDir.getAbsolutePath();

        if (!srcDir.exists() || !srcDir.isDirectory()) {
            // 如果没有src目录，则所有包路径都为空
            globalInfo.entityOrdomainPackage = "";
            globalInfo.mapperPackage = "";
            globalInfo.servicePackage = "";
            globalInfo.controllerPackage = "";
            globalInfo.implPackage = "";
            return;
        }

        // 2. 按优先级为每个类别查找最佳包路径
        globalInfo.entityOrdomainPackage = findBestPackageForCategory(srcDir, TARGET_ALIASES.get("entity"));
        globalInfo.mapperPackage = findBestPackageForCategory(srcDir, TARGET_ALIASES.get("mapper"));
        globalInfo.servicePackage = findBestPackageForCategory(srcDir, TARGET_ALIASES.get("service"));
        globalInfo.controllerPackage = findBestPackageForCategory(srcDir, TARGET_ALIASES.get("controller"));
        globalInfo.implPackage = findBestPackageForCategory(srcDir, TARGET_ALIASES.get("impl"));
    }

    /**
     * 按优先级顺序为单个类别（如 "mapper"）查找最佳包路径。
     *
     * @param searchRoot 搜索的根目录 (通常是 'src')。
     * @param aliases    要搜索的别名数组，按优先级排序。
     * @return 找到的最佳包名，如果未找到则返回空字符串。
     */
    private static String findBestPackageForCategory(File searchRoot, String[] aliases) {
        for (String alias : aliases) {
            List<String> foundPaths = new ArrayList<>();
            findPathsForAlias(searchRoot, alias, foundPaths);

            if (!foundPaths.isEmpty()) {
                // 如果找到了一个或多个匹配项，选择最短的路径，转换并立即返回
                String bestPath = foundPaths.stream().min(Comparator.comparingInt(String::length)).get();
                return convertPathToPackage(bestPath);
            }
        }
        // 如果遍历完所有别名都没有找到，则返回空字符串
        return "";
    }

    /**
     * 递归地在目录中查找所有与单个别名匹配的文件夹。
     *
     * @param directory  当前搜索的目录。
     * @param alias      要查找的别名。
     * @param foundPaths 用于存储找到的路径的列表。
     */
    private static void findPathsForAlias(File directory, String alias, List<String> foundPaths) {
        if (directory.getName().equalsIgnoreCase(alias)) {
            foundPaths.add(directory.getAbsolutePath());
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String name = file.getName();
                    // 跳过常见的非源码目录
                    if (!name.equals(".git") && !name.equals("target") && !name.equals(".idea") && !name.equals("build") && !name.equals("node_modules")) {
                        findPathsForAlias(file, alias, foundPaths);
                    }
                }
            }
        }
    }

    /**
     * 将文件系统路径转换为Java包名。
     */
    private static String convertPathToPackage(String path) {
        if (path == null || path.trim().isEmpty()) return "";
        String packagePath = path.replace("\\", "/");
        
        String[] prefixes = {"src/main/java/", "src/test/java/", "src/"};
        for (String prefix : prefixes) {
            if (packagePath.contains(prefix)) {
                packagePath = packagePath.substring(packagePath.indexOf(prefix) + prefix.length());
                break;
            }
        }
        return packagePath.replace('/', '.');
    }
}