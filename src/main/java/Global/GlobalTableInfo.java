package Global;

public class GlobalTableInfo {

    // 1. 唯一实例
    private static final GlobalTableInfo INSTANCE = new GlobalTableInfo();

    // 2. 实例字段（非 static）
    public String tableName;
    public String[] fieldNames;
    public String[] fieldTypes;

    // 3. 私有构造，防止其他地方 new
    private GlobalTableInfo() { }

    // 4. 全局访问点
    public static GlobalTableInfo getInstance() {
        return INSTANCE;
    }
}