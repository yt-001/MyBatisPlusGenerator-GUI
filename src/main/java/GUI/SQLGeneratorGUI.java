package GUI;

import Global.FileConfigurationReadingUtils;
import Global.GlobalTableInfo;
import utils.AutomaticSearchPathUtils;
import utils.TableFieldExtractor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class SQLGeneratorGUI extends JFrame {
    private JTextArea sqlInputArea;
    private JTextArea logOutputArea;
    private JButton generateButton;
    private JButton smartMatchButton;
    private JButton saveConfigButton;

    public SQLGeneratorGUI() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private JTextField projectPathField;
    private JTextField entityPathField;
    private JTextField mapperPathField;
    private JTextField servicePathField;
    private JTextField controllerPathField;
    private JTextField implPathField;
    private JButton projectBrowseButton;
    private JButton entityBrowseButton;
    private JButton mapperBrowseButton;
    private JButton serviceBrowseButton;
    private JButton controllerBrowseButton;
    private JButton implBrowseButton;

    private void initializeComponents() {
        setTitle("MyBatis Plus 代码生成器");
        setSize(600, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        sqlInputArea = new JTextArea();
        Font textFont = new Font("Microsoft YaHei", Font.PLAIN, 10);
        if (!textFont.getFamily().equals("Microsoft YaHei")) {
            textFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
        }
        sqlInputArea.setFont(textFont);
        sqlInputArea.setLineWrap(true);
        sqlInputArea.setWrapStyleWord(true);
        sqlInputArea.setText("-- 请在此输入SQL建表语句\n-- 例如：\n-- CREATE TABLE user (\n--     id BIGINT PRIMARY KEY AUTO_INCREMENT,\n--     name VARCHAR(50) NOT NULL,\n--     email VARCHAR(100)\n-- );");

        generateButton = new JButton("生成代码");
        generateButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        generateButton.setPreferredSize(new Dimension(80, 28));

        smartMatchButton = new JButton("智能匹配");
        smartMatchButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        smartMatchButton.setPreferredSize(new Dimension(80, 28));

        saveConfigButton = new JButton("保存配置文件");
        saveConfigButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        saveConfigButton.setPreferredSize(new Dimension(100, 28));

        initializePathComponents();

        logOutputArea = new JTextArea();
        logOutputArea.setFont(textFont);
        logOutputArea.setEditable(false);
        logOutputArea.setLineWrap(true);
        logOutputArea.setWrapStyleWord(true);
    }

    private void initializePathComponents() {
        Font labelFont = new Font("Microsoft YaHei", Font.PLAIN, 10);
        Font fieldFont = new Font("Microsoft YaHei", Font.PLAIN, 9);
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 8);

        projectPathField = new JTextField();
        projectPathField.setFont(fieldFont);
        projectPathField.setText("C:/Users/DELL/Desktop/MyBatisPlusGenerator");
        projectBrowseButton = new JButton("...");
        projectBrowseButton.setFont(buttonFont);
        projectBrowseButton.setPreferredSize(new Dimension(25, 20));

        entityPathField = new JTextField();
        entityPathField.setFont(fieldFont);
        entityPathField.setText("src/main/java/entity");
        entityBrowseButton = new JButton("...");
        entityBrowseButton.setFont(buttonFont);
        entityBrowseButton.setPreferredSize(new Dimension(25, 20));

        mapperPathField = new JTextField();
        mapperPathField.setFont(fieldFont);
        mapperPathField.setText("src/main/java/mapper");
        mapperBrowseButton = new JButton("...");
        mapperBrowseButton.setFont(buttonFont);
        mapperBrowseButton.setPreferredSize(new Dimension(25, 20));

        servicePathField = new JTextField();
        servicePathField.setFont(fieldFont);
        servicePathField.setText("src/main/java/service");
        serviceBrowseButton = new JButton("...");
        serviceBrowseButton.setFont(buttonFont);
        serviceBrowseButton.setPreferredSize(new Dimension(25, 20));

        controllerPathField = new JTextField();
        controllerPathField.setFont(fieldFont);
        controllerPathField.setText("src/main/java/controller");
        controllerBrowseButton = new JButton("...");
        controllerBrowseButton.setFont(buttonFont);
        controllerBrowseButton.setPreferredSize(new Dimension(25, 20));

        implPathField = new JTextField();
        implPathField.setFont(fieldFont);
        implPathField.setText("src/main/java/service/impl");
        implBrowseButton = new JButton("...");
        implBrowseButton.setFont(buttonFont);
        implBrowseButton.setPreferredSize(new Dimension(25, 20));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("SQL建表语句"));
        leftPanel.setPreferredSize(new Dimension(260, 260));

        JScrollPane scrollPane = new JScrollPane(sqlInputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel pathConfigPanel = createPathConfigPanel();

        JPanel rightWrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rightWrapperPanel.add(pathConfigPanel);

        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.setPreferredSize(new Dimension(0, 200));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(generateButton);
        southContainer.add(buttonPanel, BorderLayout.NORTH);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("日志输出"));
        JScrollPane logScrollPane = new JScrollPane(logOutputArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        southContainer.add(logPanel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightWrapperPanel, BorderLayout.CENTER);
        add(southContainer, BorderLayout.SOUTH);
    }

    private JPanel createPathConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("代码生成路径配置"));
        panel.setPreferredSize(new Dimension(300, 380));

        Font labelFont = new Font("Microsoft YaHei", Font.PLAIN, 10);

        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topButtonPanel.add(smartMatchButton);
        topButtonPanel.add(Box.createHorizontalStrut(5));
        topButtonPanel.add(saveConfigButton);
        topButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(topButtonPanel);

        panel.add(Box.createVerticalStrut(3));
        JLabel projectLabel = new JLabel("项目路径:");
        projectLabel.setFont(labelFont);
        projectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(projectLabel);
        panel.add(Box.createVerticalStrut(2));
        JPanel projectPanel = new JPanel(new BorderLayout(3, 0));
        projectPanel.add(projectPathField, BorderLayout.CENTER);
        projectPanel.add(projectBrowseButton, BorderLayout.EAST);
        projectPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(projectPanel);

        panel.add(Box.createVerticalStrut(5));
        JLabel entityLabel = new JLabel("Entity实体类路径:");
        entityLabel.setFont(labelFont);
        entityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(entityLabel);
        panel.add(Box.createVerticalStrut(2));
        JPanel entityPanel = new JPanel(new BorderLayout(3, 0));
        entityPanel.add(entityPathField, BorderLayout.CENTER);
        entityPanel.add(entityBrowseButton, BorderLayout.EAST);
        entityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(entityPanel);

        panel.add(Box.createVerticalStrut(5));
        JLabel mapperLabel = new JLabel("Mapper接口路径:");
        mapperLabel.setFont(labelFont);
        mapperLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(mapperLabel);
        panel.add(Box.createVerticalStrut(2));
        JPanel mapperPanel = new JPanel(new BorderLayout(3, 0));
        mapperPanel.add(mapperPathField, BorderLayout.CENTER);
        mapperPanel.add(mapperBrowseButton, BorderLayout.EAST);
        mapperPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(mapperPanel);

        panel.add(Box.createVerticalStrut(5));
        JLabel serviceLabel = new JLabel("Service服务类路径:");
        serviceLabel.setFont(labelFont);
        serviceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(serviceLabel);
        panel.add(Box.createVerticalStrut(2));
        JPanel servicePanel = new JPanel(new BorderLayout(3, 0));
        servicePanel.add(servicePathField, BorderLayout.CENTER);
        servicePanel.add(serviceBrowseButton, BorderLayout.EAST);
        servicePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(servicePanel);

        panel.add(Box.createVerticalStrut(5));
        JLabel implLabel = new JLabel("Impl实现类路径:");
        implLabel.setFont(labelFont);
        implLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(implLabel);
        panel.add(Box.createVerticalStrut(2));
        JPanel implPanel = new JPanel(new BorderLayout(3, 0));
        implPanel.add(implPathField, BorderLayout.CENTER);
        implPanel.add(implBrowseButton, BorderLayout.EAST);
        implPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(implPanel);

        panel.add(Box.createVerticalStrut(5));
        JLabel controllerLabel = new JLabel("Controller控制器路径:");
        controllerLabel.setFont(labelFont);
        controllerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(controllerLabel);
        panel.add(Box.createVerticalStrut(2));
        JPanel controllerPanel = new JPanel(new BorderLayout(3, 0));
        controllerPanel.add(controllerPathField, BorderLayout.CENTER);
        controllerPanel.add(controllerBrowseButton, BorderLayout.EAST);
        controllerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(controllerPanel);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void setupEventHandlers() {
        generateButton.addActionListener(e -> generateCode());
        smartMatchButton.addActionListener(e -> smartMatchAndUpdateUI());
        saveConfigButton.addActionListener(e -> saveAndPrintGlobalConfig());
        setupBrowseButtonEvents();
    }

    private void generateCode() {
        String sqlContent = sqlInputArea.getText().trim();
        if (sqlContent.isEmpty() || sqlContent.startsWith("-- 请在此输入")) {
            JOptionPane.showMessageDialog(this, "请输入有效的SQL建表语句！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 更新全局配置
        updateGlobalInfoFromPaths();
        
        try {
            System.out.println("=== 开始代码生成流程 ===");
            
            // 1. 先执行字段解析
            System.out.println("=== 步骤1: 解析SQL ===");
            TableFieldExtractor.TableInfo tableInfo = TableFieldExtractor.parseSql(sqlContent);
            TableFieldExtractor.printCoreInfo(tableInfo);
            
            // 2. 执行所有生成器
            System.out.println("\n=== 步骤2: 生成代码模板 ===");
            GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
            
            // 生成 Entity
            System.out.println("\n--- 生成 Entity 实体类 ---");
            Generator.entityGenerator entityGen = new Generator.entityGenerator();
            entityGen.generateEntityTemplate(globalInfo);
            
            // 生成 Mapper
            System.out.println("\n--- 生成 Mapper 接口 ---");
            Generator.MapperGenerator mapperGen = new Generator.MapperGenerator();
            mapperGen.generateMapperTemplate(globalInfo);
            
            // 生成 Service 和 ServiceImpl
            System.out.println("\n--- 生成 Service 接口和实现类 ---");
            Generator.ServiceGenerator serviceGen = new Generator.ServiceGenerator();
            serviceGen.generateServiceAndImplTemplates(globalInfo);
            
            // 生成 Controller
            System.out.println("\n--- 生成 Controller 控制器 ---");
            Generator.ControllerGenerator controllerGen = new Generator.ControllerGenerator();
            controllerGen.generateControllerTemplate(globalInfo);
            
            System.out.println("\n=== 代码生成完成 ===");
            
            // 询问是否写入文件
            int choice = JOptionPane.showConfirmDialog(this, 
                "代码生成完成！\n表名: " + tableInfo.getTableName() + 
                "\n字段数: " + tableInfo.getFields().size() + 
                "\n已生成: Entity, Mapper, Service, ServiceImpl, Controller" +
                "\n\n是否要将代码写入到文件？", 
                "写入文件", 
                JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    System.out.println("\n=== 开始写入代码文件 ===");
                    Generator.CodeFileWriter.writeAllCodeFiles(globalInfo);
                    JOptionPane.showMessageDialog(this, "代码文件写入完成！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception fileEx) {
                    System.out.println("错误：写入文件时发生异常：" + fileEx.getMessage());
                    JOptionPane.showMessageDialog(this, "写入文件时发生错误：" + fileEx.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    fileEx.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "代码生成完成！仅在控制台显示。", "完成", JOptionPane.INFORMATION_MESSAGE);
            }
                
        } catch (Exception ex) {
            System.out.println("错误：代码生成过程中发生异常：" + ex.getMessage());
            JOptionPane.showMessageDialog(this, "代码生成时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void smartMatchAndUpdateUI() {
        String projectPath = projectPathField.getText().trim();
        if (projectPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先设置项目路径！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        System.out.println("=== 开始智能匹配并更新全局配置 ===");
        AutomaticSearchPathUtils.findAndSetGlobalPaths(projectPath);
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        updatePathFieldFromPackage(entityPathField, globalInfo.entityOrdomainPackage, "Entity/Domain");
        updatePathFieldFromPackage(mapperPathField, globalInfo.mapperPackage, "Mapper");
        updatePathFieldFromPackage(servicePathField, globalInfo.servicePackage, "Service");
        updatePathFieldFromPackage(implPathField, globalInfo.implPackage, "Impl");
        updatePathFieldFromPackage(controllerPathField, globalInfo.controllerPackage, "Controller");
        System.out.println("=== 智能匹配完成 ===");
        JOptionPane.showMessageDialog(this, "路径匹配完成！全局配置已更新。", "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveAndPrintGlobalConfig() {
        updateGlobalInfoFromPaths();
        FileConfigurationReadingUtils.saveConfiguration();
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        System.out.println("========== 当前全局配置 (已保存) ==========");
        System.out.println("项目路径: " + (globalInfo.projectPath != null ? globalInfo.projectPath : "未设置"));
        System.out.println("Entity/Domain包: " + (globalInfo.entityOrdomainPackage != null && !globalInfo.entityOrdomainPackage.isEmpty() ? globalInfo.entityOrdomainPackage : "未找到"));
        System.out.println("Mapper包: " + (globalInfo.mapperPackage != null && !globalInfo.mapperPackage.isEmpty() ? globalInfo.mapperPackage : "未找到"));
        System.out.println("Service包: " + (globalInfo.servicePackage != null && !globalInfo.servicePackage.isEmpty() ? globalInfo.servicePackage : "未找到"));
        System.out.println("Impl包: " + (globalInfo.implPackage != null && !globalInfo.implPackage.isEmpty() ? globalInfo.implPackage : "未找到"));
        System.out.println("Controller包: " + (globalInfo.controllerPackage != null && !globalInfo.controllerPackage.isEmpty() ? globalInfo.controllerPackage : "未找到"));
        System.out.println("==========================================");
        JOptionPane.showMessageDialog(this, "配置已保存到文件并打印到控制台。", "配置已保存", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updatePathFieldFromPackage(JTextField field, String packageName, String type) {
        if (packageName != null && !packageName.isEmpty()) {
            String relativePath = "src/main/java/" + packageName.replace('.', '/');
            
            // 检查路径是否存在
            String projectPath = projectPathField.getText().trim();
            if (!projectPath.isEmpty()) {
                File fullPath = new File(projectPath, relativePath);
                
                if (!fullPath.exists()) {
                    // 路径不存在，询问用户是否创建
                    int choice = JOptionPane.showConfirmDialog(this, 
                        "包路径不存在：" + relativePath + " 是否要创建该包路径？",
                        "创建包路径", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        try {
                            // 创建目录
                            boolean created = fullPath.mkdirs();
                            if (created) {
                                field.setText(relativePath);
                                System.out.println("已创建并更新 " + type + " 路径: " + relativePath);
                            } else {
                                System.out.println("创建 " + type + " 路径失败: " + relativePath);
                                JOptionPane.showMessageDialog(this, 
                                    "创建包路径失败：" + relativePath, 
                                    "创建失败", 
                                    JOptionPane.ERROR_MESSAGE);
                                field.setText("");
                            }
                        } catch (Exception e) {
                            System.out.println("创建 " + type + " 路径时发生异常: " + e.getMessage());
                            JOptionPane.showMessageDialog(this, 
                                "创建包路径时发生错误：" + e.getMessage(), 
                                "创建失败", 
                                JOptionPane.ERROR_MESSAGE);
                            field.setText("");
                        }
                    } else {
                        // 用户选择不创建，清空字段
                        field.setText("");
                        System.out.println("用户选择不创建 " + type + " 路径，UI字段已清空。");
                    }
                } else {
                    // 路径存在，正常设置
                    field.setText(relativePath);
                    System.out.println("更新 " + type + " 路径: " + relativePath);
                }
            } else {
                // 项目路径为空，无法检查，直接设置
                field.setText(relativePath);
                System.out.println("更新 " + type + " 路径: " + relativePath + " (未验证存在性，项目路径为空)");
            }
        } else {
            // 没有找到包名，询问用户是否要创建一个默认的包路径
            String projectPath = projectPathField.getText().trim();
            if (!projectPath.isEmpty()) {
                String defaultPackageName = getDefaultPackageName(type);
                String defaultRelativePath = "src/main/java/" + defaultPackageName;
                
                int choice = JOptionPane.showConfirmDialog(this, 
                    "未找到 " + type + " 包路径。 是否要创建默认的包路径：" + defaultRelativePath + "？",
                    "创建默认包路径", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (choice == JOptionPane.YES_OPTION) {
                    File fullPath = new File(projectPath, defaultRelativePath);
                    try {
                        boolean created = fullPath.mkdirs();
                        if (created) {
                            field.setText(defaultRelativePath);
                            System.out.println("已创建并更新 " + type + " 默认路径: " + defaultRelativePath);
                            
                            // 同时更新全局配置中的包名
                            updateGlobalPackageInfo(type, defaultPackageName);
                        } else {
                            System.out.println("创建 " + type + " 默认路径失败: " + defaultRelativePath);
                            JOptionPane.showMessageDialog(this, 
                                "创建默认包路径失败：" + defaultRelativePath, 
                                "创建失败", 
                                JOptionPane.ERROR_MESSAGE);
                            field.setText("");
                        }
                    } catch (Exception e) {
                        System.out.println("创建 " + type + " 默认路径时发生异常: " + e.getMessage());
                        JOptionPane.showMessageDialog(this, 
                            "创建默认包路径时发生错误：" + e.getMessage(), 
                            "创建失败", 
                            JOptionPane.ERROR_MESSAGE);
                        field.setText("");
                    }
                } else {
                    field.setText("");
                    System.out.println("用户选择不创建 " + type + " 默认路径，UI字段已清空。");
                }
            } else {
                field.setText("");
                System.out.println("未找到 " + type + " 的路径，且项目路径为空，UI字段已清空。");
            }
        }
    }

    private void updateGlobalInfoFromPaths() {
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        String projectPath = projectPathField.getText().trim();
        if (!projectPath.isEmpty()) {
            globalInfo.projectPath = new File(projectPath).getAbsolutePath();
        }
        globalInfo.entityOrdomainPackage = convertPathToPackage(entityPathField.getText().trim());
        globalInfo.mapperPackage = convertPathToPackage(mapperPathField.getText().trim());
        globalInfo.servicePackage = convertPathToPackage(servicePathField.getText().trim());
        globalInfo.implPackage = convertPathToPackage(implPathField.getText().trim());
        globalInfo.controllerPackage = convertPathToPackage(controllerPathField.getText().trim());
    }

    private String convertPathToPackage(String path) {
        if (path == null || path.trim().isEmpty()) return "";
        String packagePath = path.replace("\\", "/");
        String[] prefixes = {"src/main/java/", "src/test/java/", "src/"};
        for (String prefix : prefixes) {
            if (packagePath.startsWith(prefix)) {
                packagePath = packagePath.substring(prefix.length());
                break;
            }
        }
        return packagePath.replace('/', '.');
    }

    private void setupBrowseButtonEvents() {
        projectBrowseButton.addActionListener(e -> selectFolder(projectPathField, true, "选择项目根目录"));
        entityBrowseButton.addActionListener(e -> selectFolder(entityPathField, false, "选择Entity实体类路径"));
        mapperBrowseButton.addActionListener(e -> selectFolder(mapperPathField, false, "选择Mapper接口路径"));
        serviceBrowseButton.addActionListener(e -> selectFolder(servicePathField, false, "选择Service服务类路径"));
        implBrowseButton.addActionListener(e -> selectFolder(implPathField, false, "选择Impl实现类路径"));
        controllerBrowseButton.addActionListener(e -> selectFolder(controllerPathField, false, "选择Controller控制器路径"));
    }

    private void selectFolder(JTextField pathField, boolean isProjectPath, String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(title);
        fileChooser.setApproveButtonText("打开");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

        String currentPath;
        if (isProjectPath) {
            currentPath = pathField.getText();
        } else {
            currentPath = projectPathField.getText() + "/" + pathField.getText();
        }

        java.io.File currentDir = new java.io.File(currentPath);
        if (currentDir.exists()) {
            fileChooser.setCurrentDirectory(currentDir);
        }

        int result = fileChooser.showOpenDialog(SQLGeneratorGUI.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            if (isProjectPath) {
                pathField.setText(selectedFile.getAbsolutePath());
            } else {
                String selectedPath = selectedFile.getAbsolutePath();
                String projectPath = projectPathField.getText();

                if (selectedPath.startsWith(projectPath)) {
                    String relativePath = selectedPath.substring(projectPath.length());
                    if (relativePath.startsWith("\\") || relativePath.startsWith("/")) {
                        relativePath = relativePath.substring(1);
                    }
                    pathField.setText(relativePath.replace("\\", "/"));
                } else {
                    pathField.setText(selectedPath.replace("\\", "/"));
                }
            }
        }
    }

    private void updateUIFromGlobalConfig() {
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        projectPathField.setText(globalInfo.projectPath != null ? globalInfo.projectPath : "");
        updatePathFieldFromPackage(entityPathField, globalInfo.entityOrdomainPackage, "Entity/Domain");
        updatePathFieldFromPackage(mapperPathField, globalInfo.mapperPackage, "Mapper");
        updatePathFieldFromPackage(servicePathField, globalInfo.servicePackage, "Service");
        updatePathFieldFromPackage(implPathField, globalInfo.implPackage, "Impl");
        updatePathFieldFromPackage(controllerPathField, globalInfo.controllerPackage, "Controller");
    }

    /**
     * 获取默认包名
     */
    private String getDefaultPackageName(String type) {
        switch (type.toLowerCase()) {
            case "entity/domain":
            case "entity":
                return "domain/entity";  // 先创建domain包，再创建entity子包
            case "mapper":
                return "mapper";
            case "service":
                return "service";
            case "impl":
                return "service/impl";   // 先创建service包，再创建impl子包
            case "controller":
                return "controller";
            default:
                return type.toLowerCase();
        }
    }
    
    /**
     * 更新全局配置中的包信息
     */
    private void updateGlobalPackageInfo(String type, String packageName) {
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        switch (type.toLowerCase()) {
            case "entity/domain":
            case "entity":
                globalInfo.entityOrdomainPackage = packageName;
                break;
            case "mapper":
                globalInfo.mapperPackage = packageName;
                break;
            case "service":
                globalInfo.servicePackage = packageName;
                break;
            case "impl":
                globalInfo.implPackage = packageName;
                break;
            case "controller":
                globalInfo.controllerPackage = packageName;
                break;
        }
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");

        FileConfigurationReadingUtils.loadConfiguration();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            SQLGeneratorGUI gui = new SQLGeneratorGUI();
            gui.updateUIFromGlobalConfig();
            gui.setVisible(true);

            TextAreaOutputStream outStream = new TextAreaOutputStream(gui.logOutputArea, false);
            TextAreaOutputStream errStream = new TextAreaOutputStream(gui.logOutputArea, true);
            try {
                java.io.PrintStream outPrintStream = new java.io.PrintStream(outStream, true, "UTF-8");
                System.setOut(outPrintStream);
                java.io.PrintStream errPrintStream = new java.io.PrintStream(errStream, true, "UTF-8");
                System.setErr(errPrintStream);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            System.out.println("MyBatis Plus 代码生成器已启动。");
            System.out.println("日志输出已重定向到此区域。");
        });
    }

    static class TextAreaOutputStream extends java.io.OutputStream {
        private final JTextArea textArea;
        private final java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        private final int MAX_LINES = 1000;
        private final boolean filterIntelliJExceptions;

        public TextAreaOutputStream(JTextArea textArea, boolean filter) {
            this.textArea = textArea;
            this.filterIntelliJExceptions = filter;
        }

        @Override
        public synchronized void write(int b) {
            buffer.write(b);
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) {
            buffer.write(b, off, len);
        }

        @Override
        public synchronized void flush() {
            if (buffer.size() == 0) {
                return;
            }

            final String text = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
            buffer.reset();

            if (filterIntelliJExceptions) {
                if (text.contains("com.intellij.codeInsight.editorActions.FoldingData") ||
                    text.contains("com.intellij.openapi.editor.impl.EditorCopyPasteHelperImpl$CopyPasteOptionsTransferableData")) {
                    return;
                }
            }

            SwingUtilities.invokeLater(() -> {
                textArea.append(text);
                limitLines();
            });
        }

        private void limitLines() {
            try {
                if (textArea.getLineCount() > MAX_LINES) {
                    int end = textArea.getLineStartOffset(textArea.getLineCount() - MAX_LINES);
                    textArea.replaceRange("", 0, end);
                }
                textArea.setCaretPosition(textArea.getDocument().getLength());
            } catch (Exception ex) {
            }
        }
    }
}