package GUI;

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
    
    private void initializeComponents() {
        setTitle("MyBatis Plus 代码生成器");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 创建SQL输入区域
        sqlInputArea = new JTextArea();
        sqlInputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        sqlInputArea.setLineWrap(true);
        sqlInputArea.setWrapStyleWord(true);
        sqlInputArea.setText("-- 请在此输入SQL建表语句\n-- 例如：\n-- CREATE TABLE user (\n--     id BIGINT PRIMARY KEY AUTO_INCREMENT,\n--     name VARCHAR(50) NOT NULL,\n--     email VARCHAR(100)\n-- );");
        
        // 创建生成按钮
        generateButton = new JButton("生成代码");
        generateButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        generateButton.setPreferredSize(new Dimension(120, 35));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 上半部分 - SQL输入区域
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("SQL建表语句"));
        
        JScrollPane scrollPane = new JScrollPane(sqlInputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        topPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 下半部分 - 按钮区域
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(generateButton);
        
        // 添加到主窗口
        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
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
                
                // 将输入框内容赋值给sql变量
                String sql = sqlContent;
                
                // 调用TableFieldExtractor进行处理
                try {
                    System.out.println("=== 开始解析SQL ===");
                    TableFieldExtractor.TableInfo tableInfo = TableFieldExtractor.parseSql(sql);
                    
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
    }
    
    public static void main(String[] args) {
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
