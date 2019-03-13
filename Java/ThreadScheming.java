import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class ThreadScheming {

    private List<MyThreads> _listThreads;
    private int _amountThreads;
    private JButton _buttonStart;
    private JButton _buttonStop;
    private JButton _buttonReset;
    private Thread _activeScheme;
    private MyThreads _activeThread;
    private boolean _preemptive;
    private boolean _preStop = false;
    private boolean _priorityScheme;
    private boolean _shortestScheme;
    private boolean _roundScheme;

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

    public void suspendScheme() {
        if (_preemptive) {
            _preStop = true;
        } else {
            _activeScheme.suspend();
            for (MyThreads t:_listThreads) {
                try {
                    t.suspend();
                } catch (Exception e) {
                    System.out.println("[ ERR ] Thread Suspend : " + t.getThreadID());
                }
            }
        }
    }

    public void resumeScheme() {
        if (_preemptive) {
            _preStop = false;
        } else {
            _activeScheme.resume();
            try {
                _activeThread.resume();
            } catch (Exception e) {
                System.out.println("[ ERR ] Thread Resume : " + _activeThread.getThreadID());
            }
        }
    }

    public void terminateScheme() {
        _activeScheme.stop();
        for (MyThreads t:_listThreads) {
            try {
                t.stop();
            } catch (Exception e) {
                System.out.println("[ ERR ] Thread Stop : " + t.getThreadID());
            }
        }
    }

    public void threadUpdateScheme() {
        _activeScheme.stop();
        for (MyThreads t:_listThreads) {
            try {
                t.suspend();
            } catch (Exception e) {
                System.out.println("[ ERR ] Thread Suspend : " + t.getThreadID());
            }
        }
    }

    // non-preemptive
    public void FCFS() {
        _preemptive = false;
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                methodFCFS();
            }
        }); 
        _activeScheme = t1; 
        t1.start();
    }

    // inner-working of FCFS
    public void methodFCFS() {
        int count = 0;
        while (count < _amountThreads) {
            // start
            _listThreads.get(count).start();
            _activeThread = _listThreads.get(count);
            // run till thread is finished
            while (_listThreads.get(count).isAlive()) {
                try {
                    Thread.sleep(1); // less work intensive on CPU
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            // make sure thread is terminated
            _listThreads.get(count).terminate();
            // move to next
            count++;
        }
        buttonFinished();
    }

    // preemptive
    public void RR() {
        _preemptive = true;
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                // turn off comparable for normal round-robin
                _roundScheme = true;
                // run round-robin
                methodRR();
            }
        });  
        _activeScheme = t2;
        t2.start();
    }

    public void methodRR() {
        // counter for amount of threads that need to be finished
        int count = 0;
        _preStop = false;
        while (count < _amountThreads) {
            for (int i = 0; i < _amountThreads; i++) {
                // sort if priority otherwise do round-robin
                if (!_roundScheme) {
                    comparableSet();
                }
                _activeThread = _listThreads.get(i);
                if (_listThreads.get(i).isTerminated() && !_listThreads.get(i).getReportTerminate()) {
                    count++;
                    _listThreads.get(i).setReportTerminate(true); // this thread is reported to be finished
                    System.out.println("RR_Finished : " + count + " / " + _amountThreads);
                } else {
                    if (!_listThreads.get(i).isTerminated()) {
                        // do not execute further if scheme was stopped
                        while (_preStop) { 
                            try {
                                Thread.sleep(600); // less CPU intensive
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        // either start of resume current thread
                        try {
                            _listThreads.get(i).start();
                        } catch (Exception ex) {
                            _listThreads.get(i).resume();
                        }
                        // threads execute 
                        try {
                            Thread.sleep(600); // quantum
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        _listThreads.get(i).suspend();
                    } 
                }
            }
        }
        buttonFinished();
    }

    public void comparableSet() {
        // set comparable to be user
        for (MyThreads t:_listThreads) {
            t.setPriorityComparable(_priorityScheme);
        }
        for (MyThreads t:_listThreads) {
            t.setPriorityComparable(_shortestScheme);
        }
        // sort accordingly
        _listThreads.sort(null);
    }

    // non-preemptive (adding and removing threads not allowed)
    public void SJF() {
        _preemptive = false;
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                // sort
                _listThreads.sort(null); 
                // run FCFS on sorted threads
                methodFCFS();
            }
        });  
        _activeScheme = t3;
        t3.start();
    }

    // preemptive 
    public void PS() {
        _preemptive = true;
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                // turn priority comparable on and sort
                _roundScheme = false;
                _priorityScheme = true;
                _shortestScheme = false;
                // run RR on sorted threads
                methodRR();
            }
        });  
        _activeScheme = t4;
        t4.start();
    }

    // preemptive 
    public void SRT() {
        _preemptive = true;
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                // turn priority comparable on and sort
                _roundScheme = false;
                _priorityScheme = false;
                _shortestScheme = true;
                // run RR on sorted threads
                methodRR();
            }
        });  
        _activeScheme = t4;
        t4.start();
    }
}