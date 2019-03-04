import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.List;

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
    static final int speedInit = 10;   
    JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, speedMin, speedMax, speedInit);
    static final int activeMin = 2;
    static final int activeMax = 8;
    static final int activeInit = 2; 
    JSlider activeSlider = new JSlider(JSlider.HORIZONTAL, activeMin, activeMax, activeInit);
    List<MyThreads> listThreads = new ArrayList<MyThreads>();
    JFrame mainWindow;
    int speedValue = 1; // initial speed value
    ThreadScheming scheme;
    JLabel preemptiveLabel;
    boolean schemeStarted = false;

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
        scheme = new ThreadScheming(start, stop, reset);
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
        panels[1].setLayout(gLayout);
        // create panel components
        preemptiveLabel = new JLabel("Non-Preemptive");
        threadSelection = new JComboBox(threadSchemes);
        threadSelection.setPreferredSize(new Dimension(200,43));
        threadSelection.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (threadSelection.getSelectedItem().toString() == "First-Come First-Serve" || threadSelection.getSelectedItem().toString() == "Shortest Job First") {
                    preemptiveLabel.setText("Non-Preemptive");
                } else {
                    preemptiveLabel.setText("Preemptive");
                }
            }
        });
        // add panel components
        panels[1].add(threadSelection);
        panels[1].add(preemptiveLabel);
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
        JLabel speedLabel = new JLabel("<html>Speed : <font color='orange'>10</font></html>");
        // add listener event
        speedSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int val = ((JSlider)e.getSource()).getValue();
                speedLabel.setText("<html>Speed : <font color='orange'> " + val + "</font></html>");
                speedValue = 11 - ((JSlider)e.getSource()).getValue();
                mainWindow.repaint();
                mainWindow.revalidate();
                // set new speeds for threads
                for (MyThreads t:listThreads) {
                    t.setGlobalSpeed(speedValue);
                }
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
        JLabel activeLabel = new JLabel("<html>Threads : <font color='orange'>2</font></html>");
        activeSlider.setPreferredSize(new Dimension(200, 43));
        activeSlider.setMajorTickSpacing(1);
        activeSlider.setMinorTickSpacing(1);
        activeSlider.setPaintTicks(true);
        activeSlider.setPaintLabels(true);
        // add listener event
        activeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int val = ((JSlider)e.getSource()).getValue();
                activeLabel.setText("<html>Threads : <font color='orange'> " + val + "</font></html>");
                mainWindow.repaint();
                mainWindow.revalidate();
                //resetThreads(); this should be replaced with preamptive approach
                int amount = panels[5].getComponentCount();
                int newVal = activeSlider.getValue();
                if (preemptiveLabel.getText() == "Non-Preemptive" && !start.isEnabled()) {
                    activeLabel.setText("<html><font color='orange'>Not allowed!</font></html>");
                    activeSlider.setValue(amount);
                    mainWindow.repaint();
                    mainWindow.revalidate();
                } else {
                    if (amount > newVal) {
                        // remove threads
                        for (int i = 1; i <= (amount - newVal); i++) {
                            removeLastThread();
                        }
                    } else {
                        // add threads
                        for (int i = 1; i <= (newVal - amount); i++) {
                            addLastThread();
                        }
                    }
                }
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
                    schemeStarted = true;
                    buttonStart();
                    scheme.resumeScheme();                  
                } else {
                    schemeStarted = true;
                    buttonStart();
                    scheme.setThreads(listThreads, listThreads.size());
                    switch (threadSelection.getSelectedItem().toString()) {
                        case "First-Come First-Serve":
                            scheme.FCFS();
                            break; 
                        case "Round-Robin":
                            scheme.RR();
                            break;
                        case "Shortest Job First":
                            scheme.SJF();
                            break;
                        case "Shortest Remaining Time":
                            scheme.SRT();
                            break;
                        case "Priority Scheduling":
                            scheme.PS();
                            break; 
                        case "Multiple Queue":
                            break; 
                        default:
                            break;
                    }
                }
            }  
        });  
        stop.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                buttonStop();
                schemeStarted = false;
                scheme.suspendScheme();
            }  
        }); 
        reset.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                schemeStarted = false;
                scheme.terminateScheme();
                resetThreads(activeSlider.getValue());
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

    public void buttonStart() {
        start.setText("Active");
        start.setEnabled(false);
        stop.setEnabled(true);
        reset.setEnabled(false);
    }

    public void buttonStop() {
        start.setText("Resume");
        start.setEnabled(true);
        stop.setEnabled(false);
        reset.setEnabled(true);
    }

    public void setupThread() {
        // create panel
        panels[5] = new JPanel();
        panels[5].setSize(420, 200);
        panels[5].setBorder(BorderFactory.createTitledBorder("Threads"));
        panels[5].setLayout(gLayout2);
        createAddThread();
    }

    public void createAddThread() {
        // create components
        for (int i = 0; i < activeSlider.getValue(); i++) {
            listThreads.add(new MyThreads(i, speedValue));
        }
        // add components to panel
        for (MyThreads t:listThreads) {
            panels[5].add(t.getGUI());
        }
        mainWindow.repaint();
        mainWindow.revalidate();
    }

    public void resetThreads(int amount) {
        // terminate existing threads
        for (MyThreads t:listThreads) {
            t.terminate();;
        }
        listThreads.clear();
        // remove components from thread panel
        start.setText("Start");
        start.setEnabled(true);
        panels[5].removeAll();
        mainWindow.repaint();
        mainWindow.revalidate();
        // create threads components and add them to panel
        createAddThread();
    }

    public void removeLastThread() {
        if (schemeStarted) {
            scheme.threadUpdateScheme();
        }
        panels[5].remove(listThreads.get(listThreads.size() - 1).getGUI());
        listThreads.remove(listThreads.size() - 1);
        scheme.setThreads(listThreads, listThreads.size());
        if (schemeStarted) {
            switch (threadSelection.getSelectedItem().toString()) {
                case "First-Come First-Serve":
                    scheme.FCFS();
                    break; 
                case "Round-Robin":
                    scheme.RR();
                    break;
                case "Shortest Job First":
                    scheme.SJF();
                    break;
                case "Shortest Remaining Time":
                    scheme.SRT();
                    break;
                case "Priority Scheduling":
                    scheme.PS();
                    break; 
                case "Multiple Queue":
                    break; 
                default:
                    break;
            }
        }
        mainWindow.repaint();
        mainWindow.revalidate();
    }

    public void addLastThread() {
        if (schemeStarted) {
            scheme.threadUpdateScheme();
        }
        listThreads.add(new MyThreads(listThreads.size(), speedValue));
        scheme.setThreads(listThreads, listThreads.size());
        panels[5].add(listThreads.get(listThreads.size() - 1).getGUI());
        if (schemeStarted) {
            switch (threadSelection.getSelectedItem().toString()) {
                case "First-Come First-Serve":
                    scheme.FCFS();
                    break; 
                case "Round-Robin":
                    scheme.RR();
                    break;
                case "Shortest Job First":
                    scheme.SJF();
                    break;
                case "Shortest Remaining Time":
                    scheme.SRT();
                    break;
                case "Priority Scheduling":
                    scheme.PS();
                    break; 
                case "Multiple Queue":
                    break; 
                default:
                    break;
            }
        }
        mainWindow.repaint();
        mainWindow.revalidate();
    }
}