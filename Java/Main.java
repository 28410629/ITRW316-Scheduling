import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // run interface in gui thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Interface();
            }
        });
    }
}