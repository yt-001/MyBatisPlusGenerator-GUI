package utils;

import Global.GlobalTableInfo;

/**
 * 字段处理工具类
 * 用于处理数据库字段名称，将下划线命名转换为驼峰命名
 */
public class FieldProcessorUtils {
    
    /**
     * 将下划线命名转换为驼峰命名
     * 例如：user_id -> userId, user_name -> userName
     * 
     * @param fieldName 原始字段名（下划线命名）
     * @return 转换后的驼峰命名
     */
    public static String toCamelCase(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return fieldName;
        }
        
        // 转换为小写并按下划线分割
        String[] parts = fieldName.toLowerCase().split("_");
        
        // 如果只有一个部分，直接返回
        if (parts.length == 1) {
            return parts[0];
        }
        
        StringBuilder camelCase = new StringBuilder();
        
        // 第一个部分保持小写
        camelCase.append(parts[0]);
        
        // 后续部分首字母大写
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].length() > 0) {
                camelCase.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    camelCase.append(parts[i].substring(1));
                }
            }
        }
        
        return camelCase.toString();
    }
    
    /**
     * 批量处理字段名数组，将所有下划线命名转换为驼峰命名
     * 
     * @param fieldNames 原始字段名数组
     * @return 转换后的驼峰命名数组
     */
    public static String[] processBatch(String[] fieldNames) {
        if (fieldNames == null) {
            return null;
        }
        
        String[] processedFields = new String[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            processedFields[i] = toCamelCase(fieldNames[i]);
        }
        
        return processedFields;
    }
    
    /**
     * 直接处理GlobalTableInfo中的fieldNames数组
     * 将原数组中的下划线命名转换为驼峰命名并重新赋值
     */
    public static void processGlobalTableInfo() {
        GlobalTableInfo globalTableInfo = GlobalTableInfo.getInstance();
        if (globalTableInfo.fieldNames != null) {
            globalTableInfo.fieldNames = processBatch(globalTableInfo.fieldNames);
            System.out.println("字段名称已转换为驼峰命名格式");
        }
    }
    
    /**
     * 测试方法
     */
    public static void main(String[] args) {
        GlobalTableInfo globalTableInfo = GlobalTableInfo.getInstance();
        // 测试单个字段转换
        System.out.println("=== 单个字段转换测试 ===");
        System.out.println("user_id -> " + toCamelCase("user_id"));
        System.out.println("user_name -> " + toCamelCase("user_name"));
        System.out.println("create_time -> " + toCamelCase("create_time"));
        System.out.println("is_deleted -> " + toCamelCase("is_deleted"));
        System.out.println("phone_number -> " + toCamelCase("phone_number"));
        
        // 测试批量转换
        System.out.println("\n=== 批量转换测试 ===");
        String[] testFields = {"user_id", "user_name", "email_address", "create_time", "update_time"};
        String[] processedFields = processBatch(testFields);
        
        System.out.println("原始字段:");
        for (String field : testFields) {
            System.out.println("  " + field);
        }
        
        System.out.println("转换后字段:");
        for (String field : processedFields) {
            System.out.println("  " + field);
        }
        
        // 测试GlobalTableInfo处理
        System.out.println("\n=== GlobalTableInfo处理测试 ===");
        globalTableInfo.fieldNames = new String[]{"user_id", "user_name", "phone_number", "create_time"};
        
        System.out.println("处理前:");
        for (String field : globalTableInfo.fieldNames) {
            System.out.println("  " + field);
        }
        
        processGlobalTableInfo();
        
        System.out.println("处理后:");
        for (String field : globalTableInfo.fieldNames) {
            System.out.println("  " + field);
        }
    }
}