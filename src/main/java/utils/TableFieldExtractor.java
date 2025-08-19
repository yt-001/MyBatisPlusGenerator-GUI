package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Global.GlobalTableInfo;

/**
 * 动态SQL表字段提取器
 * 用于从任意SQL建表语句中提取表名和字段信息
 */
public class TableFieldExtractor {
    /**
     * 表信息类
     */
    public static class TableInfo {
        private String tableName;
        private String tableComment;
        private List<FieldInfo> fields;
        
        public TableInfo() {
            this.fields = new ArrayList<>();
        }
        
        // getter和setter方法
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        public String getTableComment() { return tableComment; }
        public void setTableComment(String tableComment) { this.tableComment = tableComment; }
        public List<FieldInfo> getFields() { return fields; }
        public void setFields(List<FieldInfo> fields) { this.fields = fields; }
    }
    
    /**
     * 字段信息类
     */
    public static class FieldInfo {
        private String fieldName;
        private String fieldType;
        private String comment;
        
        public FieldInfo(String fieldName, String fieldType, String comment) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.comment = comment;
        }
        
        // getter和setter方法
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        public String getFieldType() { return fieldType; }
        public void setFieldType(String fieldType) { this.fieldType = fieldType; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }
    
    /**
     * 解析SQL建表语句
     * @param sql SQL建表语句
     * @return 表信息
     */
    public static TableInfo parseSql(String sql) {
        TableInfo tableInfo = new TableInfo();
        
        // 提取表名
        String tableName = extractTableName(sql);
        tableInfo.setTableName(tableName);
        
        // 提取表注释
        String tableComment = extractTableComment(sql);
        tableInfo.setTableComment(tableComment);
        
        // 提取字段信息
        List<FieldInfo> fields = extractFields(sql);
        tableInfo.setFields(fields);
        
        // 设置全局变量
        setGlobalVariables(tableInfo);
        
        return tableInfo;
    }
    
    /**
     * 设置全局变量
     * @param tableInfo 表信息
     */
    private static void setGlobalVariables(TableInfo tableInfo) {
        GlobalTableInfo globalTableInfo = GlobalTableInfo.getInstance();
        // 设置全局表名
        globalTableInfo.tableName = tableInfo.getTableName();

        // 设置全局字段名数组
        globalTableInfo.fieldNames = new String[tableInfo.getFields().size()];
        for (int i = 0; i < tableInfo.getFields().size(); i++) {
            globalTableInfo.fieldNames[i] = tableInfo.getFields().get(i).getFieldName();
        }

        // 设置全局字段类型数组
        globalTableInfo.fieldTypes = new String[tableInfo.getFields().size()];
        for (int i = 0; i < tableInfo.getFields().size(); i++) {
            globalTableInfo.fieldTypes[i] = tableInfo.getFields().get(i).getFieldType();
        }

        // 设置全局字段注释数组
        globalTableInfo.fieldAnnotations = new String[tableInfo.getFields().size()];
        for (int i = 0; i < tableInfo.getFields().size(); i++) {
            String comment = tableInfo.getFields().get(i).getComment();
            globalTableInfo.fieldAnnotations[i] = (comment != null && !comment.trim().isEmpty()) ? comment : "";
        }
    }
    
