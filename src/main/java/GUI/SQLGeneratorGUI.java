package GUI;

import Global.GlobalTableInfo;
import utils.TableFieldExtractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SQLGeneratorGUI extends JFrame {
    private JTextArea sqlInputArea;
    private JButton generateButton;
    
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
        setSize(900, 500);  // 增加宽度以容纳右侧区域
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
        leftPanel.setPreferredSize(new Dimension(500, 400));
        
        JScrollPane scrollPane = new JScrollPane(sqlInputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 右侧 - 路径配置区域
        JPanel rightPanel = createPathConfigPanel();
        rightPanel.setPreferredSize(new Dimension(350, 400));
        
        // 底部 - 按钮区域
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(generateButton);
        
        // 添加到主窗口
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPathConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("代码生成路径配置"));
        
        Font labelFont = new Font("Microsoft YaHei", Font.PLAIN, 12);
        
        // 项目路径配置
        panel.add(Box.createVerticalStrut(10));
        JLabel projectLabel = new JLabel("项目路径:");
        projectLabel.setFont(labelFont);
        panel.add(projectLabel);
        panel.add(Box.createVerticalStrut(5));
        JPanel projectPanel = new JPanel(new BorderLayout(5, 0));
        projectPanel.add(projectPathField, BorderLayout.CENTER);
        projectPanel.add(projectBrowseButton, BorderLayout.EAST);
        panel.add(projectPanel);
        
        // Entity路径配置
        panel.add(Box.createVerticalStrut(15));
        JLabel entityLabel = new JLabel("Entity实体类路径:");
        entityLabel.setFont(labelFont);
        panel.add(entityLabel);
        panel.add(Box.createVerticalStrut(5));
        JPanel entityPanel = new JPanel(new BorderLayout(5, 0));
        entityPanel.add(entityPathField, BorderLayout.CENTER);
        entityPanel.add(entityBrowseButton, BorderLayout.EAST);
        panel.add(entityPanel);
        
        // Mapper路径配置
        panel.add(Box.createVerticalStrut(15));
        JLabel mapperLabel = new JLabel("Mapper接口路径:");
        mapperLabel.setFont(labelFont);
        panel.add(mapperLabel);
        panel.add(Box.createVerticalStrut(5));
        JPanel mapperPanel = new JPanel(new BorderLayout(5, 0));
        mapperPanel.add(mapperPathField, BorderLayout.CENTER);
        mapperPanel.add(mapperBrowseButton, BorderLayout.EAST);
        panel.add(mapperPanel);
        
        // Service路径配置
        panel.add(Box.createVerticalStrut(15));
        JLabel serviceLabel = new JLabel("Service服务类路径:");
        serviceLabel.setFont(labelFont);
        panel.add(serviceLabel);
        panel.add(Box.createVerticalStrut(5));
        JPanel servicePanel = new JPanel(new BorderLayout(5, 0));
        servicePanel.add(servicePathField, BorderLayout.CENTER);
        servicePanel.add(serviceBrowseButton, BorderLayout.EAST);
        panel.add(servicePanel);
        
        // Controller路径配置
        panel.add(Box.createVerticalStrut(15));
        JLabel controllerLabel = new JLabel("Controller控制器路径:");
        controllerLabel.setFont(labelFont);
        panel.add(controllerLabel);
        panel.add(Box.createVerticalStrut(5));
        JPanel controllerPanel = new JPanel(new BorderLayout(5, 0));
        controllerPanel.add(controllerPathField, BorderLayout.CENTER);
        controllerPanel.add(controllerBrowseButton, BorderLayout.EAST);
        panel.add(controllerPanel);
        
        // 添加弹性空间
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // 生成代码按钮事件
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sqlContent = sqlInputArea.getText().trim();
                
                if (sqlContent.isEmpty() || sqlContent.startsWith("-- 请在此输入")) {
                    JOptionPane.showMessageDialog(
                        SQLGeneratorGUI.this,
                        "请输入有效的SQL建表语句！",
                        "提示",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // 调用TableFieldExtractor进行处理
                try {
                    System.out.println("=== 开始解析SQL ===");
                    TableFieldExtractor.TableInfo tableInfo = TableFieldExtractor.parseSql(sqlContent);
                    
                    // 打印核心信息
                    TableFieldExtractor.printCoreInfo(tableInfo);
                    
                    JOptionPane.showMessageDialog(
                        SQLGeneratorGUI.this,
                        "SQL解析成功！\n表名: " + tableInfo.getTableName() + 
                        "\n字段数: " + tableInfo.getFields().size() + 
                        "\n请查看控制台输出详细信息。",
                        "解析成功",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        SQLGeneratorGUI.this,
                        "处理SQL时发生错误：" + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                }
            }
        });
        
        // 文件夹选择事件处理
        setupBrowseButtonEvents();
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
    
    public static void main(String[] args) {
        // 设置系统属性以支持中文显示
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SQLGeneratorGUI().setVisible(true);
            }
        });
    }
}
