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
     * 将字符串的首字母大写。
     * 例如：userName -> UserName
     *
     * @param str 原始字符串
     * @return 首字母大写后的字符串
     */
    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
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
}