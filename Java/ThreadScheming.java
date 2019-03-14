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
    private boolean _preStop = false;
    private boolean _schemeWasSuspendedT = false;
    private boolean _schemeWasSuspendedQ = false;
    private int _schemeWasSuspendedThread;
    private int _schemeWasSuspendedQuantum;

    // CONSTRUCTOR 

    public ThreadScheming (JButton buttonStart, JButton buttonStop, JButton buttonReset) {
        _buttonStart = buttonStart;
        _buttonStop = buttonStop;
        _buttonReset = buttonReset;
    }

    // METHODS

    private int determineThreadI() { // determines where for loop starts
        if (_schemeWasSuspendedT) {
            _schemeWasSuspendedT = false;
            return _schemeWasSuspendedThread;
        } else {
            return 0;
        }
    }

    private int determineQuantumI() { // determines where for loop starts
        if (_schemeWasSuspendedQ) {
            _schemeWasSuspendedQ = false;
            return _schemeWasSuspendedQuantum;
        } else {
            return 0;
        }
    }

    public void buttonFinished() {
        _buttonStart.setText("Finished");
        _buttonStart.setEnabled(false);
        _buttonStop.setEnabled(false);
        _buttonReset.setEnabled(true);
    }

    public void buttonStart() {
        _buttonStart.setText("Active");
        _buttonStart.setEnabled(false);
        _buttonStop.setEnabled(true);
        _buttonReset.setEnabled(false);
    }

    public void setThreads(List<MyThreads> listThreads, int amountThreads) {
        _listThreads = listThreads;
        _amountThreads = amountThreads;
    }

    public void comparableSort(boolean priority) {
        for (MyThreads t:_listThreads) {
            t.setPriorityComparable(priority); // set comparable to use priority
        }
        _listThreads.sort(null); // sort accordingly
    }

    public int getActiveThreads() {
        int count = 0;
        for (int i = 0; i < _listThreads.size(); i++) {
            if (_listThreads.get(i).isTerminated()) {
                count++;
            } 
        }
        System.out.println("Threads " + count + " completed of " + _listThreads.size() + ".");
        return count;
    }

    public void suspendScheme() {
        _schemeWasSuspendedT = true;
        _activeScheme.suspend();
        for (MyThreads t:_listThreads) {
            try {
                t.suspend();
            } catch (Exception e) {
                System.out.println("[ ERR ] Thread Suspend : " + t.getThreadID());
            }
        }
    }

    public void resumeScheme() {
        _activeScheme.resume();
        try {
            _activeThread.resume();
        } catch (Exception e) {
            System.out.println("[ ERR ] Thread Resume : " + _activeThread.getThreadID());
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

    // SCHEME METHODS
    
    private void schemeThreadStart(int i) {
        // either start of resume current thread
        try {
            _listThreads.get(i).start();
        } catch (Exception ex) {
            _listThreads.get(i).resume();
        }
    }

    private void schemeThreadQuantum(int quantumTime) {
         // threads execute their quantum
         try {
            for (int i = determineQuantumI(); i < quantumTime; i++) {
                System.out.print("Thread " + _activeThread.getThreadID() + " enters quantum : " + i + "\r");
                Thread.sleep(1); // quantum
                _schemeWasSuspendedQuantum = i + 1;
            }
            System.out.println("");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void schemeThreadMultipleQuantum() {
        // threads execute their quantum
        try {
            int quantumTime = _activeThread.getMultipleQueueLevel() * 360; // time is based on level (1 is 360ms to 5 which is 1800ms)
            schemeThreadQuantum(quantumTime);
            _activeThread.setMultipleQueueLevel(); // increments level for next quantum
        } catch (Exception ex) {
            
        }
    }

    private void schemeThreadSuspend(int i) {
        // try to suspend thread if not finished
        try {
            _listThreads.get(i).suspend();
        } catch (Exception ex) {
           System.out.println("Thread " + i + " failed to suspend, terminated status: " + _listThreads.get(i).isTerminated());
        }
    }

    private void schemeIsStopped() {
        // do not execute further if scheme was stopped, loops in this section
        System.out.println("Scheme is stopped.");
        while (_preStop) { 
            try {
                Thread.sleep(1); // less CPU intensive
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Scheme resumes.");
    }

    private void schemeSetActiveThread(int i) {
        // assign active thread globally
        _schemeWasSuspendedThread = i;
        _activeThread = _listThreads.get(i);
    }

    private void schemeWaitForThread(int i) {
        System.out.println("Scheme waiting for thread to finish.");
        while (_listThreads.get(i).isAlive()) {
            try {
                Thread.sleep(1); // less work intensive on CPU
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // THREADS SCHEMES

    public void FCFS() { // first-come-first-serve
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                methodFCFS(); // start scheme
            }
        }); 
        _activeScheme = t1; 
        t1.start();
    }

    private void methodFCFS() {
        buttonStart();
        int count = 0;
        while (count < _amountThreads) {
            schemeSetActiveThread(count); // set active thread
            schemeThreadStart(count); // start thread
            schemeWaitForThread(count); // wait for thread to finish
            count++; // move to next
        }
        buttonFinished();
    }

    public void MQ() { // multiple queues
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                methodMQ(); // run scheme
            }
        });  
        _activeScheme = t4;
        t4.start();
    }

    private void methodMQ() {
        while (getActiveThreads() < _amountThreads) { // run while their are unfinished threads
            buttonStart(); // set buttons to active scheme config
            for (int i = determineThreadI(); i < _amountThreads; i++) {
                schemeSetActiveThread(i); // set active thread
                if (!_listThreads.get(i).isTerminated()) { // skip thread if finished
                    schemeIsStopped(); // execution is stopped
                    schemeThreadStart(i); // start thread
                    schemeThreadMultipleQuantum(); // thread quantum, in miliseconds
                    schemeThreadSuspend(i); // suspend thread
                }   
            }
        }
        buttonFinished(); // set buttons to stopped scheme config
    }

    public void PS() { // priority-scheduling
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                comparableSort(true); // turn priority comparable on
                methodPS(); // start scheme
            }
        });  
        _activeScheme = t4;
        t4.start();
    }

    private void methodPS() {
        while (getActiveThreads() < _amountThreads) { // run while their are unfinished threads
            buttonStart(); // set buttons to active scheme config
            for (int i = determineThreadI(); i < _amountThreads; i++) {
                schemeSetActiveThread(i); // set active thread
                if (!_listThreads.get(i).isTerminated()) { // skip thread if finished
                    schemeIsStopped(); // execution is stopped
                    schemeThreadStart(i); // start thread
                    schemeWaitForThread(i); // wait for thread to finish
                    schemeThreadSuspend(i); // suspend thread
                }  
                comparableSort(true); // turn priority comparable on 
            }
        }
        buttonFinished(); // set buttons to stopped scheme config
    }
    
    public void RR() { // round-robin
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                methodRR(); // run scheme
            }
        });  
        _activeScheme = t2;
        t2.start();
    } 

    private void methodRR() {
        while (getActiveThreads() < _amountThreads) { // run while their are unfinished threads
            buttonStart(); // set buttons to active scheme config
            for (int i = determineThreadI(); i < _amountThreads; i++) {
                schemeSetActiveThread(i); // set active thread
                if (!_listThreads.get(i).isTerminated()) { // skip thread if finished
                    schemeIsStopped(); // execution is stopped
                    schemeThreadStart(i); // start thread
                    schemeThreadQuantum(600); // thread quantum, in miliseconds
                    schemeThreadSuspend(i); // suspend thread
                }   
            }
        }
        buttonFinished(); // set buttons to stopped scheme config
    }
    
    public void SJF() { // shortest-job-first
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                comparableSort(false); // sort thread on shortest remaining time
                methodFCFS(); // uses first-come-first-serve scheme, start
            }
        });  
        _activeScheme = t3;
        t3.start();
    }

    public void SRT() { // shortest-remaining-time
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                comparableSort(false); // turn shortest comparable on
                methodSRT(); // run scheme
            }
        });  
        _activeScheme = t4;
        t4.start();
    }

    private void methodSRT() {
        while (getActiveThreads() < _amountThreads) { // run while their are unfinished threads
            buttonStart(); // set buttons to active scheme config
            for (int i = determineThreadI(); i < _amountThreads; i++) {
                schemeSetActiveThread(i); // set active thread
                if (!_listThreads.get(i).isTerminated()) { // skip thread if finished
                    schemeIsStopped(); // execution is stopped
                    schemeThreadStart(i); // start thread
                    schemeWaitForThread(i); // wait for thread to finish
                    schemeThreadSuspend(i); // suspend thread
                }  
                comparableSort(false); // sort
            }
        }
        buttonFinished(); // set buttons to stopped scheme config
    }
}