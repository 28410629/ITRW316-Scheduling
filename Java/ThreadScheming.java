import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class ThreadScheming {

    private List<MyThreads> _listThreads;
    private int _amountThreads;
    private JButton _buttonStart;
    private JButton _buttonStop;
    private JButton _buttonReset;
    private boolean _isStopped;

    public ThreadScheming (JButton buttonStart, JButton buttonStop, JButton buttonReset) {
        _buttonStart = buttonStart;
        _buttonStop = buttonStop;
        _buttonReset = buttonReset;
    }

    public void buttonFinished() {
        _buttonStart.setText("Finished");
        _buttonStart.setEnabled(false);
        _buttonStop.setEnabled(false);
        _buttonReset.setEnabled(true);
    }

    public void setThreads(List<MyThreads> listThreads, int amountThreads) {
        _listThreads = listThreads;
        _amountThreads = amountThreads;
    }

    public void setIsStopped(boolean isStopped) {
        _isStopped = isStopped;
    }

    // non-preemptive
    public void firstComeFirstServed() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (count < _amountThreads) {
                    _listThreads.get(count).start();
                    while (_listThreads.get(count).isAlive()) {
                        try {
                            Thread.sleep(600); // less work intensive on CPU
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                     }
                    _listThreads.get(count).terminate();
                    count++;
                }
                buttonFinished();
            }
        });  
        t1.start();
    }

    // preemptive
    public void roundRobin() {
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                
                int count = 0;
                while (count < _amountThreads) {
                    for (int i = 0; i < _amountThreads; i++) {
                        if (_listThreads.get(i).isTerminated() && !_listThreads.get(i).getReportTerminate()) {
                            count++;
                            _listThreads.get(i).setReportTerminate(true); // this thread is reported to be finished
                            System.out.println("RR_Finished : " + count + " / " + _amountThreads);
                        } else {
                            while (_isStopped) { 
                                try {
                                    Thread.sleep(600);  // less work intensive on CPU
                                } catch (InterruptedException ex) {
                                    Thread.currentThread().interrupt();
                                }
                             }
                            try {
                                _listThreads.get(i).start();
                            } catch (Exception ex) {
                                _listThreads.get(i).resume();
                            }
                            try {
                                Thread.sleep(600); // quantum
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                            _listThreads.get(i).suspend();
                        }
                    }
                }
                buttonFinished();
            }
        });  
        t2.start();
    }

    // non-preemptive
    public void shortestJobFirst() {
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                
                while (count < _amountThreads) {
                    // sort
                    _listThreads.sort(null); 
                    // run
                    _listThreads.get(count).start();
                    while (_listThreads.get(count).isAlive()) {
                        try {
                            Thread.sleep(600); // less work intensive on CPU
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                     }
                    _listThreads.get(count).terminate();
                    count++;
                }
                buttonFinished();
            }
        });  
        t2.start();
    }
}