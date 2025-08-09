import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        
        return tableInfo;
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
        for (String line : lines) {
            line = line.trim();
            
            // 跳过非字段行
            if (line.isEmpty() || 
                line.toLowerCase().startsWith("create") ||
                line.toLowerCase().startsWith("constraint") ||
                line.toLowerCase().startsWith("unique") ||
                line.toLowerCase().startsWith("primary key") ||
                line.toLowerCase().contains("index") ||
                line.startsWith(")") ||
                line.startsWith("(")) {
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
     * 解析单行字段定义
     */
    private static FieldInfo parseFieldLine(String line) {
        // 移除末尾的逗号
        line = line.replaceAll(",$", "").trim();
        
        // 基本字段匹配
        Pattern pattern = Pattern.compile(
            "^([\\w_]+)\\s+([\\w()\\s,]+?)(?:\\s+comment\\s+'([^']*)')?.*$",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String fieldName = matcher.group(1);
            String fieldType = matcher.group(2).trim();
            String comment = matcher.group(3);
            
            // 清理字段类型，移除多余的关键字
            fieldType = cleanFieldType(fieldType);
            
            return new FieldInfo(fieldName, fieldType, comment != null ? comment : "");
        }
        
        return null;
    }
    
    /**
     * 清理字段类型
     */
    private static String cleanFieldType(String fieldType) {
        // 移除常见的约束关键字，只保留数据类型
        return fieldType.replaceAll("(?i)\\s*(auto_increment|not null|null|default\\s+[^\\s]+).*", "").trim();
    }
    
    /**
     * 打印核心信息：表名、字段数、字段名数组
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
     * 主方法，用于测试SQL解析
     */
    public static void main(String[] args) {
        // 示例SQL语句 - JDK 1.8兼容写法
        String sql = "create table user_basic\n" +
                "(\n" +
                "    user_id        bigint auto_increment comment '用户ID，主键'\n" +
                "        primary key,\n" +
                "    username       varchar(32)                                                   not null comment '用户名',\n" +
                "    password       varchar(255)                                                  not null comment '加密后的密码',\n" +
                "    mobile         varchar(20)                                                   null comment '手机号',\n" +
                "    email          varchar(100)                                                  null comment '邮箱',\n" +
                "    avatar_url     varchar(255) default 'https://example.com/default_avatar.png' null comment '头像URL',\n" +
                "    background_url varchar(255) default 'https://example.com/default_avatar.png' null comment '背景URL',\n" +
                "    signature      varchar(50)  default '这个人很懒，什么都没留下~'               null comment '个性签名',\n" +
                "    gender         tinyint      default 0                                        null comment '性别:0-未知,1-男,2-女',\n" +
                "    birthday       date                                                          null comment '生日',\n" +
                "    country        varchar(10)  default '中国'                                   null comment '国家',\n" +
                "    province       varchar(10)                                                   null comment '省份',\n" +
                "    city           varchar(10)                                                   null comment '城市',\n" +
                "    status         tinyint      default 1                                        null comment '账号状态:0-禁用,1-正常,2-锁定',\n" +
                "    is_verified    tinyint      default 0                                        null comment '是否认证:0-否,1-是',\n" +
                "    verified_type  tinyint                                                       null comment '认证类型:1-个人,2-企业,3-机构',\n" +
                "    create_time    datetime     default CURRENT_TIMESTAMP                        not null comment '创建时间',\n" +
                "    update_time    datetime     default CURRENT_TIMESTAMP                        not null on update CURRENT_TIMESTAMP comment '更新时间',\n" +
                "    constraint idx_mobile\n" +
                "        unique (mobile),\n" +
                "    constraint idx_username\n" +
                "        unique (username)\n" +
                ")\n" +
                "    comment '用户基础信息表' collate = utf8mb4_unicode_ci;";

        // 解析SQL并打印核心结果
        TableInfo tableInfo = parseSql(sql);
        printCoreInfo(tableInfo);
    }
}