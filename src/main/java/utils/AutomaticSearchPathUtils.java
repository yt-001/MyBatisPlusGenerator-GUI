package utils;

import Global.GlobalTableInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class AutomaticSearchPathUtils {

    /**
     * 查找并设置全局路径。
     * 优先通过寻找 @SpringBootApplication 来确定基础包，然后在其下寻找标准子包。
     * 如果失败，则回退到在 src/main/java 下直接寻找标准子包。
     * @param projectPath 项目根路径
     */
    public static void findAndSetGlobalPaths(String projectPath) {
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        globalInfo.projectPath = projectPath;

        File srcJavaDir = new File(projectPath, "src/main/java");
        if (!srcJavaDir.exists() || !srcJavaDir.isDirectory()) {
            System.out.println("错误：在 " + projectPath + " 中未找到 'src/main/java' 目录。");
            // Clear all paths
            globalInfo.entityOrdomainPackage = "";
            globalInfo.mapperPackage = "";
            globalInfo.servicePackage = "";
            globalInfo.implPackage = "";
            globalInfo.controllerPackage = "";
            return;
        }

        String basePackage = findBasePackage(srcJavaDir.toPath());
        String basePackagePath = basePackage != null ? basePackage.replace('.', '/') : null;

        if (basePackagePath != null) {
            System.out.println("自动检测到基础包路径: " + basePackage);
            // 在基础包下查找
            globalInfo.entityOrdomainPackage = findSubPackage(srcJavaDir, basePackagePath, "entity", "domain");
            globalInfo.mapperPackage = findSubPackage(srcJavaDir, basePackagePath, "mapper", "dao");
            globalInfo.servicePackage = findSubPackage(srcJavaDir, basePackagePath, "service");
            globalInfo.controllerPackage = findSubPackage(srcJavaDir, basePackagePath, "controller", "web");
            // Impl is special, it's inside service
            if (globalInfo.servicePackage != null && !globalInfo.servicePackage.isEmpty()) {
                globalInfo.implPackage = findSubPackage(srcJavaDir, globalInfo.servicePackage.replace('.', '/'), "impl");
            } else {
                 globalInfo.implPackage = "";
            }

        } else {
            System.out.println("警告：未能自动检测到基础包路径。将在 src/main/java 下直接查找。");
            // 回退逻辑：直接在 src/main/java 下查找
            globalInfo.entityOrdomainPackage = findSubPackage(srcJavaDir, "", "entity", "domain");
            globalInfo.mapperPackage = findSubPackage(srcJavaDir, "", "mapper", "dao");
            globalInfo.servicePackage = findSubPackage(srcJavaDir, "", "service");
            globalInfo.controllerPackage = findSubPackage(srcJavaDir, "", "controller", "web");
            globalInfo.implPackage = findSubPackage(srcJavaDir, "", "service/impl");
        }
        
        // 如果 service.impl 没找到，但 service 找到了，就组合一个
        if (globalInfo.implPackage.isEmpty() && !globalInfo.servicePackage.isEmpty()) {
            String potentialImplPackage = globalInfo.servicePackage + ".impl";
            File implDir = new File(srcJavaDir, potentialImplPackage.replace('.', '/'));
            if (implDir.exists() && implDir.isDirectory()) {
                globalInfo.implPackage = potentialImplPackage;
            }
        }


        System.out.println("智能匹配到的包路径如下:");
        System.out.println("  - Entity/Domain: " + (globalInfo.entityOrdomainPackage.isEmpty() ? "未找到" : globalInfo.entityOrdomainPackage));
        System.out.println("  - Mapper: " + (globalInfo.mapperPackage.isEmpty() ? "未找到" : globalInfo.mapperPackage));
        System.out.println("  - Service: " + (globalInfo.servicePackage.isEmpty() ? "未找到" : globalInfo.servicePackage));
        System.out.println("  - Impl: " + (globalInfo.implPackage.isEmpty() ? "未找到" : globalInfo.implPackage));
        System.out.println("  - Controller: " + (globalInfo.controllerPackage.isEmpty() ? "未找到" : globalInfo.controllerPackage));
    }

    /**
     * 查找启动类来确定基础包路径。
     * @param srcJavaPath src/main/java 的路径
     * @return 基础包名，例如 com.example.demo, 如果找不到则返回 null
     */
    public static String findBasePackage(Path srcJavaPath) {
        try {
            List<Path> mainAppFiles = new ArrayList<>();
            Files.walkFileTree(srcJavaPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith("Application.java")) {
                        String content = new String(Files.readAllBytes(file));
                        if (content.contains("@SpringBootApplication")) {
                            mainAppFiles.add(file);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            if (!mainAppFiles.isEmpty()) {
                // 通常只有一个启动类，取第一个
                Path mainAppPath = mainAppFiles.get(0);
                Path packagePath = srcJavaPath.relativize(mainAppPath.getParent());
                return packagePath.toString().replace(File.separator, ".");
            }
        } catch (IOException e) {
            System.out.println("错误：在查找基础包路径时发生IO异常：" + e.getMessage());
        }
        return null;
    }

    /**
     * 在基础路径下查找子包。
     * @param srcJavaDir src/main/java 目录
     * @param basePackagePath 基础包的路径形式 (e.g., "com/example/demo")
     * @param aliases 子包的别名 (e.g., "entity", "domain")
     * @return 找到的完整包名，找不到则返回空字符串
     */
    private static String findSubPackage(File srcJavaDir, String basePackagePath, String... aliases) {
        for (String alias : aliases) {
            String subPath = basePackagePath.isEmpty() ? alias : basePackagePath + "/" + alias;
            File checkDir = new File(srcJavaDir, subPath);
            if (checkDir.exists() && checkDir.isDirectory()) {
                return subPath.replace('/', '.');
            }
        }
        return "";
    }
}