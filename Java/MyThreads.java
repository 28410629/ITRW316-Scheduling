import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

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
    private boolean _shortestComparable = false;

    public MyThreads(int threadID, int Speed) {
        super();
        createGUI(threadID);
        globalSpeed = Speed;
    }

    public void createGUI(int id) {
        threadID = id;
        // create panel
        panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Thread : " + threadID));
        panel.setLayout(gLayout);
        // create components 
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        activeLabel();
        prioritySlider.setBackground(new Color(255,140,0));
        workSlider.setBackground(new Color(128,128,128));
        activeLabel.setText("<html><font color='orange'>Priority : 0</font> | <font color='gray'>Work Intensity : 0</font></html>");
        // add listener events
        workSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateActiveLabel();
                activeLabel();
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

    public void updateActiveLabel() {
        _workIntensity = workSlider.getValue();
        _priority = prioritySlider.getValue();
        activeLabel.setText("<html><font color='orange'>Priority : " + _priority + "</font>, <font color='gray'>Work Intensity : " + _workIntensity + "</font></html>");
    }

    public void activeLabel() {
        if (active) {
            panel.setBorder(BorderFactory.createTitledBorder("<html>Thread : " + threadID + ", <font color='red'>ACTIVE</font>, <font color='purple'>" + this.getRemainingTime() + " miliseconds</font></html>"));
            panel.repaint();
            panel.revalidate();
        } else {
            panel.setBorder(BorderFactory.createTitledBorder("<html>Thread : " + threadID + ", <font color='blue'>INACTIVE</font>, <font color='purple'>" + this.getRemainingTime() + " miliseconds</font></html>"));
            panel.repaint();
            panel.revalidate();
        }
    }

    public void setGlobalSpeed(int spd) {
        globalSpeed = spd;
    }

    public int getGlobalSpeed() {
        return globalSpeed;
    }

    public int getThreadID() {
        return threadID;
    }

    public JPanel getGUI() {
        activeLabel();
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
    }

    public void setShortestComparable(boolean shortestComparable) {
        _shortestComparable = shortestComparable;
    }

    @Override
    public void run(){
        System.out.println(globalSpeed + " : " + threadID);
        for (int i = 1; i <= 100; i++) {
            active = true;
            activeLabel();
            progressBar.setValue(i);
            try {
                Thread.sleep(globalSpeed * 50); // global speed
                Thread.sleep(_workIntensity * 20); // thread work intensity
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            if(i == 100) {
                panel.setBorder(BorderFactory.createTitledBorder("<html>Thread : " + threadID + ", <font color='green'>FINISHED</font></html>"));
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
            return Integer.compare(this._priority, other._priority);
        } else if (_shortestComparable) {
            return Integer.compare(this.getRemainingTime(), other.getRemainingTime());
        } else {
            return Integer.compare(this._workIntensity, other._workIntensity);
        }
    }
}