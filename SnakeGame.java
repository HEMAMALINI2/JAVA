import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private final int DOT_SIZE = 10;    // Size of snake parts
    private final int ALL_DOTS = 900;   // Max possible dots
    private final int RAND_POS = 29;    // Random position for food
    private final int DELAY = 140;      // Speed of the game (lower is faster)
    private final int x[] = new int[ALL_DOTS]; // X coordinates of snake parts
    private final int y[] = new int[ALL_DOTS]; // Y coordinates of snake parts
    private int dots;          // Current length of the snake
    private int apple_x;       // X position of the apple
    private int apple_y;       // Y position of the apple
    private int points = 0;    // Player's points
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    private Timer timer;
    private Random random = new Random();
    // Define the screen width and height for fullscreen mode
    private int screenWidth;
    private int screenHeight;
    public SnakeGame() {
        initBoard();
    }
    private void initBoard() {
        addKeyListener(new TAdapter());
        setBackground(Color.pink);  // Set background color to green
        setFocusable(true);          // So that key events are captured
        // Use the screen resolution for fullscreen mode
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        initGame();
    }
    private void initGame() {
        dots = 3;  // Initial snake length
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        locateApple();  // Place the first apple
        timer = new Timer(DELAY, this);
        timer.start();  // Start the game loop
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
    private void doDrawing(Graphics g) {
        if (inGame) {
            // Draw game title
            String title = "Snake Game";
            Font titleFont = new Font("Helvetica", Font.BOLD, 36);
            g.setFont(titleFont);
            g.setColor(Color.black);
            FontMetrics titleMetrics = getFontMetrics(titleFont);
            g.drawString(title, (screenWidth - titleMetrics.stringWidth(title)) / 2, 40);
            // Draw apple
            g.setColor(Color.red);
            g.fillOval(apple_x, apple_y, DOT_SIZE, DOT_SIZE);
            // Draw snake
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.setColor(Color.green);  // Head is black
                } else {
                    g.setColor(Color.black);  // Body is white
                }
                g.fillRect(x[z], y[z], DOT_SIZE, DOT_SIZE);
            }
            // Display the current score
            String scoreMsg = "Points: " + points;
            Font scoreFont = new Font("Helvetica", Font.BOLD, 20);
            g.setFont(scoreFont);
            g.setColor(Color.black);
            g.drawString(scoreMsg, 10, screenHeight - 30);
            // Display a winning message if points reach 10
            if (points >= 10) {
                String winMsg = "You Win!";
                Font winFont = new Font("Helvetica", Font.BOLD, 24);
                g.setFont(winFont);
                FontMetrics metr = getFontMetrics(winFont);
                g.setColor(Color.black);
                g.drawString(winMsg, (screenWidth - metr.stringWidth(winMsg)) / 2, screenHeight / 2);
                inGame = false;
                timer.stop();
            }
            Toolkit.getDefaultToolkit().sync();  // Smooth animation
        } else {
            gameOver(g);
        }
    }
    private void gameOver(Graphics g) {
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);
        g.setColor(Color.black);
        g.setFont(small);
        g.drawString(msg, (screenWidth - metr.stringWidth(msg)) / 2, screenHeight / 2);
    }
    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;  // Snake grows when it eats the apple
            points++;  // Increase points when apple is eaten
            locateApple();
        }
    }
    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }
        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }
        if (rightDirection) {
            x[0] += DOT_SIZE;
        }
        if (upDirection) {
            y[0] -= DOT_SIZE;
        }
        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }
    private void checkCollision() {
        // Check if snake hits itself
        for (int z = dots; z > 0; z--) {
            if ((z > 3) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }
        // Check if snake hits the borders
        if (y[0] >= screenHeight || y[0] < 0 || x[0] >= screenWidth || x[0] < 0) {
            inGame = false;
        }
        if (!inGame) {
            timer.stop();  // Stop the game when it's over
        }
    }
    private void locateApple() {
        apple_x = random.nextInt(screenWidth / DOT_SIZE) * DOT_SIZE;
        apple_y = random.nextInt(screenHeight / DOT_SIZE) * DOT_SIZE;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();
        }
        repaint();  // Redraw the game
    }
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            // Arrow keys and WASD keys control
            if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        // Make the game fullscreen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);  // Remove window borders
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
