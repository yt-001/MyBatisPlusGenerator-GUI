package Generator;

import Global.GlobalTableInfo;
import utils.FieldProcessorUtils;
import utils.TypeConverterUtils;

import static utils.FieldProcessorUtils.capitalizeFirstLetter;
import static utils.FieldProcessorUtils.toCamelCase;

/**
 * Entity 实体类代码生成器。
 * 根据 GlobalTableInfo 中的配置生成相应的模板。
 */
public class entityGenerator {

    /**
     * 生成 Entity 类的代码内容（用于文件写入）
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     * @return 生成的 Entity 代码字符串
     */
    public String generateEntityContent(GlobalTableInfo tableInfo) {
        return buildEntityCode(tableInfo);
    }

    /**
     * 生成 Entity 类的代码模板（用于控制台打印）
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     */
    public void generateEntityTemplate(GlobalTableInfo tableInfo) {
        String content = buildEntityCode(tableInfo);
        System.out.println("Entity 实体类代码生成完毕");
    }

    /**
     * 构建 Entity 类的代码
     * @param tableInfo 包含所有路径和表信息的全局配置实例。
     * @return 生成的代码字符串
     */
    private String buildEntityCode(GlobalTableInfo tableInfo) {
        // 1. 从全局配置中获取所需信息
        String tableName = tableInfo.tableName;
        String entityOrdomainPackage = tableInfo.entityOrdomainPackage;
        String[] fieldNames = tableInfo.fieldNames;
        String[] fieldTypes = tableInfo.fieldTypes;
        String[] fieldAnnotations = tableInfo.fieldAnnotations;

        // 2. 检查关键信息是否缺失
        if (tableName == null || tableName.trim().isEmpty() ||
            entityOrdomainPackage == null || entityOrdomainPackage.trim().isEmpty() ||
            fieldNames == null || fieldNames.length == 0 ||
            fieldTypes == null || fieldTypes.length != fieldNames.length) {
            System.out.println("错误：生成Entity所需的一个或多个关键信息（表名、包路径、字段名、字段类型）未在GlobalTableInfo中正确设置。");
            return null;
        }

        // 3. 根据表名派生出实体类名
        String entityName = capitalizeFirstLetter(toCamelCase(tableName));

        // 4. 构建实体类的代码
        StringBuilder entityBuilder = new StringBuilder();
        entityBuilder.append(String.format("package %s;\n\n", entityOrdomainPackage));
        entityBuilder.append("import com.baomidou.mybatisplus.annotation.TableName;\n");
        entityBuilder.append("import com.baomidou.mybatisplus.annotation.IdType;\n");
        entityBuilder.append("import com.baomidou.mybatisplus.annotation.TableId;\n");
        entityBuilder.append("import com.baomidou.mybatisplus.annotation.TableField;\n");
        entityBuilder.append("\n"); 
        entityBuilder.append("import java.io.Serial;\n");
        entityBuilder.append("import java.io.Serializable;\n");
        entityBuilder.append("import java.time.LocalDateTime;\n");
        entityBuilder.append("import lombok.Getter;\n");
        entityBuilder.append("import lombok.Setter;\n");
        entityBuilder.append("import lombok.ToString;\n");
        entityBuilder.append("import lombok.NoArgsConstructor;\n");
        entityBuilder.append("import lombok.AllArgsConstructor;\n\n");

        entityBuilder.append("@Getter\n");
        entityBuilder.append("@Setter\n");
        entityBuilder.append("@ToString\n");
        entityBuilder.append("@NoArgsConstructor\n");
        entityBuilder.append("@AllArgsConstructor\n");
        entityBuilder.append(String.format("@TableName(\"%s\")\n", tableName));
        entityBuilder.append(String.format("public class %s implements Serializable {\n\n", entityName));
        entityBuilder.append("    @Serial\n");
        entityBuilder.append("    private static final long serialVersionUID = 1L;\n\n");

        // 5. 生成字段
        for (int i = 0; i < fieldNames.length; i++) {
            String dbFieldName = fieldNames[i];
            String dbFieldType = fieldTypes[i];
            String javaFieldName = toCamelCase(dbFieldName);
            // 使用 TypeConverterUtils 进行类型转换
            String javaFieldType = TypeConverterUtils.convertToJavaType(dbFieldType);

            // 添加字段注释（如果存在）
            if (fieldAnnotations != null && i < fieldAnnotations.length && 
                fieldAnnotations[i] != null && !fieldAnnotations[i].trim().isEmpty()) {
                entityBuilder.append(String.format("    /**\n"));
                entityBuilder.append(String.format("     * %s\n", fieldAnnotations[i]));
                entityBuilder.append(String.format("     */\n"));
            }

            // 假设第一个字段是主键
            if (i == 0) {
                entityBuilder.append(String.format("    @TableId(value = \"%s\", type = IdType.AUTO)\n", dbFieldName));
            } else {
                entityBuilder.append(String.format("    @TableField(\"%s\")\n", dbFieldName));
            }
            entityBuilder.append(String.format("    private %s %s;\n\n", javaFieldType, javaFieldName));
        }

        entityBuilder.append("}\n");

        return entityBuilder.toString();
    }
}