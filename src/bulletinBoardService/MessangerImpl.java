package bulletinBoardService;

import java.io.IOException;
import java.net.*;
import javax.swing.JOptionPane;

public class MessangerImpl implements Messanger {
    private UITasks ui;
    private MulticastSocket group;
    private InetAddress addr;
    private int port;
    private String name;
    private volatile boolean canceled = false;

    public MessangerImpl(InetAddress addr, int port, String name, UITasks ui) {
        this.addr = addr; this.port = port; this.name = name; this.ui = ui;
        try {
            group = new MulticastSocket(port);
            group.joinGroup(addr);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void start() {
        new Thread(() -> {
            try {
                byte[] in = new byte[512];
                DatagramPacket pkt = new DatagramPacket(in, in.length);
                while (!canceled) {
                    group.receive(pkt);
                    ui.setText(new String(pkt.getData(), 0, pkt.getLength()));
                }
            } catch (IOException e) {
                if (!canceled) JOptionPane.showMessageDialog(null, "Помилка прийому");
            }
        }).start();
    }

    @Override
    public void send() {
        new Thread(() -> {
            try {
                String msg = name + ": " + ui.getMessage();
                byte[] out = msg.getBytes();
                DatagramPacket pkt = new DatagramPacket(out, out.length, addr, port);
                group.send(pkt);
            } catch (IOException e) { e.printStackTrace(); }
        }).start();
    }

    @Override
    public void stop() {
        canceled = true;
        try { group.leaveGroup(addr); group.close(); } catch (IOException e) { e.printStackTrace(); }
    }
}