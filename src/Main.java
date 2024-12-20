/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #2
 * 1 - 5026221156 - Muhammad Ali Husain Ridwan
 * 2 - 5026221157 - Muhammad Afaf
 * 3 - 5026221162 - Raphael Andhika Pratama
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JPanel {
    private static final long serialVersionUID = 1L;

    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;
    private GameMode currentMode = GameMode.PLAYER_VS_PLAYER;
    private Seed playerSeed;
    private Seed aiSeed;
    private AIPlayer aiPlayer;

    public Main() {
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        if (currentMode == GameMode.PLAYER_VS_PLAYER || currentPlayer == playerSeed) {
                            currentState = board.stepGame(currentPlayer, row, col);
                            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                            if (currentMode == GameMode.PLAYER_VS_AI && currentState == State.PLAYING) {
                                aiMove();
                                currentPlayer = playerSeed;
                            }
                            if (currentState == State.PLAYING) {
                                SoundEffect.EAT_FOOD.play();
                            } else {
                                SoundEffect.DIE.play();
                            }
                        }
                    }
                } else {
                    newGame();
                }
                repaint();
            }
        });

        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        JButton hintButton = new JButton("Hint");
        hintButton.addActionListener(e -> showHint());
        this.add(hintButton, BorderLayout.PAGE_START); // Add button at the top

        initGame();
        newGame();
    }

    public void initGame() {
        board = new Board();
    }

    public void newGame() {
        chooseGameMode();
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }
        currentPlayer = Seed.CROSS;
        playerSeed = Seed.CROSS;
        aiSeed = Seed.NOUGHT;
        currentState = State.PLAYING;

        if (currentMode == GameMode.PLAYER_VS_AI) {
            aiPlayer.setSeed(aiSeed);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);

        board.paint(g);

        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
        }
    }

    public void chooseGameMode() {
        String[] options = {"Player vs Player", "Player vs AI (Minimax)", "Player vs AI (Table Lookup)"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose Game Mode:",
                "Game Mode",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 1) {
            currentMode = GameMode.PLAYER_VS_AI;
            aiPlayer = new AIPlayerMinimax(board);
        } else if (choice == 2) {
            currentMode = GameMode.PLAYER_VS_AI;
            aiPlayer = new AIPlayerTableLookup(board);
        } else {
            currentMode = GameMode.PLAYER_VS_PLAYER;
        }
    }

    private void aiMove() {
        if (aiPlayer != null) {
            int[] move = aiPlayer.move();
            currentState = board.stepGame(aiSeed, move[0], move[1]);
        }
    }

    private void showHint() {
        if (currentState == State.PLAYING && currentMode == GameMode.PLAYER_VS_AI) {
            int[] hintMove = aiPlayer.getHint(currentPlayer);
            board.cells[hintMove[0]][hintMove[1]].paintHint(this.getGraphics());
        }
    }


    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TITLE);
                frame.setContentPane(new Main());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}