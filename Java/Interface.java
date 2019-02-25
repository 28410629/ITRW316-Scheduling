import javax.swing.*;
import java.awt.*;

public class Interface extends JFrame {
    JPanel[] panels = new JPanel[6];
    /* 0 - Main
       1 - Scheme
       2 - Speed
       3 - Active Threads
       4 - Button
       5 - Thread*/
    GridBagLayout gbLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    JComboBox threadSelection;
    String[] threadSchemes = { "First-Come First-Serve", "Shortest Job First", "Shortest Remaining Time", "Round-Robin", "Priority Scheduling", "Multiple Queue" };
    JButton start;
    JButton stop;
    static final int speedMin = 0;
    static final int speedMax = 100;
    static final int speedInit = 20;   
    JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, speedMin, speedMax, speedInit);
    static final int activeMin = 2;
    static final int activeMax = 8;
    static final int activeInit = 2; 
    JSlider activeSlider = new JSlider(JSlider.HORIZONTAL, activeMin, activeMax, activeInit);

    public Interface() {
        super("28410629 - Thread Application");
        panels[0] = new JPanel();
        panels[0].setLayout(gbLayout);
        setupScheme();
        setupSpeed();
        setupActive();
        setupButton();
        setupThread();
        this.add(panels[0]);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1024, 600);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true); 
    }

    public void setupScheme() {
        // create panel
        panels[1] = new JPanel();
        panels[1].setSize(420, 75);
        panels[1].setBorder(BorderFactory.createTitledBorder("Thread Scheming"));
        panels[1].setLayout(gbLayout);
        // create panel components
        threadSelection = new JComboBox(threadSchemes);
        threadSelection.setPreferredSize(new Dimension(200,43));
        // add panel components
        panels[1].add(threadSelection);
        // add panel to main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panels[0].add(panels[1], gbc);
    }

    public void setupSpeed() {
        // create panel
        panels[2] = new JPanel();
        panels[2].setSize(420, 200);
        panels[2].setBorder(BorderFactory.createTitledBorder("Speed"));
        panels[2].setLayout(gbLayout);
        // create components 
        speedSlider.setMajorTickSpacing(20);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        // add panel components
        panels[2].add(speedSlider);
        // add panel to main panel
        gbc.gridx = 1;
        gbc.gridy = 0;
        panels[0].add(panels[2], gbc);
    }

    public void setupActive() {
        // create panel
        panels[3] = new JPanel();
        panels[3].setSize(420, 200);
        panels[3].setBorder(BorderFactory.createTitledBorder("Active Threads"));
        panels[3].setLayout(gbLayout);
        // create components 
        activeSlider.setMajorTickSpacing(1);
        activeSlider.setMinorTickSpacing(1);
        activeSlider.setPaintTicks(true);
        activeSlider.setPaintLabels(true);
        // add panel components
        panels[3].add(activeSlider);
        // add panel to main panel
        gbc.gridx = 2;
        gbc.gridy = 0;
        panels[0].add(panels[3], gbc);
    }

    public void setupButton() {
        // create panel
        panels[4] = new JPanel();
        panels[4].setSize(420, 200);
        panels[4].setBorder(BorderFactory.createTitledBorder("Go!"));
        panels[4].setLayout(gbLayout);
        // create panel components
        start = new JButton("Start");
        stop = new JButton("Stop");
        start.setPreferredSize(new Dimension(100, 43));
        stop.setPreferredSize(new Dimension(100, 43));
        // add panel components
        panels[4].add(start);
        panels[4].add(stop);
        // add panel to main panel
        gbc.gridx = 3;
        gbc.gridy = 0;
        panels[0].add(panels[4], gbc);
    }

    public void setupThread() {
        // create panel
        panels[5] = new JPanel();
        panels[5].setSize(420, 200);
        panels[5].setBorder(BorderFactory.createTitledBorder("Threads"));
        panels[5].setLayout(gbLayout);
        // add panel to main panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        panels[0].add(panels[5], gbc);
    }
}