package Generator;

import Global.GlobalTableInfo;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static utils.FieldProcessorUtils.capitalizeFirstLetter;
import static utils.FieldProcessorUtils.toCamelCase;

/**
 * 代码文件写入器
 * 负责将生成的代码模板写入到实际的文件中
 */
public class CodeFileWriter {
    
    /**
     * 写入所有生成的代码文件
     * @param tableInfo 全局表信息
     */
    public static void writeAllCodeFiles(GlobalTableInfo tableInfo) {
        try {
            System.out.println("=== 开始写入代码文件 ===");
            
            // 写入 Entity 文件
            writeEntityFile(tableInfo);
            
            // 写入 Mapper 文件
            writeMapperFile(tableInfo);
            
            // 写入 Service 文件
            writeServiceFile(tableInfo);
            
            // 写入 ServiceImpl 文件
            writeServiceImplFile(tableInfo);
            
            // 写入 Controller 文件
            writeControllerFile(tableInfo);
            
            System.out.println("=== 所有代码文件写入完成 ===");
            
        } catch (Exception e) {
            System.err.println("写入代码文件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 写入 Entity 实体类文件
     */
    private static void writeEntityFile(GlobalTableInfo tableInfo) throws IOException {
        entityGenerator generator = new entityGenerator();
        String content = generator.generateEntityContent(tableInfo);
        
        String entityName = capitalizeFirstLetter(toCamelCase(tableInfo.tableName));
        String fileName = entityName + ".java";
        String packagePath = tableInfo.entityOrdomainPackage.replace('.', '/');
        
        writeToFile(tableInfo.projectPath, packagePath, fileName, content);
        System.out.println("Entity 文件已写入: " + fileName);
    }
    
    /**
     * 写入 Mapper 接口文件
     */
    private static void writeMapperFile(GlobalTableInfo tableInfo) throws IOException {
        MapperGenerator generator = new MapperGenerator();
        String content = generator.generateMapperContent(tableInfo);
        
        String entityName = capitalizeFirstLetter(toCamelCase(tableInfo.tableName));
        String fileName = entityName + "Mapper.java";
        String packagePath = tableInfo.mapperPackage.replace('.', '/');
        
        writeToFile(tableInfo.projectPath, packagePath, fileName, content);
        System.out.println("Mapper 文件已写入: " + fileName);
    }
    
    /**
     * 写入 Service 接口文件
     */
    private static void writeServiceFile(GlobalTableInfo tableInfo) throws IOException {
        ServiceGenerator generator = new ServiceGenerator();
        String content = generator.generateServiceContent(tableInfo);
        
        String entityName = capitalizeFirstLetter(toCamelCase(tableInfo.tableName));
        String fileName = entityName + "Service.java";
        String packagePath = tableInfo.servicePackage.replace('.', '/');
        
        writeToFile(tableInfo.projectPath, packagePath, fileName, content);
        System.out.println("Service 文件已写入: " + fileName);
    }
    
    /**
     * 写入 ServiceImpl 实现类文件
     */
    private static void writeServiceImplFile(GlobalTableInfo tableInfo) throws IOException {
        ServiceGenerator generator = new ServiceGenerator();
        String content = generator.generateServiceImplContent(tableInfo);
        
        String entityName = capitalizeFirstLetter(toCamelCase(tableInfo.tableName));
        String fileName = entityName + "ServiceImpl.java";
        String packagePath = tableInfo.implPackage.replace('.', '/');
        
        writeToFile(tableInfo.projectPath, packagePath, fileName, content);
        System.out.println("ServiceImpl 文件已写入: " + fileName);
    }
    
    /**
     * 写入 Controller 控制器文件
     */
    private static void writeControllerFile(GlobalTableInfo tableInfo) throws IOException {
        ControllerGenerator generator = new ControllerGenerator();
        String content = generator.generateControllerContent(tableInfo);
        
        String entityName = capitalizeFirstLetter(toCamelCase(tableInfo.tableName));
        String fileName = entityName + "Controller.java";
        String packagePath = tableInfo.controllerPackage.replace('.', '/');
        
        writeToFile(tableInfo.projectPath, packagePath, fileName, content);
        System.out.println("Controller 文件已写入: " + fileName);
    }
    
    /**
    /**
     * 创建文件并写入内容
     * @param projectPath 项目根路径
     * @param packagePath 包路径
     * @param fileName 文件名
     * @param content 文件内容
     */
    private static void writeToFile(String projectPath, String packagePath, String fileName, String content) throws IOException {
        // 确保项目路径以正确的分隔符结尾
        String normalizedProjectPath = projectPath.replace('\\', '/');
        if (!normalizedProjectPath.endsWith("/")) {
            normalizedProjectPath += "/";
        }
        
        // 构建完整的目录路径：项目路径 + src/main/java + 包路径
        String fullDirPath = normalizedProjectPath + "src/main/java/" + packagePath;
        Path dirPath = Paths.get(fullDirPath);
        
        System.out.println("准备创建目录: " + dirPath.toString());
        
        // 创建目录（如果不存在）
        Files.createDirectories(dirPath);
        System.out.println("目录创建成功: " + dirPath.toString());
        
        // 构建完整的文件路径
        Path filePath = dirPath.resolve(fileName);
        System.out.println("准备创建文件: " + filePath.toString());
        
        // 创建文件并写入内容
        File file = filePath.toFile();
        if (file.exists()) {
            System.out.println("文件已存在，将覆盖: " + filePath.toString());
        } else {
            System.out.println("创建新文件: " + filePath.toString());
        }
        
        // 创建文件并写入内容
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(content);
            writer.flush();
        }
        
        System.out.println("文件创建成功: " + fileName + " 位置: " + filePath.toString());
    }
}