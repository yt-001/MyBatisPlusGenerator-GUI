package utils;

import Global.GlobalTableInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 类型转换工具类
 * 用于将MySQL数据类型转换为简化的Java类型名
 */
public class TypeConverterUtils {
    
    // MySQL类型到简化Java类型的映射表
    private static final Map<String, String> TYPE_MAPPING = new HashMap<>();
    
    static {
        // 整数类型
        TYPE_MAPPING.put("BIGINT", "Long");
        TYPE_MAPPING.put("BIGINT UNSIGNED", "BigInteger");
        TYPE_MAPPING.put("INT", "Integer");
        TYPE_MAPPING.put("INT UNSIGNED", "Long");
        TYPE_MAPPING.put("INTEGER", "Integer");
        TYPE_MAPPING.put("MEDIUMINT", "Integer");
        TYPE_MAPPING.put("SMALLINT", "Short");
        TYPE_MAPPING.put("SMALLINT UNSIGNED", "Integer");
        TYPE_MAPPING.put("TINYINT", "Byte");
        TYPE_MAPPING.put("TINYINT UNSIGNED", "Short");
        
        // 浮点类型
        TYPE_MAPPING.put("DOUBLE", "Double");
        TYPE_MAPPING.put("FLOAT", "Float");
        TYPE_MAPPING.put("REAL", "Double");
        TYPE_MAPPING.put("DECIMAL", "BigDecimal");
        TYPE_MAPPING.put("NUMERIC", "BigDecimal");
        
        // 字符串类型
        TYPE_MAPPING.put("VARCHAR", "String");
        TYPE_MAPPING.put("CHAR", "String");
        TYPE_MAPPING.put("TEXT", "String");
        TYPE_MAPPING.put("LONGTEXT", "String");
        TYPE_MAPPING.put("MEDIUMTEXT", "String");
        TYPE_MAPPING.put("TINYTEXT", "String");
        TYPE_MAPPING.put("ENUM", "String");
        TYPE_MAPPING.put("SET", "String");
        
        // 日期时间类型
        TYPE_MAPPING.put("DATE", "LocalDate");
        TYPE_MAPPING.put("DATETIME", "LocalDateTime");
        TYPE_MAPPING.put("TIMESTAMP", "LocalDateTime");
        TYPE_MAPPING.put("TIME", "LocalTime");
        TYPE_MAPPING.put("YEAR", "Year");
        
        // 布尔类型
        TYPE_MAPPING.put("BOOLEAN", "Boolean");
        TYPE_MAPPING.put("BIT", "Boolean");
        
        // 二进制类型
        TYPE_MAPPING.put("BINARY", "byte[]");
        TYPE_MAPPING.put("VARBINARY", "byte[]");
        TYPE_MAPPING.put("BLOB", "byte[]");
        TYPE_MAPPING.put("LONGBLOB", "byte[]");
        TYPE_MAPPING.put("MEDIUMBLOB", "byte[]");
        TYPE_MAPPING.put("TINYBLOB", "byte[]");
        
        // JSON类型
        TYPE_MAPPING.put("JSON", "String");
        
        // 空间类型
        TYPE_MAPPING.put("GEOMETRY", "Geometry");
        TYPE_MAPPING.put("POINT", "Point");
        TYPE_MAPPING.put("POLYGON", "Polygon");
    }
    
    /**
     * 将MySQL数据类型转换为简化的Java类型名
     * 支持带括号的类型，如 VARCHAR(255), DECIMAL(10,2) 等
     * 
     * @param mysqlType MySQL数据类型
     * @return 对应的简化Java类型名
     */
    public static String convertToJavaType(String mysqlType) {
        if (mysqlType == null || mysqlType.trim().isEmpty()) {
            return "Object";
        }
        
        // 转换为大写并去除空格
        String normalizedType = mysqlType.trim().toUpperCase();
        
        // 处理特殊情况：TINYINT(1) 作为布尔类型
        if (normalizedType.equals("TINYINT(1)")) {
            return "Boolean";
        }
        
        // 处理带括号的类型，提取基础类型名
        String baseType = extractBaseType(normalizedType);
        
        // 处理UNSIGNED类型
        if (normalizedType.contains("UNSIGNED")) {
            String unsignedType = baseType + " UNSIGNED";
            if (TYPE_MAPPING.containsKey(unsignedType)) {
                return TYPE_MAPPING.get(unsignedType);
            }
        }
        
        // 查找映射
        if (TYPE_MAPPING.containsKey(baseType)) {
            return TYPE_MAPPING.get(baseType);
        }
        
        // 如果没有找到映射，返回默认类型
        System.out.println("警告: 未知的MySQL类型: " + mysqlType + ", 使用默认类型 String");
        return "String";
    }
    
