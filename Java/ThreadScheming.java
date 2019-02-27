import java.util.List;
import java.util.ArrayList;

public class ThreadScheming {

    private List<MyThreads> _listThreads;
    private int _amountThreads;

    public void setThreads(List<MyThreads> listThreads, int amountThreads) {
        _listThreads = listThreads;
        _amountThreads = amountThreads;
    }

    public void firstComeFirstServed() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (count < _amountThreads) {
                    _listThreads.get(count).start();
                    while (_listThreads.get(count).isAlive()) { }
                    _listThreads.get(count).terminate();
                    count++;
                }
            }
        });  
        t1.start();
    }

    public void roundRobin() {
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                int amountFinished = 0;
                /*for (int i = 0; i < threads.length; i++) {
                    System.out.println("we are a go");
                    if (threads[i].getRunning()) {
                        try {
                            threads[i].start();
                            System.out.println(i + " : started");
                        } catch (Exception e) {
                            threads[i].resume();
                            System.out.println(i + " : resumed");
                        }
                        int count = 0;
                        while (count < 60) { 
                            System.out.println(count + " : count running");
                            count++;
                        }
                        try {
                            threads[i].suspend();
                            System.out.println(i + " : suspend");
                        } catch (Exception e) {
                            threads[i].terminate();
                            System.out.println(i + " : terminate, failure");
                        }
                    }
                }*/
            }
        });  
        t2.start();
    }
}