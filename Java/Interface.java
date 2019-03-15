import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.ArrayList;
import java.util.List;

public class Interface extends JFrame {
    JPanel[] panels = new JPanel[6];
    JPanel panelInfo = new JPanel();
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
    private static final int priorityMin = 0;
    private static final int priorityMax = 10;
    private static final int priorityInit = 0; 
    private JSlider prioritySlider = new JSlider(JSlider.HORIZONTAL, priorityMin, priorityMax, priorityInit);
    private static final int workMin = 0;
    private static final int workMax = 10;
    private static final int workInit = 0; 
    private JSlider workSlider = new JSlider(JSlider.HORIZONTAL, workMin, workMax, workInit);
    static final int activeMin = 2;
    static final int activeMax = 8;
    static final int activeInit = 2; 
    JSlider activeSlider = new JSlider(JSlider.HORIZONTAL, activeMin, activeMax, activeInit);
    List<MyThreads> listThreads = new ArrayList<MyThreads>();
    JFrame mainWindow;
    Interface mainInterface;
    int speedValue = 1; // initial speed value
    ThreadScheming scheme;
    JLabel preemptiveLabel;
    boolean schemeStarted = false;
    private int exeBarPosition = -1;
    private JLabel[] exeBarLabels = new JLabel[20];
    Border border = BorderFactory.createLineBorder(Color.white, 1);

