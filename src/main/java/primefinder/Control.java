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
    private volatile int totalNumbers = 0;
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

    /**
     * This method returns true if the threads of Control
     * are paused, false  otherwise
     * @return true if threads of Control are paused, false otherwise
     */
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
                System.out.println("Printed " + totalNumbers +" numbers. Press Enter to continue...");
                scan.nextLine();
                resumeThreads();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        scan.close();
    }

    /**
     * This method block the thread that use the
     * total numbers and updates the value
     * adding 1 digit to the current value.
     */
    public synchronized void addTotalNumbers(){
        this.totalNumbers++;
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
