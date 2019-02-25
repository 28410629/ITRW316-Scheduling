import javax.swing.*;
import java.awt.*;
import java.awt.event.*;  
import javax.swing.event.*;

public class Interface extends JFrame {
    JPanel[] panels = new JPanel[6];
    /* 0 - Main
       1 - Scheme
       2 - Speed
       3 - Active Threads
       4 - Button
       5 - Threads */
    GridBagLayout gbLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    GridLayout gLayout = new GridLayout(0,1);
    GridLayout gLayout2 = new GridLayout(0,4);
    JComboBox threadSelection;
    String[] threadSchemes = { "First-Come First-Serve", "Shortest Job First", "Shortest Remaining Time", "Round-Robin", "Priority Scheduling", "Multiple Queue" };
    JButton start;
    JButton stop;
    JButton reset;
    static final int speedMin = 0;
    static final int speedMax = 10;
    static final int speedInit = 8;   
    JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, speedMin, speedMax, speedInit);
    static final int activeMin = 2;
    static final int activeMax = 8;
    static final int activeInit = 2; 
    JSlider activeSlider = new JSlider(JSlider.HORIZONTAL, activeMin, activeMax, activeInit);
    MyThreads[] threads = new MyThreads[8];
    JFrame mainWindow;
    int speedValue = 2;

    public Interface() {
        super("28410629 - Thread Application");
        mainWindow = this;
        this.setLayout(gLayout);
        panels[0] = new JPanel();
        panels[0].setLayout(gbLayout);
        setupScheme();
        setupSpeed();
        setupActive();
        setupButton();
        setupThread();
        this.add(panels[0]); // main panel
        this.add(panels[5]); // thread panel
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1024, 500);
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
        threadSelection.setPreferredSize(new Dimension(200,86));
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
        panels[2].setLayout(gLayout);
        // create components 
        speedSlider.setPreferredSize(new Dimension(200, 43));
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        JLabel speedLabel = new JLabel("Set Before Starting!");
        // add listener event
        speedSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int val = ((JSlider)e.getSource()).getValue();
                speedLabel.setText("<html>Set Before Starting! : <font color='orange'> " + val + "</font></html>");
                speedValue = 11 - ((JSlider)e.getSource()).getValue();
                mainWindow.repaint();
                mainWindow.revalidate();
                resetThreads();
            }
        });
        // add panel components
        panels[2].add(speedSlider);
        panels[2].add(speedLabel);
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
        panels[3].setLayout(gLayout);
        // create components 
        JLabel activeLabel = new JLabel("Set Before Starting!");
        activeSlider.setPreferredSize(new Dimension(200, 43));
        activeSlider.setMajorTickSpacing(1);
        activeSlider.setMinorTickSpacing(1);
        activeSlider.setPaintTicks(true);
        activeSlider.setPaintLabels(true);
        // add listener event
        activeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int val = ((JSlider)e.getSource()).getValue();
                activeLabel.setText("<html>Set Before Starting! : <font color='orange'> " + val + "</font></html>");
                mainWindow.repaint();
                mainWindow.revalidate();
                resetThreads();
            }
        });
        // add panel components
        panels[3].add(activeSlider);
        panels[3].add(activeLabel);
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
        reset = new JButton("Reset");
        start.setPreferredSize(new Dimension(100, 86));
        stop.setPreferredSize(new Dimension(100, 86));
        reset.setPreferredSize(new Dimension(100, 86));
        // button action listeners
        start.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                if (start.getText() == "Resume") {
                    for (int i = 0; i < activeSlider.getValue(); i++) {
                        threads[i].resume();
                    }                   
                } else {
                    for (int i = 0; i < activeSlider.getValue(); i++) {
                        threads[i].start(); 
                    } 
                }
            }  
        });  
        stop.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                start.setText("Resume");
                for (int i = 0; i < activeSlider.getValue(); i++) {
                    threads[i].suspend();
                } 
            }  
        }); 
        reset.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                resetThreads();
            }  
        }); 
        // add panel components
        panels[4].add(start);
        panels[4].add(stop);
        panels[4].add(reset);
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
        panels[5].setLayout(gLayout2);
        // create components
        for (int i = 0; i < 8; i++) {
            threads[i] = new MyThreads(i, speedValue);
        }
        // add components to panel
        for (int i = 0; i < 8; i++) {
            panels[5].add(threads[i].getGUI());
        }
    }

    public void resetThreads() {
        // terminate existing threads
        for (int i = 0; i < activeSlider.getValue(); i++) {
            threads[i].terminate();
            threads[i] = null;
        } 
        // remove components from thread panel
        start.setText("Start");
        panels[5].removeAll();
        mainWindow.repaint();
        mainWindow.revalidate();
        // create components
        for (int i = 0; i < 8; i++) {
            threads[i] = new MyThreads(i, speedValue);
        }
        // add components to panel
        for (int i = 0; i < 8; i++) {
            panels[5].add(threads[i].getGUI());
        }
        mainWindow.repaint();
        mainWindow.revalidate();
    }
}