package Generator;

import Global.GlobalTableInfo;

import static utils.FieldProcessorUtils.capitalizeFirstLetter;
import static utils.FieldProcessorUtils.toCamelCase;

public class MapperGenerator {
    
    /**
     * 生成 Mapper 接口的代码内容（用于文件写入）
     * @param tableInfo 全局表信息
     * @return 生成的 Mapper 代码字符串
     */
    public String generateMapperContent(GlobalTableInfo tableInfo) {
        return buildMapperCode(tableInfo);
    }
    
    /**
     * 生成 Mapper 接口的代码模板（用于控制台打印）
     * @param tableInfo 全局表信息
     */
    public void generateMapperTemplate(GlobalTableInfo tableInfo) {
        String content = buildMapperCode(tableInfo);
        if (content != null) {
            System.out.println("Mapper 接口代码生成完毕");
        }
    }
    
    /**
     * 构建 Mapper 接口的代码
     * @param tableInfo 全局表信息
     * @return 生成的代码字符串
     */
    private String buildMapperCode(GlobalTableInfo tableInfo) {
        String tableName = tableInfo.tableName;
        String mapperPackage = tableInfo.mapperPackage;
        String entityOrdomainPackage = tableInfo.entityOrdomainPackage;

        if (tableName == null || tableName.trim().isEmpty() ||
            mapperPackage == null || mapperPackage.trim().isEmpty() ||
            entityOrdomainPackage == null || entityOrdomainPackage.trim().isEmpty()) {
            System.out.println("错误：GlobalTableInfo 中的 tableName, mapperPackage 或 entityOrdomainPackage 未设置。");
            return null;
        }

        // 1. 从表名生成实体名 (e.g., user_info -> UserInfo)
        String entityName = capitalizeFirstLetter(toCamelCase(tableName));

        // 2. 构建 Mapper 接口模板
        String template = String.format(
                "package %s;\n\n" +
                "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n" +
                "import %s.%s;\n" +
                "import org.apache.ibatis.annotations.Mapper;\n\n" +
                "@Mapper\n" +
                "public interface %sMapper extends BaseMapper<%s> {\n" +
                "}\n",
                mapperPackage,
                entityOrdomainPackage, entityName,
                entityName, entityName
        );

        return template;
    }
}