package Global;

public class GlobalTableInfo {

    // 1. 唯一实例
    private static final GlobalTableInfo INSTANCE = new GlobalTableInfo();

    // 2. 实例字段（非 static）
    public String tableName;
    public String[] fieldNames;
    public String[] fieldTypes;
    public String[] fieldAnnotations;

    // Project项目路径
    public String projectPath;
    // Mapper包名
    public String mapperPackage;
    // Service包名
    public String servicePackage;
    // Controller包名
    public String controllerPackage;
    // Entity或Domain包名
    public String entityOrdomainPackage;
    // Impl包名
    public String implPackage;
    

    // 3. 私有构造，防止其他地方 new
    private GlobalTableInfo() { }

    // 4. 全局访问点
    public static GlobalTableInfo getInstance() {
        return INSTANCE;
    }
}