/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primefinder;

import java.sql.Time;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class Control extends Thread {
    
    private final int NTHREADS = 3;
    private final int MAXVALUE = 30000000;
    private final int TMILISECONDS = 5000;
    private final Object monitor = new Object();

    private volatile boolean paused = false;

    private final int NDATA = MAXVALUE / NTHREADS;

    private PrimeFinderThread pft[];
    
    private Control() {
        super();
        this.pft = new  PrimeFinderThread[NTHREADS];

        int i;
        for(i = 0;i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i*NDATA, (i+1)*NDATA, monitor, this);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i*NDATA, MAXVALUE + 1, monitor, this);
    }
    
    public static Control newControl() {
        return new Control();
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in);
        execute();
        while (threadsAlive()){
            try{
                Thread.sleep(TMILISECONDS);
                pauseAll();
                scan.nextLine();
                resumeThreads();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        scan.close();
    }


    boolean threadsAlive(){
        synchronized (monitor){
            for(PrimeFinderThread p: pft){
                if(p.isAlive()){
                    return true;
                }
            }
            return false;
        }
    }
    void execute(){
        for(PrimeFinderThread p: pft){
            p.start();
        }
    }

    void resumeThreads(){
        synchronized (monitor){
            paused = false;
            monitor.notifyAll();
        }
    }

    void pauseAll(){
        synchronized (monitor) {
            paused = true;
        }
    }

}
