package GUI;

import Global.FileConfigurationReadingUtils;
import Global.GlobalTableInfo;
import utils.AutomaticSearchPathUtils;
import utils.TableFieldExtractor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class SQLGeneratorGUI extends JFrame {
    private JTextArea sqlInputArea;
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
    private JButton projectBrowseButton;
    private JButton entityBrowseButton;
    private JButton mapperBrowseButton;
    private JButton serviceBrowseButton;
    private JButton controllerBrowseButton;
    
    private void initializeComponents() {
        setTitle("MyBatis Plus 代码生成器");
        setSize(900, 550);  // 增加宽度以容纳右侧区域
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 创建SQL输入区域
        sqlInputArea = new JTextArea();
        // 使用支持中文的字体，优先使用系统默认字体
        Font textFont = new Font("Microsoft YaHei", Font.PLAIN, 12);
        if (!textFont.getFamily().equals("Microsoft YaHei")) {
            // 如果微软雅黑不可用，使用系统默认字体
            textFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        }
        sqlInputArea.setFont(textFont);
        sqlInputArea.setLineWrap(true);
        sqlInputArea.setWrapStyleWord(true);
        sqlInputArea.setText("-- 请在此输入SQL建表语句\n-- 例如：\n-- CREATE TABLE user (\n--     id BIGINT PRIMARY KEY AUTO_INCREMENT,\n--     name VARCHAR(50) NOT NULL,\n--     email VARCHAR(100)\n-- );");
        
        // 创建生成按钮
        generateButton = new JButton("生成代码");
        generateButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        generateButton.setPreferredSize(new Dimension(120, 35));
        
        // 创建智能匹配按钮
        smartMatchButton = new JButton("智能匹配");
        smartMatchButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        smartMatchButton.setPreferredSize(new Dimension(120, 35));
        
        // 创建保存配置文件按钮
        saveConfigButton = new JButton("保存配置文件");
        saveConfigButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        saveConfigButton.setPreferredSize(new Dimension(140, 35));
        
        // 创建右侧路径配置组件
        initializePathComponents();
    }
    
    private void initializePathComponents() {
        Font labelFont = new Font("Microsoft YaHei", Font.PLAIN, 12);
        Font fieldFont = new Font("Microsoft YaHei", Font.PLAIN, 11);
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 10);
        
        // 项目路径
        projectPathField = new JTextField();
        projectPathField.setFont(fieldFont);
        projectPathField.setText("C:/Users/DELL/Desktop/MyBatisPlusGenerator");
        projectBrowseButton = new JButton("...");
        projectBrowseButton.setFont(buttonFont);
        projectBrowseButton.setPreferredSize(new Dimension(30, 25));
        
        // Entity路径
        entityPathField = new JTextField();
        entityPathField.setFont(fieldFont);
        entityPathField.setText("src/main/java/entity");
        entityBrowseButton = new JButton("...");
        entityBrowseButton.setFont(buttonFont);
        entityBrowseButton.setPreferredSize(new Dimension(30, 25));
        
        // Mapper路径
        mapperPathField = new JTextField();
        mapperPathField.setFont(fieldFont);
        mapperPathField.setText("src/main/java/mapper");
        mapperBrowseButton = new JButton("...");
        mapperBrowseButton.setFont(buttonFont);
        mapperBrowseButton.setPreferredSize(new Dimension(30, 25));
        
        // Service路径
        servicePathField = new JTextField();
        servicePathField.setFont(fieldFont);
        servicePathField.setText("src/main/java/service");
        serviceBrowseButton = new JButton("...");
        serviceBrowseButton.setFont(buttonFont);
        serviceBrowseButton.setPreferredSize(new Dimension(30, 25));
        
        // Controller路径
        controllerPathField = new JTextField();
        controllerPathField.setFont(fieldFont);
        controllerPathField.setText("src/main/java/controller");
        controllerBrowseButton = new JButton("...");
        controllerBrowseButton.setFont(buttonFont);
        controllerBrowseButton.setPreferredSize(new Dimension(30, 25));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());

        // 左侧 - SQL输入区域
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("SQL建表语句"));
        leftPanel.setPreferredSize(new Dimension(400, 400));

        JScrollPane scrollPane = new JScrollPane(sqlInputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // 右侧 - 路径配置区域
        JPanel pathConfigPanel = createPathConfigPanel();

        // 创建一个包装面板，使用FlowLayout左对齐来消除空白
        JPanel rightWrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rightWrapperPanel.add(pathConfigPanel);

        // 底部 - 按钮区域
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(generateButton);

        // 添加到主窗口
        add(leftPanel, BorderLayout.WEST);
        add(rightWrapperPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createPathConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("代码生成路径配置"));
        // 设置一个首选尺寸，以便包装它的FlowLayout可以正确定位
        panel.setPreferredSize(new Dimension(450, 420));

        Font labelFont = new Font("Microsoft YaHei", Font.PLAIN, 12);

        // 在顶部添加智能匹配按钮和保存配置按钮
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topButtonPanel.add(smartMatchButton);
        topButtonPanel.add(Box.createHorizontalStrut(10)); // 添加间距
        topButtonPanel.add(saveConfigButton);
        topButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 确保按钮面板也左对齐
        panel.add(topButtonPanel);
        
        // 项目路径配置
        panel.add(Box.createVerticalStrut(10));
        JLabel projectLabel = new JLabel("项目路径:");
        projectLabel.setFont(labelFont);
        projectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(projectLabel);
        panel.add(Box.createVerticalStrut(5));
        JPanel projectPanel = new JPanel(new BorderLayout(5, 0));
        projectPanel.add(projectPathField, BorderLayout.CENTER);
        projectPanel.add(projectBrowseButton, BorderLayout.EAST);
        projectPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(projectPanel);
        
        // Entity路径配置
        panel.add(Box.createVerticalStrut(15));
        JLabel entityLabel = new JLabel("Entity实体类路径:");
        entityLabel.setFont(labelFont);
        entityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(entityLabel);
        panel.add(Box.createVerticalStrut(5));
        JPanel entityPanel = new JPanel(new BorderLayout(5, 0));
        entityPanel.add(entityPathField, BorderLayout.CENTER);
        entityPanel.add(entityBrowseButton, BorderLayout.EAST);
        entityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(entityPanel);
        
        // Mapper路径配置
        panel.add(Box.createVerticalStrut(15));
        JLabel mapperLabel = new JLabel("Mapper接口路径:");
        mapperLabel.setFont(labelFont);
        mapperLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(mapperLabel);
        panel.add(Box.createVerticalStrut(5));
        JPanel mapperPanel = new JPanel(new BorderLayout(5, 0));
        mapperPanel.add(mapperPathField, BorderLayout.CENTER);
        mapperPanel.add(mapperBrowseButton, BorderLayout.EAST);
        mapperPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(mapperPanel);
        
        // Service路径配置
        panel.add(Box.createVerticalStrut(15));
        JLabel serviceLabel = new JLabel("Service服务类路径:");
        serviceLabel.setFont(labelFont);
        serviceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(serviceLabel);
        panel.add(Box.createVerticalStrut(5));
        JPanel servicePanel = new JPanel(new BorderLayout(5, 0));
        servicePanel.add(servicePathField, BorderLayout.CENTER);
        servicePanel.add(serviceBrowseButton, BorderLayout.EAST);
        servicePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(servicePanel);
        
        // Controller路径配置
        panel.add(Box.createVerticalStrut(15));
        JLabel controllerLabel = new JLabel("Controller控制器路径:");
        controllerLabel.setFont(labelFont);
        controllerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(controllerLabel);
        panel.add(Box.createVerticalStrut(5));
        JPanel controllerPanel = new JPanel(new BorderLayout(5, 0));
        controllerPanel.add(controllerPathField, BorderLayout.CENTER);
        controllerPanel.add(controllerBrowseButton, BorderLayout.EAST);
        controllerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(controllerPanel);
        
        // 添加弹性空间
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // 生成代码按钮事件
        generateButton.addActionListener(e -> generateCode());

        // 智能匹配按钮事件
        smartMatchButton.addActionListener(e -> smartMatchAndUpdateUI());

        // 保存配置按钮事件
        saveConfigButton.addActionListener(e -> saveAndPrintGlobalConfig());

        // 文件夹选择事件处理
        setupBrowseButtonEvents();
    }

    /**
     * “生成代码”按钮的逻辑
     */
    private void generateCode() {
        String sqlContent = sqlInputArea.getText().trim();
        if (sqlContent.isEmpty() || sqlContent.startsWith("-- 请在此输入")) {
            JOptionPane.showMessageDialog(this, "请输入有效的SQL建表语句！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 在生成代码前，确保全局配置与UI上的路径一致
        updateGlobalInfoFromPaths();

        try {
            System.out.println("=== 开始解析SQL ===");
            TableFieldExtractor.TableInfo tableInfo = TableFieldExtractor.parseSql(sqlContent);
            TableFieldExtractor.printCoreInfo(tableInfo);
            JOptionPane.showMessageDialog(this, "SQL解析成功！\n表名: " + tableInfo.getTableName() + "\n字段数: " + tableInfo.getFields().size() + "\n请查看控制台输出详细信息。", "解析成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "处理SQL时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * “智能匹配”按钮的逻辑：搜索路径、更新全局变量，并刷新UI。
     */
    private void smartMatchAndUpdateUI() {
        String projectPath = projectPathField.getText().trim();
        if (projectPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先设置项目路径！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("=== 开始智能匹配并更新全局配置 ===");
        // 1. 搜索路径并直接更新全局变量
        AutomaticSearchPathUtils.findAndSetGlobalPaths(projectPath);

        // 2. 从全局变量读取信息并更新UI
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        updatePathFieldFromPackage(entityPathField, globalInfo.entityOrdomainPackage, "Entity/Domain");
        updatePathFieldFromPackage(mapperPathField, globalInfo.mapperPackage, "Mapper");
        updatePathFieldFromPackage(servicePathField, globalInfo.servicePackage, "Service");
        updatePathFieldFromPackage(controllerPathField, globalInfo.controllerPackage, "Controller");

        System.out.println("=== 智能匹配完成 ===");
        JOptionPane.showMessageDialog(this, "路径匹配完成！全局配置已更新。", "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * “保存配置”按钮的逻辑：保存配置到文件，然后打印当前的全局配置信息。
     */
    private void saveAndPrintGlobalConfig() {
        // 1. 从UI更新全局变量
        updateGlobalInfoFromPaths();
        
        // 2. 将全局变量保存到文件
        FileConfigurationReadingUtils.saveConfiguration();

        // 3. 打印当前配置到控制台
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        System.out.println("========== 当前全局配置 (已保存) ==========");
        System.out.println("项目路径: " + (globalInfo.projectPath != null ? globalInfo.projectPath : "未设置"));
        System.out.println("Entity/Domain包: " + (globalInfo.entityOrdomainPackage != null && !globalInfo.entityOrdomainPackage.isEmpty() ? globalInfo.entityOrdomainPackage : "未找到"));
        System.out.println("Mapper包: " + (globalInfo.mapperPackage != null && !globalInfo.mapperPackage.isEmpty() ? globalInfo.mapperPackage : "未找到"));
        System.out.println("Service包: " + (globalInfo.servicePackage != null && !globalInfo.servicePackage.isEmpty() ? globalInfo.servicePackage : "未找到"));
        System.out.println("Controller包: " + (globalInfo.controllerPackage != null && !globalInfo.controllerPackage.isEmpty() ? globalInfo.controllerPackage : "未找到"));
        System.out.println("==========================================");
        JOptionPane.showMessageDialog(this, "配置已保存到文件并打印到控制台。", "配置已保存", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 辅助方法，根据包名更新UI文本框。
     */
    private void updatePathFieldFromPackage(JTextField field, String packageName, String type) {
        if (packageName != null && !packageName.isEmpty()) {
            String relativePath = "src/main/java/" + packageName.replace('.', '/');
            field.setText(relativePath);
            System.out.println("更新 " + type + " 路径: " + relativePath);
        } else {
            field.setText("");
            System.out.println("未找到 " + type + " 的路径，UI字段已清空。");
        }
    }

    /**
     * 从UI上的路径输入框读取信息，并更新到GlobalTableInfo单例中。
     */
    private void updateGlobalInfoFromPaths() {
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        String projectPath = projectPathField.getText().trim();
        if (!projectPath.isEmpty()) {
            globalInfo.projectPath = new File(projectPath).getAbsolutePath();
        }
        globalInfo.entityOrdomainPackage = convertPathToPackage(entityPathField.getText().trim());
        globalInfo.mapperPackage = convertPathToPackage(mapperPathField.getText().trim());
        globalInfo.servicePackage = convertPathToPackage(servicePathField.getText().trim());
        globalInfo.controllerPackage = convertPathToPackage(controllerPathField.getText().trim());
    }

    /**
     * 将文件系统路径转换为Java包名。
     */
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
        // 项目路径选择
        projectBrowseButton.addActionListener(e -> selectFolder(projectPathField, true, "选择项目根目录"));
        
        // Entity路径选择
        entityBrowseButton.addActionListener(e -> selectFolder(entityPathField, false, "选择Entity实体类路径"));
        
        // Mapper路径选择
        mapperBrowseButton.addActionListener(e -> selectFolder(mapperPathField, false, "选择Mapper接口路径"));
        
        // Service路径选择
        serviceBrowseButton.addActionListener(e -> selectFolder(servicePathField, false, "选择Service服务类路径"));
        
        // Controller路径选择
        controllerBrowseButton.addActionListener(e -> selectFolder(controllerPathField, false, "选择Controller控制器路径"));
    }
    
    private void selectFolder(JTextField pathField, boolean isProjectPath, String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle(title);
        fileChooser.setApproveButtonText("打开");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        
        // 设置当前目录
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
        
        // 显示对话框
        int result = fileChooser.showOpenDialog(SQLGeneratorGUI.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            
            if (isProjectPath) {
                // 项目路径直接设置绝对路径
                pathField.setText(selectedFile.getAbsolutePath());
            } else {
                // 其他路径设置相对路径
                String selectedPath = selectedFile.getAbsolutePath();
                String projectPath = projectPathField.getText();
                
                if (selectedPath.startsWith(projectPath)) {
                    // 计算相对路径
                    String relativePath = selectedPath.substring(projectPath.length());
                    if (relativePath.startsWith("\\") || relativePath.startsWith("/")) {
                        relativePath = relativePath.substring(1);
                    }
                    pathField.setText(relativePath.replace("\\", "/"));
                } else {
                    // 如果不在项目路径下，使用绝对路径
                    pathField.setText(selectedPath.replace("\\", "/"));
                }
            }
        }
    }
    
    /**
     * 辅助方法，根据包名更新UI文本框。
     */
    private void updateUIFromGlobalConfig() {
        GlobalTableInfo globalInfo = GlobalTableInfo.getInstance();
        projectPathField.setText(globalInfo.projectPath != null ? globalInfo.projectPath : "");
        updatePathFieldFromPackage(entityPathField, globalInfo.entityOrdomainPackage, "Entity/Domain");
        updatePathFieldFromPackage(mapperPathField, globalInfo.mapperPackage, "Mapper");
        updatePathFieldFromPackage(servicePathField, globalInfo.servicePackage, "Service");
        updatePathFieldFromPackage(controllerPathField, globalInfo.controllerPackage, "Controller");
    }

    public static void main(String[] args) {
        // 设置系统属性以支持中文显示
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");

        // 在GUI启动前，首先加载配置文件
        FileConfigurationReadingUtils.loadConfiguration();
        
        // 设置Look and Feel为系统默认
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // 如果设置失败，使用默认的Look and Feel
        }
        
        // 抑制IntelliJ IDEA相关的警告信息
        System.setErr(new java.io.PrintStream(new java.io.OutputStream() {
            @Override
            public void write(int b) {
                // 过滤掉包含特定关键词的错误信息
                // 不输出任何内容，从而抑制警告
            }
        }));
        
        // 在事件调度线程中创建和显示GUI
        SwingUtilities.invokeLater(() -> {
            SQLGeneratorGUI gui = new SQLGeneratorGUI();
            // 使用加载的配置更新UI
            gui.updateUIFromGlobalConfig();
            gui.setVisible(true);
        });
    }
}
