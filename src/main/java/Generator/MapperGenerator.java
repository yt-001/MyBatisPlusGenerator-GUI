package Generator;

import Global.GlobalTableInfo;

import static utils.FieldProcessorUtils.capitalizeFirstLetter;
import static utils.FieldProcessorUtils.toCamelCase;

public class MapperGenerator {
    public void generateMapperTemplate(GlobalTableInfo tableInfo) {
        String tableName = tableInfo.tableName;
        String mapperPackage = tableInfo.mapperPackage;
        String entityOrdomainPackage = tableInfo.entityOrdomainPackage;

        if (tableName == null || tableName.trim().isEmpty() ||
            mapperPackage == null || mapperPackage.trim().isEmpty() ||
            entityOrdomainPackage == null || entityOrdomainPackage.trim().isEmpty()) {
            System.out.println("错误：GlobalTableInfo 中的 tableName, mapperPackage 或 entityOrdomainPackage 未设置。");
            return;
        }

        // 1. 从表名生成实体名 (e.g., user_info -> UserInfo)
        String entityName = capitalizeFirstLetter(toCamelCase(tableName));

        // 2. 直接使用 entityOrdomainPackage 作为实体类包路径

        // 3. 构建 Mapper 接口模板
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

        System.out.println("\n--- Generated Mapper Template ---");
        System.out.println(template);
    }
}