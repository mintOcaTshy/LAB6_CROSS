package bulletinBoardService;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Proxy;
import java.net.InetAddress;

public class ConferenceGUI extends JFrame {
    private JTextArea textArea = new JTextArea();
    private JTextField textFieldMsg = new JTextField();
    private JTextField textFieldName = new JTextField("Користувач");
    private JButton btnSend = new JButton("Надіслати");
    private JButton btnConnect = new JButton("З'єднати");
    private Messanger messanger;

    public ConferenceGUI() {
        super("Чат конференції");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.add(new JLabel(" Ваше ім'я:"));
        topPanel.add(textFieldName);
        add(topPanel, BorderLayout.NORTH);

        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(textFieldMsg, BorderLayout.CENTER);
        bottomPanel.add(btnSend, BorderLayout.EAST);

        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.add(bottomPanel);
        southPanel.add(btnConnect);
        add(southPanel, BorderLayout.SOUTH);

        UITasks uiProxy = (UITasks) Proxy.newProxyInstance(
                UITasks.class.getClassLoader(),
                new Class[]{UITasks.class},
                new EDTInvocationHandler(new UITasks() {
                    public String getMessage() {
                        String m = textFieldMsg.getText();
                        textFieldMsg.setText("");
                        return m;
                    }
                    public void setText(String txt) {
                        textArea.append(txt + "\n");
                    }
                })
        );

        btnConnect.addActionListener(e -> {
            try {
                if (messanger == null) {
                    messanger = new MessangerImpl(InetAddress.getByName("224.0.0.1"), 3456, textFieldName.getText(), uiProxy);
                    messanger.start();
                    btnConnect.setText("Роз'єднати");
                } else {
                    messanger.stop();
                    messanger = null;
                    btnConnect.setText("З'єднати");
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnSend.addActionListener(e -> {
            if (messanger != null) messanger.send();
        });

        textFieldMsg.addActionListener(e -> btnSend.doClick());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConferenceGUI().setVisible(true));
    }
}