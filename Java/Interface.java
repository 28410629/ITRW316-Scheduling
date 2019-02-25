import javax.swing.*;
import java.awt.*;

public class Interface extends JFrame {
    String[] threadSchemes = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };
    JPanel panelMain;
    JPanel panelScheme;
    JPanel panelSpeed;
    JPanel panelButton;
    JPanel panelThread;
    GridLayout singleLayout = new GridLayout(0,1);
    GridLayout doubleLayout = new GridLayout(0,2);
    JComboBox threadSelection;
    JButton start;
    JButton stop;

    public Interface() {
        super("28410629 - Thread Application");
        panelMain = new JPanel();
        panelMain.setLayout(singleLayout);
        setupScheme();
        setupSpeed();
        setupButton();
        setupThread();
        this.add(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(420, 770);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true); 
    }

    public void setupScheme() {
        panelScheme = new JPanel();
        panelScheme.setSize(420, 75);
        panelScheme.setBorder(BorderFactory.createTitledBorder("Thread Scheming"));
        panelScheme.setLayout(singleLayout);
        threadSelection = new JComboBox(threadSchemes);
        panelScheme.add(threadSelection);
        panelMain.add(panelScheme);
    }

    public void setupSpeed() {
        panelSpeed = new JPanel();
        panelSpeed.setSize(420, 200);
        panelSpeed.setBorder(BorderFactory.createTitledBorder("Speed"));
        panelSpeed.setLayout(singleLayout);
        panelMain.add(panelSpeed);
    }

    public void setupButton() {
        panelButton = new JPanel();
        panelButton.setSize(420, 200);
        panelButton.setBorder(BorderFactory.createTitledBorder(""));
        panelButton.setLayout(doubleLayout);
        start = new JButton("Start");
        stop = new JButton("Stop");
        panelButton.add(start);
        panelButton.add(stop);
        panelMain.add(panelButton);
    }

    public void setupThread() {
        panelThread = new JPanel();
        panelThread.setSize(420, 200);
        panelThread.setBorder(BorderFactory.createTitledBorder("Threads"));
        panelThread.setLayout(singleLayout);
        panelMain.add(panelThread);
    }
}