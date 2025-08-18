package Generator;

import Global.GlobalTableInfo;
import static utils.FieldProcessorUtils.capitalizeFirstLetter;
import static utils.FieldProcessorUtils.toCamelCase;

/**
 * Service 和 ServiceImpl 代码生成器。
 * 根据 GlobalTableInfo 中的配置生成相应的模板。
 */
public class ServiceGenerator {

    /**
     * 生成 Service 接口的代码内容（用于文件写入）
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     * @return 生成的 Service 代码字符串
     */
    public String generateServiceContent(GlobalTableInfo tableInfo) {
        return buildServiceCode(tableInfo);
    }

    /**
     * 生成 ServiceImpl 实现类的代码内容（用于文件写入）
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     * @return 生成的 ServiceImpl 代码字符串
     */
    public String generateServiceImplContent(GlobalTableInfo tableInfo) {
        return buildServiceImplCode(tableInfo);
    }

    /**
     * 生成 Service 接口和 ServiceImpl 实现类的代码模板（用于控制台打印）
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     */
    public void generateServiceAndImplTemplates(GlobalTableInfo tableInfo) {
        String serviceContent = buildServiceCode(tableInfo);
        String serviceImplContent = buildServiceImplCode(tableInfo);
        
        if (serviceContent != null && serviceImplContent != null) {
            System.out.println("Service 接口代码生成完毕");
            System.out.println("ServiceImpl 实现类代码生成完毕");
        }
    }

    /**
     * 构建 Service 接口的代码
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     * @return 生成的代码字符串
     */
    private String buildServiceCode(GlobalTableInfo tableInfo) {
        // 1. 从全局配置中获取所需信息
        String tableName = tableInfo.tableName;
        String servicePackage = tableInfo.servicePackage;
        String entityOrdomainPackage = tableInfo.entityOrdomainPackage;

        // 2. 检查关键信息是否缺失
        if (tableName == null || tableName.trim().isEmpty() ||
            servicePackage == null || servicePackage.trim().isEmpty() ||
            entityOrdomainPackage == null || entityOrdomainPackage.trim().isEmpty()) {
            System.out.println("错误：生成Service所需的一个或多个关键包路径（service, entity）或表名未在GlobalTableInfo中设置。");
            return null;
        }

        // 3. 根据表名派生出相关的类名
        String entityName = capitalizeFirstLetter(toCamelCase(tableName));
        String serviceName = entityName + "Service";

        // 4. 构建 Service 接口的代码模板
        String serviceTemplate = String.format(
            "package %s;\n\n" +
            "import %s.%s;\n" +
            "import com.baomidou.mybatisplus.extension.service.IService;\n\n" +
            "public interface %s extends IService<%s> {\n" +
            "}\n",
            servicePackage,
            entityOrdomainPackage, entityName,
            serviceName, entityName
        );

        return serviceTemplate;
    }

    /**
     * 构建 ServiceImpl 实现类的代码
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     * @return 生成的代码字符串
     */
    private String buildServiceImplCode(GlobalTableInfo tableInfo) {
        // 1. 从全局配置中获取所需信息
        String tableName = tableInfo.tableName;
        String implPackage = tableInfo.implPackage;
        String entityOrdomainPackage = tableInfo.entityOrdomainPackage;
        String mapperPackage = tableInfo.mapperPackage;
        String servicePackage = tableInfo.servicePackage;

        // 2. 检查关键信息是否缺失
        if (tableName == null || tableName.trim().isEmpty() ||
            implPackage == null || implPackage.trim().isEmpty() ||
            entityOrdomainPackage == null || entityOrdomainPackage.trim().isEmpty() ||
            mapperPackage == null || mapperPackage.trim().isEmpty() ||
            servicePackage == null || servicePackage.trim().isEmpty()) {
            System.out.println("错误：生成ServiceImpl所需的一个或多个关键包路径（impl, entity, mapper, service）或表名未在GlobalTableInfo中设置。");
            return null;
        }

        // 3. 根据表名派生出相关的类名
        String entityName = capitalizeFirstLetter(toCamelCase(tableName));
        String serviceName = entityName + "Service";
        String serviceImplName = serviceName + "Impl";
        String mapperName = entityName + "Mapper";

        // 4. 构建 ServiceImpl 实现类的代码模板
        String serviceImplTemplate = String.format(
            "package %s;\n\n" +
            "import %s.%s;\n" +
            "import %s.%s;\n" +
            "import %s.%s;\n" +
            "import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n" +
            "import org.springframework.stereotype.Service;\n\n" +
            "@Service\n" +
            "public class %s extends ServiceImpl<%s, %s> implements %s {\n" +
            "}\n",
            implPackage,
            entityOrdomainPackage, entityName,
            mapperPackage, mapperName,
            servicePackage, serviceName,
            serviceImplName, mapperName, entityName, serviceName
        );

        return serviceImplTemplate;
    }
}