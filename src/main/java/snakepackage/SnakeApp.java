package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jd-
 *
 */
public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
        new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2,
        3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
        new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2,
        GridSize.GRID_HEIGHT - 2)};
    private JFrame frame;
    private static Board board;
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];

    // Referencias a los JLabel
    private JLabel worstSnakeLabel;
    private boolean worstSnakeHasBeenSet = false;
    private JLabel longestSnakeLabel;

    // Referencias a los botones

    private static CountDownLatch latch = new CountDownLatch(MAX_THREADS);


    public SnakeApp() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(618, 640);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 40);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);
        board = new Board();
        
        
        frame.add(board,BorderLayout.CENTER);
        
        JPanel actionsBPabel=new JPanel();
        actionsBPabel.setLayout(new FlowLayout());
        actionsBPabel.add(new JButton("Action "));
        frame.add(actionsBPabel,BorderLayout.SOUTH);

        // Crear panel para los letreros
        // Crear panel para los letreros
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        worstSnakeLabel = new JLabel("La peor serpiente: ");
        longestSnakeLabel = new JLabel("La serpiente viva más larga: ");
        infoPanel.add(worstSnakeLabel);
        infoPanel.add(longestSnakeLabel);
        frame.add(infoPanel, BorderLayout.EAST);

    }

    public static void main(String[] args) {
        app = new SnakeApp();
        app.init();
    }

    private void init() {
        
        
        
        for (int i = 0; i != MAX_THREADS; i++) {
            
            snakes[i] = new Snake(i + 1, spawn[i], i + 1, latch);
            snakes[i].addObserver(board);
            thread[i] = new Thread(snakes[i]);
            thread[i].start();
        }

        frame.setVisible(true);


        try {
            latch.await(); // Espera a que todas las serpientes terminen
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Thread (snake) status:");
        for (int i = 0; i != MAX_THREADS; i++) {
            System.out.println("["+i+"] :"+thread[i].getState());
        }
        

    }

    public static SnakeApp getApp() {
        return app;
    }

    public synchronized void updateWorstSnake(String text) {
        if (!worstSnakeHasBeenSet) {
            worstSnakeHasBeenSet = true;
            worstSnakeLabel.setText("La peor serpiente: " + text);
        }
    }

    public synchronized void updateLongestSnake() {
        Snake longestSnake = snakes[0];
        for (int i = 0; i != MAX_THREADS; i++) {
            if (snakes[i] != null) {
                if (snakes[i].getBody().size() > longestSnake.getBody().size()) {
                    longestSnake = snakes[i];
                }
            }
        }
        longestSnakeLabel.setText("La serpiente viva más larga: " + longestSnake.getIdt() + " " + longestSnake.getBody().size());
    }

}
