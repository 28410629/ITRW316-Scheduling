
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;

public class MyThreads extends Thread implements Comparable<MyThreads>{

    private JPanel panel;
    private int threadID;
    private JProgressBar progressBar;
    private GridLayout gLayout = new GridLayout(0,1);
    private GridBagLayout gbLayout = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();
    private JLabel activeLabel = new JLabel("Inactive");
    private boolean active = false;
    private boolean finished = false;
    private int globalSpeed;
    private volatile boolean running = true;
    private static final int priorityMin = 0;
    private static final int priorityMax = 10;
    private static final int priorityInit = 0; 
    private JSlider prioritySlider = new JSlider(JSlider.HORIZONTAL, priorityMin, priorityMax, priorityInit);
    private static final int workMin = 0;
    private static final int workMax = 10;
    private static final int workInit = 0; 
    private JSlider workSlider = new JSlider(JSlider.HORIZONTAL, workMin, workMax, workInit);
    private boolean isTerminated = false;
    private int _priority = 0;
    private int _workIntensity = 0;
    private boolean _reportTerminate = false;
    private boolean _priorityComparable = false;
    private int _multipleQueueLevel = 1;
    private Interface _mainWindow;

    public MyThreads(int threadID, int Speed, Interface mainWindow) {
        super();
        createGUI(threadID);
        globalSpeed = Speed;
        _mainWindow = mainWindow;
    }

    public MyThreads(int threadID, int Speed, Interface mainWindow, int Priority, int Worker) {
        this(threadID, Speed, mainWindow);
        prioritySlider.setValue(Priority);
        workSlider.setValue(Worker);
    }

    public void createGUI(int id) {
        threadID = id;
        // create panel
        panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.darkGray);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.white),"<html><font color='white'>Thread : " + threadID + "</font></html>"));
        panel.setLayout(gLayout);
        // create components 
        colorProgressBar(id);
        activeBorder();
        prioritySlider.setOpaque(true);
        workSlider.setForeground(Color.white);
        prioritySlider.setForeground(Color.white);
        prioritySlider.setBackground(new Color(255,140,0));
        workSlider.setBackground(new Color(128,128,128));
        updateActiveLabel();
        // add listener events
        workSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateActiveLabel();
                activeBorder();
            }
        });
        prioritySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateActiveLabel();
            }
        });
        // add panel components
        panel.add(progressBar);
        panel.add(prioritySlider);
        panel.add(workSlider);
        panel.add(activeLabel);
    }

    public void colorProgressBar(int threadID) {
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
        Color color2;
        if (color == Color.blue) {
            color2 = Color.white;
        } else {
            color2 = Color.black;
        }
        UIDefaults defaults = UIManager.getDefaults();
        // the foreground color
        defaults.put("ProgressBar.foreground", new ColorUIResource(color));
        // the color used to render the text over the foreground color
        defaults.put("ProgressBar.selectionForeground", new ColorUIResource(color2));
        // the background color
        defaults.put("ProgressBar.background", new ColorUIResource(Color.darkGray));
        // the color used to render the text over the background color
        defaults.put("ProgressBar.selectionBackground", new ColorUIResource(Color.white));
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setBorderPainted(false);
    }

    public void updateActiveLabel() {
        _workIntensity = workSlider.getValue();
        _priority = prioritySlider.getValue();
        activeLabel.setText("<html><font color='orange'>Priority : " + _priority + "</font>, <font color='gray'>Work Intensity : " + _workIntensity + "</font></html>");
    }

    public void activeBorder() {
        if (active) {
            panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.white),"<html><font color='white'>Thread : " + threadID + ",  </font><font color='red'>ACTIVE</font><font color='white'>,  " + this.getRemainingTime() + " miliseconds</font></html>"));
            panel.repaint();
            panel.revalidate();
        } else {
            panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.white),"<html><font color='white'>Thread : " + threadID + ",  </font><font color='blue'>INACTIVE</font><font color='white'>,  " + this.getRemainingTime() + " miliseconds</font></html>"));
            panel.repaint();
            panel.revalidate();
        }
    }

    public void setTickThread(int amount) {
        if (amount <= 4) {
            prioritySlider.setMinorTickSpacing(1);
            prioritySlider.setPaintTicks(true);
            prioritySlider.setSnapToTicks(true);
            workSlider.setMinorTickSpacing(1);
            workSlider.setPaintTicks(true);
            workSlider.setSnapToTicks(true);
        } else {
            prioritySlider.setPaintTicks(false);
            workSlider.setPaintTicks(false);
        }
        panel.repaint();
        panel.revalidate();
    }

    public void setGlobalSpeed(int spd) {
        globalSpeed = spd;
    }

    public int getGlobalSpeed() {
        return globalSpeed;
    }

    public void setMultipleQueueLevel() {
        if (_multipleQueueLevel == 5) {
            _multipleQueueLevel = 1;
        } else {
            _multipleQueueLevel++;
        }
    }

    public int getMultipleQueueLevel() {
        return _multipleQueueLevel;
    }

    public int getThreadID() {
        return threadID;
    }

    public JPanel getGUI() {
        activeBorder();
        return panel;
    }
    
    public void terminate() {
        running = false;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public void setReportTerminate(boolean reportTerminate) {
        _reportTerminate = reportTerminate;
    }

    public boolean getReportTerminate() {
        return _reportTerminate;
    }

    public void setPriorityComparable(boolean priorityComparable) {
        _priorityComparable = priorityComparable;
        System.out.println("Thread " + threadID + " : Priority comparable = " + _priorityComparable);
    }

    @Override
    public void run(){
        for (int i = 1; i <= 100; i++) {
            active = true;
            activeBorder();
            progressBar.setValue(i);
            try {
                Thread.sleep(globalSpeed * 50); // global speed
                Thread.sleep(_workIntensity * 20); // thread work intensity
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            if(i == 100) {
                panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.white),"<html><font color='white'>Thread : " + threadID + ",  </font><font color='green'>FINISHED</font></html>"));
                panel.repaint();
                panel.revalidate();
                isTerminated = true;
                terminate();
            }
        }
    } 

    public int getRemainingTime() {
        return (100 - this.progressBar.getValue()) * (this._workIntensity * 20 + this.globalSpeed * 50);
    }

    @Override
    public int compareTo (MyThreads other) {
        if (_priorityComparable) {
            System.out.println("Thread " + threadID + " : Priority comparable used.");
            return Integer.compare(this._priority, other._priority);
        } else {
            System.out.println("Thread " + threadID + " : Shortest remaining time comparable used.");
            return Integer.compare(this.getRemainingTime(), other.getRemainingTime());
        } 
    }
}