    /**
     * 提取基础类型名（去除括号和参数）
     * 例如：VARCHAR(255) -> VARCHAR, DECIMAL(10,2) -> DECIMAL
     * 
     * @param type 完整的类型字符串
     * @return 基础类型名
     */
    private static String extractBaseType(String type) {
        // 使用正则表达式提取括号前的部分
        Pattern pattern = Pattern.compile("^([A-Z]+)(?:\\s+UNSIGNED)?(?:\\([^)]*\\))?");
        Matcher matcher = pattern.matcher(type);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // 如果正则匹配失败，手动处理
        int parenIndex = type.indexOf('(');
        if (parenIndex > 0) {
            return type.substring(0, parenIndex).trim();
        }
        
        return type;
    }
    
    /**
     * 批量转换字段类型数组
     * 
     * @param mysqlTypes MySQL类型数组
     * @return 简化Java类型数组
     */
    public static String[] convertBatch(String[] mysqlTypes) {
        if (mysqlTypes == null) {
            return null;
        }
        
        String[] javaTypes = new String[mysqlTypes.length];
        for (int i = 0; i < mysqlTypes.length; i++) {
            javaTypes[i] = convertToJavaType(mysqlTypes[i]);
        }
        
        return javaTypes;
    }
    
    /**
     * 直接处理GlobalTableInfo中的fieldTypes数组
     * 将MySQL类型转换为简化Java类型并重新赋值
     */
    public static void processGlobalTableInfo() {
        GlobalTableInfo instance = GlobalTableInfo.getInstance();
        if (instance.fieldTypes != null) {
            instance.fieldTypes = convertBatch(instance.fieldTypes);
            System.out.println("字段类型已转换为简化Java类型格式");
        }
    }
    
    /**
     * 测试方法
     */
    public static void main(String[] args) {
        System.out.println("=== MySQL类型转换测试 ===");
        
        // 测试各种MySQL类型
        String[] testTypes = {
            "VARCHAR(255)",
            "VARCHAR(20)",
            "VARCHAR(100)",
            "INT",
            "BIGINT",
            "DECIMAL(10,2)",
            "DATETIME",
            "TIMESTAMP",
            "TINYINT(1)",
            "TINYINT",
            "TEXT",
            "BLOB",
            "DOUBLE",
            "FLOAT",
            "DATE",
            "TIME",
            "BOOLEAN",
            "JSON",
            "INT UNSIGNED",
            "BIGINT UNSIGNED"
        };
        
        System.out.println("MySQL类型 -> 简化Java类型:");
        for (String mysqlType : testTypes) {
            String javaType = convertToJavaType(mysqlType);
            System.out.println(String.format("%-20s -> %s", mysqlType, javaType));
        }
        
        // 测试批量转换
        System.out.println("\n=== 批量转换测试 ===");
        String[] batchTypes = {"VARCHAR(50)", "INT", "DATETIME", "DECIMAL(10,2)"};
        String[] convertedTypes = convertBatch(batchTypes);
        
        System.out.println("批量转换结果:");
        for (int i = 0; i < batchTypes.length; i++) {
            System.out.println("  " + batchTypes[i] + " -> " + convertedTypes[i]);
        }
        
        // 测试GlobalTableInfo处理
        System.out.println("\n=== GlobalTableInfo处理测试 ===");
        GlobalTableInfo instance = GlobalTableInfo.getInstance();
        instance.fieldTypes = new String[]{"BIGINT", "VARCHAR(50)", "DATETIME", "TINYINT(1)"};
        
        System.out.println("处理前:");
        for (String type : instance.fieldTypes) {
            System.out.println("  " + type);
        }
        
        processGlobalTableInfo();
        
        System.out.println("处理后:");
        for (String type : instance.fieldTypes) {
            System.out.println("  " + type);
        }
    }
}