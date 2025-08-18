
package Generator;

import Global.GlobalTableInfo;

import static utils.FieldProcessorUtils.capitalizeFirstLetter;
import static utils.FieldProcessorUtils.toCamelCase;

/**
 * Controller 代码生成器。
 * 根据 GlobalTableInfo 中的配置生成相应的模板。
 */
public class ControllerGenerator {

    /**
     * 生成 Controller 类的代码内容（用于文件写入）
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     * @return 生成的 Controller 代码字符串
     */
    public String generateControllerContent(GlobalTableInfo tableInfo) {
        return buildControllerCode(tableInfo);
    }

    /**
     * 生成 Controller 类的代码模板（用于控制台打印）
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     */
    public void generateControllerTemplate(GlobalTableInfo tableInfo) {
        String content = buildControllerCode(tableInfo);
        if (content != null) {
            System.out.println("Controller 控制器代码生成完毕");
        }
    }

    /**
     * 构建 Controller 类的代码
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     * @return 生成的代码字符串
     */
    private String buildControllerCode(GlobalTableInfo tableInfo) {
        // 1. 从全局配置中获取所需信息
        String tableName = tableInfo.tableName;
        String controllerPackage = tableInfo.controllerPackage;
        String servicePackage = tableInfo.servicePackage;
        String entityOrdomainPackage = tableInfo.entityOrdomainPackage;

        // 2. 检查关键信息是否缺失
        if (tableName == null || tableName.trim().isEmpty() ||
            controllerPackage == null || controllerPackage.trim().isEmpty() ||
            servicePackage == null || servicePackage.trim().isEmpty() ||
            entityOrdomainPackage == null || entityOrdomainPackage.trim().isEmpty()) {
            System.out.println("错误：生成Controller所需的一个或多个关键包路径（controller, service, entity）或表名未在GlobalTableInfo中设置。");
            return null;
        }

        // 3. 根据表名派生出相关的类名和路径
        // 3. 根据表名派生出相关的类名和路径
        String entityName = capitalizeFirstLetter(toCamelCase(tableName));
        String serviceName = entityName + "Service";
        String controllerName = entityName + "Controller";
        String requestMappingPath = toCamelCase(tableName); // e.g., user_info -> userInfo
        // 正确的驼峰命名：首字母小写
        String serviceInstanceName = Character.toLowerCase(serviceName.charAt(0)) + serviceName.substring(1);

        // 4. 构建 Controller 类的代码模板
        String controllerTemplate = String.format(
            "package %s;\n\n" +
            "import %s.%s;\n" +
            "import %s.%s;\n" +
            "import org.springframework.web.bind.annotation.*;\n" +
            "import jakarta.annotation.Resource;\n\n" +
            "@RestController\n" +
            "@RequestMapping(\"/%s\")\n" +
            "public class %s {\n\n" +
            "    @Resource\n" +
            "    private %s %s;\n\n" +
            "    // 可在此处添加基本的CRUD方法\n" +
            "}\n",
            controllerPackage,
            entityOrdomainPackage, entityName,
            servicePackage, serviceName,
            requestMappingPath,
            controllerName,
            serviceName, serviceInstanceName
        );

        return controllerTemplate;
    }
}