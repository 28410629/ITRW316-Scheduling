
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;

public class ThreadScheming {

    private List<MyThreads> _listThreads;
    private int _amountThreads;
    private JButton _buttonStart;
    private JButton _buttonStop;
    private JButton _buttonReset;
    private Thread _activeScheme;
    private MyThreads _activeThread;
    private boolean _schemeWasSuspendedT = false;
    private boolean _schemeWasSuspendedQ = false;
    private boolean _schemeWasSuspendedC = false;
    private int _schemeWasSuspendedThread;
    private int _schemeWasSuspendedQuantum;
    private int _schemeWasSuspendedCount;
    private Interface _mainInterface;
    
    // STASTISTICS

    private long startTime;
    private long endTime;

    // CONSTRUCTOR 

    public ThreadScheming (JButton buttonStart, JButton buttonStop, JButton buttonReset, Interface mainInterface) {
        _buttonStart = buttonStart;
        _buttonStop = buttonStop;
        _buttonReset = buttonReset;
        _mainInterface = mainInterface;
    }

    // METHODS

    public static void resumeAt(int value, String what) 
    { 
        String str = what + " resumes at: " + value + "\n";
        try { 
            BufferedWriter out = new BufferedWriter(new FileWriter("stop_resume.txt", true)); 
            out.write(str); 
            out.close(); 
        } 
        catch (IOException e) { 
            System.out.println("exception occoured" + e); 
        } 
    } 

    private int determineThreadI() { // determines where for loop starts
        if (_schemeWasSuspendedT) {
            _schemeWasSuspendedT = false;
            resumeAt(_schemeWasSuspendedThread, "Thread");
            return _schemeWasSuspendedThread;
        } else {
            return 0;
        }
    }

    private int determineCount() { // determines where for loop starts
        if (_schemeWasSuspendedC) {
            _schemeWasSuspendedC = false;
            resumeAt(_schemeWasSuspendedCount, "Count");
            return _schemeWasSuspendedCount;
        } else {
            return 0;
        }
    }

    private int determineQuantumI() { // determines where for loop starts
        if (_schemeWasSuspendedQ) {
            _schemeWasSuspendedQ = false;
            resumeAt(_schemeWasSuspendedQuantum, "Quantum");
            return _schemeWasSuspendedQuantum;
        } else {
            return 0;
        }
    }

    public void buttonFinished() {
        _buttonStart.setText("<html><font color='white'>Finished</font></html>");
        _buttonStart.setEnabled(false);
        _buttonStop.setEnabled(false);
        _buttonReset.setEnabled(true);
    }

    public void buttonStart() {
        _buttonStart.setText("<html><font color='white'>Active</font></html>");
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
        return count;
    }

