import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class FlippingTilesGame extends JFrame implements ActionListener {
    private JPanel gamePanel, timerPanel;
    private JLabel timerLabel;
    private JButton startButton, retryButton, quitButton;
    private JButton[] buttons;
    private boolean[] flipped;
    private ArrayList<Integer> tileValues;
    private int firstIndex = -1, secondIndex = -1;
    private Timer flipTimer, gameTimer;
    private int matchesFound = 0;
    private final int TOTAL_PAIRS = 8;
    private int timeRemaining = 60;  // 1 minutes in seconds
    private boolean isAnimating = false;

    public FlippingTilesGame() {
        setTitle("Flipping Tiles Game");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        showStartScreen();

        setVisible(true);
    }

    private void showStartScreen() {
        JPanel startPanel = new JPanel();
        startPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.addActionListener(this);

        quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Arial", Font.BOLD, 24));
        quitButton.addActionListener(this);

        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        startPanel.add(startButton, gbc);

        gbc.gridy = 1;
        startPanel.add(quitButton, gbc);

        add(startPanel, BorderLayout.CENTER);
    }

    private void initializeGame() {
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(4, 4, 5, 5)); 

        buttons = new JButton[16];
        flipped = new boolean[16];
        tileValues = new ArrayList<>();

        for (int i = 0; i < TOTAL_PAIRS; i++) {
            tileValues.add(i);
            tileValues.add(i);
        }
        Collections.shuffle(tileValues);

        for (int i = 0; i < 16; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 24));
            buttons[i].addActionListener(this);
            gamePanel.add(buttons[i]);
        }

        timerPanel = new JPanel();
        timerLabel = new JLabel("Time: 01:00", JLabel.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerPanel.add(timerLabel);

        add(timerPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        gamePanel.revalidate();
        gamePanel.repaint();

        startCountdownTimer();
    }

    private void startCountdownTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                timeRemaining--;
                updateTimerLabel();

                if (timeRemaining == 0) {
                    gameTimer.stop();
                    showLoseMessage();
                }
            }
        });
        gameTimer.start();
    }

    private void updateTimerLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        String timeText = String.format("Time: %02d:%02d", minutes, seconds);
        timerLabel.setText(timeText);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            getContentPane().removeAll();
            initializeGame();
            matchesFound = 0;
            timeRemaining = 60;  
            revalidate();
            repaint();
        } else if (e.getSource() == retryButton) {
            getContentPane().removeAll();
            initializeGame();
            matchesFound = 0;
            timeRemaining = 60;  
            revalidate();
            repaint();
        } else if (e.getSource() == quitButton) {
            System.exit(0);
        } else {
            for (int i = 0; i < buttons.length; i++) {
                if (e.getSource() == buttons[i] && !flipped[i] && !isAnimating) {
                    handleTileFlip(i);
                }
            }
        }
    }

    private void handleTileFlip(int index) {
        if (firstIndex == -1) {
            firstIndex = index;
            animateFlip(index, true);  
        } else if (secondIndex == -1) {
            secondIndex = index;
            animateFlip(index, true);  

            if (tileValues.get(firstIndex).equals(tileValues.get(secondIndex))) {
                flipped[firstIndex] = true;
                flipped[secondIndex] = true;
                matchesFound++;
                firstIndex = -1;
                secondIndex = -1;

                if (matchesFound == TOTAL_PAIRS) {
                    gameTimer.stop();  
                    showWinMessage();
                }
            } else {
                isAnimating = true;
                flipTimer = new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        animateFlip(firstIndex, false);  
                        animateFlip(secondIndex, false);
                        firstIndex = -1;
                        secondIndex = -1;
                        isAnimating = false;
                        flipTimer.stop();
                    }
                });
                flipTimer.start();
            }
        }
    }

    private void showWinMessage() {
        JOptionPane.showMessageDialog(this, "You Won!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
        resetGame();
    }

    private void showLoseMessage() {
        getContentPane().removeAll();
        JPanel losePanel = new JPanel();
        losePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel loseLabel = new JLabel("You Lose!", JLabel.CENTER);
        loseLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        losePanel.add(loseLabel, gbc);

        retryButton = new JButton("Retry");
        retryButton.setFont(new Font("Arial", Font.BOLD, 24));
        retryButton.addActionListener(this);

        quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Arial", Font.BOLD, 24));
        quitButton.addActionListener(this);

        gbc.gridy = 1;
        losePanel.add(retryButton, gbc);

        gbc.gridy = 2;
        losePanel.add(quitButton, gbc);

        add(losePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void resetGame() {
        getContentPane().removeAll();
        showStartScreen();
        revalidate();
        repaint();
    }

    // Modify this method to use images instead of text
    private void animateFlip(int index, boolean showValue) {
        final JButton button = buttons[index];
        final int animationDuration = 500;  
        final int animationSteps = 10;  
        final int stepDelay = animationDuration / animationSteps;

        Timer flipAnimation = new Timer(stepDelay, new ActionListener() {
            int currentStep = 0;

            public void actionPerformed(ActionEvent evt) {
                double scale = 1.0 - Math.abs((currentStep - animationSteps / 2.0) / (animationSteps / 2.0));
                button.setPreferredSize(new Dimension((int) (100 * scale), button.getHeight()));

                if (currentStep == animationSteps / 2) {
                    // Change the content at the midpoint of the animation
                    if (showValue) {
                        // Load image for the corresponding tile value
                        button.setIcon(new ImageIcon("C:\\VS Code\\Java\\image" + tileValues.get(index) + ".jpg"));
                       
                    } else {
                        button.setIcon(null);  // Hide image when flipping back
                    }
                }

                currentStep++;
                button.revalidate();
                button.repaint();

                if (currentStep >= animationSteps) {
                    ((Timer) evt.getSource()).stop();
                }
            }
        });
        flipAnimation.start();
    }

    public static void main(String[] args) {
        new FlippingTilesGame();
    }
}