    public Interface() {
        super("28410629 - Thread Application");
        mainWindow = this;
        mainInterface = this;
        this.setLayout(gLayout);
        panels[0] = new JPanel();
        panels[0].setLayout(gbLayout);
        panels[0].setOpaque(true);
        panels[0].setBackground(Color.darkGray);
        panelInfo.setOpaque(true);
        panelInfo.setBackground(Color.darkGray);
        panelInfo.setLayout(new GridLayout(2,0));
        setupScheme();
        setupSpeed();
        setupActive();
        setupButton();
        setupThread();
        panelInfo.add(panels[0]); // main panel
        setupExecutionBar();
        this.add(panelInfo);
        this.add(panels[5]); // thread panel
        scheme = new ThreadScheming(start, stop, reset, mainInterface);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1024, 500);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true); 
    }

    public void setupScheme() {
        // create panel
        panelAttributes(1, "Thread Scheming", gLayout);
        // create panel components
        preemptiveLabel = new JLabel("<html><font color='white'>Non-Preemptive</font></html>");
        threadSelection = new JComboBox(threadSchemes);
        threadSelection.setPreferredSize(new Dimension(200,43));
        threadSelection.setBackground(Color.darkGray);
        threadSelection.setForeground(Color.white);
        threadSelection.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (threadSelection.getSelectedItem().toString() == "First-Come First-Serve" || threadSelection.getSelectedItem().toString() == "Shortest Job First") {
                    preemptiveLabel.setText("<html><font color='white'>Non-Preemptive</font></html>");
                } else {
                    preemptiveLabel.setText("<html><font color='white'>Preemptive</font></html>");
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

    public void setupExecutionBar() {
        JPanel exeBar = new JPanel();
        exeBar.setLayout(new GridLayout(0,20));
        exeBar.setOpaque(true);
        exeBar.setBackground(Color.darkGray);
        exeBar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.white),"<html><font color='white'>Execution Bar</font></html>"));
        for (int i = 0; i < 20; i++) {
            exeBarLabels[i] = new JLabel();
            exeBarLabels[i].setOpaque(true);
            exeBarLabels[i].setBackground(Color.darkGray);
            exeBarLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            exeBarLabels[i].setBorder(border);
            exeBar.add(exeBarLabels[i]);
        }
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelInfo.add(exeBar, gbc);
    }

    public void updateExecutionBar(int threadID) {
        Color color;
        switch (threadID) {
            case 0:
                color = Color.pink;
                break;
            case 1:
                color = Color.magenta;
                break;
            case 2:
                color = Color.cyan;
                break;
            case 3:
                color = Color.yellow;
                break;
            case 4:
                color = Color.green;
                break;
            case 5:
                color = Color.blue;
                break;
            case 6:
                color = Color.orange;
                break;
            case 7:
                color = Color.white;
                break;
            default:
                color = Color.black;
                break;
        }
        if (exeBarPosition == 19) {
            exeBarPosition = -1;
        }
        exeBarPosition++;
        exeBarLabels[exeBarPosition].setForeground(color);
        exeBarLabels[exeBarPosition].setText("" + threadID);
        for (int i = exeBarPosition + 1; i < 20; i++) {
            exeBarLabels[i].setText("");
        }
    }

    public void setupSpeed() {
        // create panel
        panelAttributes(2, "Thread Attributes", gLayout);
        // create components 
        prioritySlider.setBackground(new Color(255,140,0));
        workSlider.setBackground(new Color(128,128,128));
        prioritySlider.setPreferredSize(new Dimension(200, 43));
        workSlider.setPreferredSize(new Dimension(200, 43));
        prioritySlider.setMinorTickSpacing(1);
        prioritySlider.setPaintTicks(true);
        prioritySlider.setSnapToTicks(true);
        workSlider.setMinorTickSpacing(1);
        workSlider.setPaintTicks(true);
        workSlider.setSnapToTicks(true);
        workSlider.setForeground(Color.white);
        prioritySlider.setForeground(Color.white);
        // add panel components
        panels[2].add(prioritySlider);
        panels[2].add(workSlider);
        // add panel to main panel
        gbc.gridx = 1;
        gbc.gridy = 0;
        panels[0].add(panels[2], gbc);
    }

    public void panelAttributes(int i, String name, GridLayout l) {
        panels[i] = new JPanel();
        panels[i].setSize(420, 200);
        panels[i].setOpaque(true);
        panels[i].setBackground(Color.darkGray);
        panels[i].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.white),"<html><font color='white'>" + name + "</font></html>"));
        if (i == 4) {
            panels[i].setLayout(gbLayout);
        } else {
            panels[i].setLayout(l);
        }
       
    }

    public void setupActive() {
        // create panel
        panelAttributes(3, "Active Threads", gLayout);
        // create components 
        JLabel activeLabel = new JLabel("<html><font color='white'>Threads : </font><font color='orange'>2</font></html>");
        activeSlider.setPreferredSize(new Dimension(200, 43));
        activeSlider.setMajorTickSpacing(1);
        activeSlider.setMinorTickSpacing(1);
        activeSlider.setPaintTicks(true);
        activeSlider.setPaintLabels(true);
        activeSlider.setBackground(Color.darkGray);
        activeSlider.setForeground(Color.white);
        // add listener event
        activeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int val = ((JSlider)e.getSource()).getValue();
                activeLabel.setText("<html><font color='white'>Threads : </font><font color='orange'> " + val + "</font></html>");
                mainWindow.repaint();
                mainWindow.revalidate();
                int amount = panels[5].getComponentCount();
                int newVal = activeSlider.getValue();
                if (!stop.isEnabled() && !start.isEnabled()) {
                    activeLabel.setText("<html><font color='orange'>Not allowed, reset!</font></html>");
                    activeSlider.setValue(amount);
                    mainWindow.repaint();
                    mainWindow.revalidate();
                } else {
                    if (amount > newVal) {
                        // remove threads
                        if (stop.isEnabled() && start.isEnabled() && reset.isEnabled()) {
                            for (int i = 1; i <= (amount - newVal); i++) {
                                removeLastThread();
                            }
                        } else {
                            activeSlider.setValue(amount);
                            mainWindow.repaint();
                            mainWindow.revalidate();
                        }
                    } else {
                        // add threads
                        for (int i = 1; i <= (newVal - amount); i++) {
                            addLastThread();
                        }
                    }
                }
                for (MyThreads t : listThreads) {
                    t.setTickThread(amount);
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
        panelAttributes(4, "Go!", gLayout);
        // create panel components
        start = new JButton("<html><font color='white'>Start</font></html>");
        stop = new JButton("<html><font color='white'>Stop</font></html>");
        reset = new JButton("<html><font color='white'>Reset</font></html>");
        buttonAttributes(start);
        buttonAttributes(stop);
        buttonAttributes(reset);
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
                            scheme.MQ();
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
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new Interface();
                    }
                });
                mainInterface.dispose();
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

    public void buttonAttributes(JButton button) {
        button.setOpaque(true);
        button.setBackground(Color.darkGray);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setBorder(border);
        button.setPreferredSize(new Dimension(100, 86));
    }

    public void buttonStart() {
        start.setText("<html><font color='white'>Active</font></html>");
        start.setEnabled(false);
        stop.setEnabled(true);
        reset.setEnabled(false);
    }

    public void buttonStop() {
        start.setText("<html><font color='white'>Resume</font></html>");
        start.setEnabled(true);
        stop.setEnabled(false);
        reset.setEnabled(true);
    }

    public void setupThread() {
        // create panel
        panelAttributes(5, "Threads", gLayout2);
        createAddThread();
    }

    public void createAddThread() {
        // create components
        for (int i = 0; i < activeSlider.getValue(); i++) {
            listThreads.add(new MyThreads(i, speedValue, mainInterface));
        }
        // add components to panel
        for (MyThreads t:listThreads) {
            panels[5].add(t.getGUI());
            t.setTickThread(2);
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
        listThreads.add(new MyThreads(listThreads.size(), speedValue, mainInterface, prioritySlider.getValue(),workSlider.getValue()));
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
                    scheme.MQ();
                    break; 
                default:
                    break;
            }
        }
        mainWindow.repaint();
        mainWindow.revalidate();
    }
}