package com.example.snakefx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Optional;

public class SnakeController {
    @FXML
    private Pane root;
    @FXML
    private Text scoreText;
    @FXML
    private Text classificaText;
    @FXML
    private Button restartButton;

    private int x = 50, y = 50, dx = 0, dy = 0, score = 0;
    private final int STEP = 10, WIDTH = 300, HEIGHT = 400;
    private boolean gameOver = false;
    private LinkedList<Rectangle> body;
    private Cibo cibo;
    private Timeline timeline;
    private Classifica classifica;
    private String giocatoreNome;
    private FileProperties fileProperties;

    @FXML
    public void initialize() {
        initGame();
        root.setOnKeyPressed(event -> handleKeyPressed(event.getCode()));
        // Assicurati che nessun altro nodo catturi il focus
        restartButton.setFocusTraversable(false);
        scoreText.setFocusTraversable(false);
        classificaText.setFocusTraversable(false);
        Platform.runLater(() -> root.requestFocus());

        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> gameLoop()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void initGame() {
        giocatoreNome = chiediNomeGiocatore();
        fileProperties = new FileProperties();
        classifica = new Classifica();
        body = new LinkedList<>();
        body.add(new Rectangle(x, y, STEP, STEP));
        cibo = new Cibo(WIDTH, HEIGHT);
        root.getChildren().add(cibo.getRectangle());

        for (Rectangle segment : body) {
            root.getChildren().add(segment);
        }
        restartButton.setOnAction(e -> restartGame());
    }
    private String chiediNomeGiocatore() {
        // Crea una finestra di input per chiedere il nome
        TextInputDialog dialog = new TextInputDialog("Anonimo");
        dialog.setTitle("Nome Giocatore");
        dialog.setHeaderText("Inserisci il tuo nome:");
        dialog.setContentText("Nome:");

        // Mostra il dialogo e aspetta l'input
        Optional<String> result = dialog.showAndWait();

        // Ritorna il valore inserito o un nome di default
        return result.orElse("Anonimo");
    }

    private void gameLoop() {
        if (!gameOver) {
            moveSnake();
            checkCollisionWithFood();
            checkCollisionWithWalls();
            checkCollisionWithSelf();
            updateGame();
        } else if (!restartButton.isVisible()) {
            restartButton.setVisible(true);
            Giocatore giocatore = new Giocatore(giocatoreNome, score);
            classifica.aggiungiGiocatore(giocatore);
            updateClassificaDisplay();
        }
    }

    private void moveSnake() {
        Rectangle newHead = new Rectangle(x + dx, y + dy, STEP, STEP);
        body.addFirst(newHead);
        if (body.size() > score + 1) {
            body.removeLast();
        }
        x = (int) newHead.getX();
        y = (int) newHead.getY();
    }

    private void checkCollisionWithFood() {
        if (x == cibo.getX() && y == cibo.getY()) {
            score++;
            scoreText.setText("Punteggio: " + score);
            cibo.reset(WIDTH, HEIGHT);
            playSound(fileProperties.getPath("audio") + "/ciboMangiato.mp3");
        }
    }


    private void checkCollisionWithWalls() {
        if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) {
            gameOver = true;
            playSound(fileProperties.getPath("audio") + "/gameOver.mp3");
        }
    }

    private void checkCollisionWithSelf() {
        for (int i = 1; i < body.size(); i++) {
            Rectangle segment = body.get(i);
            if (x == segment.getX() && y == segment.getY()) {
                gameOver = true;
                playSound(fileProperties.getPath("audio") + "/gameOver.mp3");
                break;
            }
        }
    }

    private void updateGame() {
        root.getChildren().removeIf(node -> node instanceof Rectangle && !((Rectangle) node).equals(cibo.getRectangle()));
        for (Rectangle segment : body) {
            root.getChildren().add(segment);
        }
        // Update score text
        scoreText.setText("Punteggio: " + score);
    }

    private void restartGame() {
        x = 50;
        y = 50;
        dx = 0;
        dy = 0;
        score = 0;
        gameOver = false;
        body.clear();
        body.add(new Rectangle(x, y, STEP, STEP));
        root.getChildren().clear();
        root.getChildren().addAll(scoreText, classificaText, restartButton, cibo.getRectangle());
        for (Rectangle segment : body) {
            root.getChildren().add(segment);
        }
        scoreText.setText("Punteggio: 0");
        restartButton.setVisible(false);
        classificaText.setVisible(false);
        timeline.play();
    }
    private void handleKeyPressed(KeyCode key) {
        if (gameOver) return;
        if (key == KeyCode.UP) {
            dx = 0;
            dy = -STEP;
        } else if (key == KeyCode.DOWN) {
            dx = 0;
            dy = STEP;
        } else if (key == KeyCode.LEFT) {
            dx = -STEP;
            dy = 0;
        } else if (key == KeyCode.RIGHT) {
            dx = STEP;
            dy = 0;
        }
    }

    private void updateClassificaDisplay() {
        classificaText.setVisible(true);
        classificaText.setText(classifica.getClassifica());
    }

    private void playSound(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            Player player = new Player(bis);
            new Thread(() -> {
                try {
                    player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