    /**
     * 提取表名
     */
    private static String extractTableName(String sql) {
        Pattern pattern = Pattern.compile("create\\s+table\\s+([\\w_]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown_table";
    }
    
    /**
     * 提取表注释
     */
    private static String extractTableComment(String sql) {
        Pattern pattern = Pattern.compile("comment\\s*=\\s*'([^']*)'", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    
    /**
     * 提取字段信息
     */
    private static List<FieldInfo> extractFields(String sql) {
        List<FieldInfo> fields = new ArrayList<>();
        
        String[] lines = sql.split("\n");
        boolean inCreateTableBlock = false;
        
        for (String line : lines) {
            line = line.trim();
            
            // 检测是否进入CREATE TABLE块
            if (line.toLowerCase().startsWith("create table")) {
                inCreateTableBlock = true;
                continue;
            }
            
            // 检测是否离开CREATE TABLE块（遇到独立的CREATE INDEX等语句）
            if (line.toLowerCase().startsWith("create index") || 
                line.toLowerCase().startsWith("create unique index")) {
                inCreateTableBlock = false;
                continue;
            }
            
            // 只在CREATE TABLE块内解析字段
            if (!inCreateTableBlock) {
                continue;
            }
            
            // 跳过非字段行
            if (line.isEmpty() || 
                line.toLowerCase().startsWith("constraint") ||
                line.toLowerCase().startsWith("unique") ||
                line.toLowerCase().startsWith("primary key") ||
                line.startsWith(")") ||
                line.startsWith("(")) {
                continue;
            }
            
            // 检测CREATE TABLE块结束（遇到以)结尾的行）
            if (line.endsWith(");") || (line.equals(")") || line.matches("\\)\\s*comment.*"))) {
                inCreateTableBlock = false;
                continue;
            }
            
            // 解析字段
            FieldInfo fieldInfo = parseFieldLine(line);
            if (fieldInfo != null) {
                fields.add(fieldInfo);
            }
        }
        
        return fields;
    }
    
    /**
     * 解析单行字段定义 - 增强版注释提取
     */
    private static FieldInfo parseFieldLine(String line) {
        // 移除末尾的逗号
        line = line.replaceAll(",$", "").trim();
        
        // 跳过单独的"on update"约束行，但不跳过字段定义行
        String trimmedLower = line.toLowerCase().trim();
        if (trimmedLower.startsWith("on update") && !trimmedLower.matches(".*\\w+_time\\s+.*")) {
            return null;
        }
        
        // 增强的字段匹配正则表达式，支持多种注释格式
        // 支持格式：
        // 1. COMMENT '注释内容'
        // 2. COMMENT "注释内容"
        // 3. COMMENT `注释内容`
        // 4. comment='注释内容'
        // 5. comment="注释内容"
        Pattern pattern = Pattern.compile(
            "^\\s*([\\w_]+)\\s+([\\w()]+(?:\\s+unsigned)?)" +  // 字段名和类型（支持unsigned）
            "(?:\\s+.*?)?" +                                    // 中间的约束条件（可选）
            "(?:\\s+comment\\s*[=\\s]*['\"`]([^'\"`]*)['\"`])?" + // 注释部分（支持多种引号）
            ".*$",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String fieldName = matcher.group(1);
            String fieldType = matcher.group(2).trim();
            String comment = matcher.group(3);
            
            // 验证字段名不是SQL关键字
            if (isValidFieldName(fieldName)) {
                // 清理字段类型，移除多余的关键字
                fieldType = cleanFieldType(fieldType);
                
                // 如果没有通过正则匹配到注释，尝试其他方式提取
                if (comment == null || comment.trim().isEmpty()) {
                    comment = extractCommentFromLine(line);
                }
                
                return new FieldInfo(fieldName, fieldType, comment != null ? comment.trim() : "");
            }
        }
        
        return null;
    }
    
    /**
     * 从字段定义行中提取注释的辅助方法
     * 处理各种复杂的注释格式
     */
    private static String extractCommentFromLine(String line) {
        // 尝试多种注释提取模式
        String[] commentPatterns = {
            "comment\\s*=\\s*'([^']*)'",           // comment='注释'
            "comment\\s*=\\s*\"([^\"]*)\"",       // comment="注释"
            "comment\\s*=\\s*`([^`]*)`",          // comment=`注释`
            "comment\\s+'([^']*)'",               // comment '注释'
            "comment\\s+\"([^\"]*)\"",            // comment "注释"
            "comment\\s+`([^`]*)`",               // comment `注释`
            "comment\\s*:\\s*'([^']*)'",          // comment: '注释'
            "comment\\s*:\\s*\"([^\"]*)\"",       // comment: "注释"
        };
        
        for (String patternStr : commentPatterns) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String comment = matcher.group(1);
                if (comment != null && !comment.trim().isEmpty()) {
                    return comment.trim();
                }
            }
        }
        
        return "";
    }
    
    /**
     * 验证字段名是否有效（不是SQL关键字）
     */
    private static boolean isValidFieldName(String fieldName) {
        // 过滤SQL关键字，但不过滤包含关键字的字段名（如create_time, update_time）
        String[] sqlKeywords = {"on", "primary", "key", "constraint", "unique", "index", "foreign", "create", "table", "alter", "drop"};
        String lowerFieldName = fieldName.toLowerCase();
        
        // 如果是纯关键字，则无效
        for (String keyword : sqlKeywords) {
            if (keyword.equals(lowerFieldName)) {
                return false;
            }
        }
        
        // 额外检查：字段名必须是有效的标识符格式（字母开头，包含字母数字下划线）
        if (!fieldName.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 清理字段类型
     */
    private static String cleanFieldType(String fieldType) {
        // 移除常见的约束关键字，只保留数据类型
        return fieldType.replaceAll("(?i)\\s*(auto_increment|not null|null|default\\s+[^\\s]+).*", "").trim();
    }
    
    /**
     * 打印核心信息：表名、字段数、字段名数组、字段类型数组
     */
    public static void printCoreInfo(TableInfo tableInfo) {
        System.out.println("=== 核心信息提取结果 ===");
        System.out.println("表名: " + tableInfo.getTableName());
        System.out.println("字段数: " + tableInfo.getFields().size());
        
        System.out.println("\n=== 字段名数组 ===");
        System.out.print("String[] fields = {");
        for (int i = 0; i < tableInfo.getFields().size(); i++) {
            System.out.print("\"" + tableInfo.getFields().get(i).getFieldName() + "\"");
            if (i < tableInfo.getFields().size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("};");
        
        // 调用字段类型数组打印方法
        printFieldTypes(tableInfo);
    }
    
    /**
     * 获取字段名数组
     */
    public static String[] getFieldNames(TableInfo tableInfo) {
        String[] fieldNames = new String[tableInfo.getFields().size()];
        for (int i = 0; i < tableInfo.getFields().size(); i++) {
            fieldNames[i] = tableInfo.getFields().get(i).getFieldName();
        }
        return fieldNames;
    }
    
    /**
     * 获取字段类型数组
     */
    public static String[] getFieldTypes(TableInfo tableInfo) {
        String[] fieldTypes = new String[tableInfo.getFields().size()];
        for (int i = 0; i < tableInfo.getFields().size(); i++) {
            fieldTypes[i] = tableInfo.getFields().get(i).getFieldType();
        }
        return fieldTypes;
    }
    
    /**
     * 获取字段注释数组
     */
    public static String[] getFieldComments(TableInfo tableInfo) {
        String[] fieldComments = new String[tableInfo.getFields().size()];
        for (int i = 0; i < tableInfo.getFields().size(); i++) {
            fieldComments[i] = tableInfo.getFields().get(i).getComment();
        }
        return fieldComments;
    }
    
    /**
     * 打印字段类型数组信息
     */
    public static void printFieldTypes(TableInfo tableInfo) {
        System.out.println("\n=== 字段类型数组 ===");
        System.out.print("String[] fieldTypes = {");
        for (int i = 0; i < tableInfo.getFields().size(); i++) {
            System.out.print("\"" + tableInfo.getFields().get(i).getFieldType() + "\"");
            if (i < tableInfo.getFields().size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("};");
    }
}