    public void suspendScheme() {
        _schemeWasSuspendedT = true;
        _schemeWasSuspendedC = true;
        _schemeWasSuspendedQ = true;
        _activeScheme.stop();
        for (MyThreads t:_listThreads) {
            try {
                t.suspend();
            } catch (Exception e) {
                System.out.println("[ ERR ] Thread Suspend : " + t.getThreadID());
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
        _mainInterface.updateExecutionBar(i);
    }

    private void schemeThreadQuantum(int quantumTime) {
         // threads execute their quantum
         try {
            for (int i = determineQuantumI(); i < quantumTime; i++) {
                System.out.print("Thread " + _activeThread.getThreadID() + " enters quantum : " + i + "\r");
                Thread.sleep(1); // quantum
                if (_activeThread.isTerminated()) {
                    System.out.println("");
                    return;
                }
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

    private void schemeSetActiveThread(int i) {
        // assign active thread globally
        _schemeWasSuspendedThread = i;
        _activeThread = _listThreads.get(i);
    }

    private void schemeWaitForThread(int i) {
        _schemeWasSuspendedCount = determineCount();
        while (_listThreads.get(i).isAlive()) {
            try {
                System.out.print("Scheme waiting for Thread " + _activeThread.getThreadID() + " to finish : " + _schemeWasSuspendedCount + "\r");
                Thread.sleep(1); // less work intensive on CPU
                _schemeWasSuspendedCount++;
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }

    // THREADS SCHEMES

    public void FCFS() { // first-come-first-serve
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                startTime = System.nanoTime();
                methodFCFS(false); // start scheme
                endTime = System.nanoTime();
                System.out.println("Execution time in milliseconds : " + ((endTime - startTime) / 1000000)); 
            }
        }); 
        _activeScheme = t1; 
        t1.start();
    }

    private void methodFCFS(boolean isSJF) {
        buttonStart();
        while (getActiveThreads() < _amountThreads) {
            for (int i = determineThreadI(); i < _amountThreads; i++) {
                if (isSJF) {
                    comparableSort(false); // sort thread on shortest remaining time
                }
                if (!_listThreads.get(i).isTerminated()) {
                    schemeSetActiveThread(i); // set active thread
                    schemeThreadStart(i); // start thread
                    schemeWaitForThread(i); // wait for thread to finish
                }
            }
        }
        buttonFinished();
    }

    public void MQ() { // multiple queues
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                startTime = System.nanoTime();
                methodRR(true); // run scheme
                endTime = System.nanoTime();
                System.out.println("Execution time in milliseconds : " + ((endTime - startTime) / 1000000)); 
            }
        });  
        _activeScheme = t4;
        t4.start();
    }

    public void PS() { // priority-scheduling
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                startTime = System.nanoTime();
                comparableSort(true); // sort thread on priority
                methodSRT(true); // start scheme
                endTime = System.nanoTime();
                System.out.println("Execution time in milliseconds : " + ((endTime - startTime) / 1000000));               
            }
        });  
        _activeScheme = t4;
        t4.start();
    }
    
    public void RR() { // round-robin
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                startTime = System.nanoTime();
                methodRR(false); // run scheme
                endTime = System.nanoTime();
                System.out.println("Execution time in milliseconds : " + ((endTime - startTime) / 1000000));
            }
        });  
        _activeScheme = t2;
        t2.start();
    } 

    private void methodRR(boolean isMQ) {
        while (getActiveThreads() < _amountThreads) { // run while their are unfinished threads
            for (int i = determineThreadI(); i < _amountThreads; i++) {
                schemeSetActiveThread(i); // set active thread
                if (!_listThreads.get(i).isTerminated()) { // skip thread if finished
                    schemeThreadStart(i); // start thread
                    if (isMQ) {
                        schemeThreadMultipleQuantum(); // thread quantum, in miliseconds
                    } else {
                        schemeThreadQuantum(600); // thread quantum, in miliseconds
                    }
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
                startTime = System.nanoTime();
                methodFCFS(true); // uses first-come-first-serve scheme, start
                endTime = System.nanoTime();
                System.out.println("Execution time in milliseconds : " + ((endTime - startTime) / 1000000));
            }
        });  
        _activeScheme = t3;
        t3.start();
    }

    public void SRT() { // shortest-remaining-time
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                startTime = System.nanoTime();
                comparableSort(false); // sort
                methodSRT(false); // run scheme
                endTime = System.nanoTime();
                System.out.println("Execution time in milliseconds : " + ((endTime - startTime) / 1000000));
            }
        });  
        _activeScheme = t4;
        t4.start();
    }

    private void methodSRT(boolean isPS) {
        while (getActiveThreads() < _amountThreads) { // run while their are unfinished threads
            for (int i = 0; i < _amountThreads; i++) {
                if (isPS) {
                    comparableSort(true);
                } else {
                    comparableSort(false);
                }
                schemeSetActiveThread(i); // set active thread
                if (!_listThreads.get(i).isTerminated()) { // skip thread if finished
                    schemeThreadStart(i); // start thread
                    schemeWaitForThread(i); // wait for thread to finish
                    schemeThreadSuspend(i); // suspend thread
                }  
            }
        }
        buttonFinished(); // set buttons to stopped scheme config
    }
}