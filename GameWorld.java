package compSciClub;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class GameWorld extends Pane {
	private static int WORLD_COUNT = 0;
	private static int DEFAULT_PLAYER_VELOCITY = 1;
	private static int SCORE = 0;
	private static boolean HAS_EXTRA_LIFE = true;
	
	Level level = new Level();
	private Node player;
	private boolean touching = false;
	private Circle touchedEnemy;

	private boolean canPlaceLabel = true;
	private boolean canJump = true; // -- player should be able to jump from the beginning
	private boolean canExpand = true;
	private Point2D playerVelocity = new Point2D(0, 0); // represents magnitude of velocity of the player.

	// -- we want to store our platforms and enemies for further use.
	private List<Node> platforms = new ArrayList<>();
	private List<Circle> enemies = new ArrayList<>();
	private List<ImageView> hearts = new ArrayList<>();
	private int levelWidth;

	Pane uiRoot = new Pane();
	Pane gameRoot = new Pane();

	/**
	 * stores the keys (i.e. W, A, S, and D) and their state (true or false) true
	 * means it's pressed, false means it's not.
	 */
	Map<KeyCode, Boolean> keys = new HashMap<>();;

	/**
	 * Initializes a new World filled with platforms and enemies.
	 */
	private void updateWorld() {
		WORLD_COUNT++;
		if (WORLD_COUNT % 2 ==0) {
		    DEFAULT_PLAYER_VELOCITY++;
		}
		Random rn = new Random();

		// -- create background and set it to be a light blue color
		Rectangle bg = new Rectangle(1280, 720);
		bg.fillProperty().set(Color.rgb(rn.nextInt(255), rn.nextInt(255), rn.nextInt(255)));

		// -- width of the level is determined by the array in Levels.java
		
		levelWidth = level.getFirst().get(0).length() * 60;
		
		// -- we iterate over the strings, first from the row and then for each column
		// in the row.
		Color platformColor = Color.rgb(rn.nextInt(255), rn.nextInt(255), rn.nextInt(255));
    	Color enemyColor = Color.rgb(rn.nextInt(255), rn.nextInt(255), rn.nextInt(255));

		for (int i = 0; i < level.getFirst().size(); i++) {
			String line = level.getFirst().get(i);
			for (int j = 0; j < line.length(); j++) {
				switch (line.charAt(j)) {
				case '0':
					// -- we just ignore since this represents our background
					break;
				case '1':
					// -- the floor for our game world
					Node platform = createRectangle(j * 60, i * 60, 60, 60, platformColor);
					platforms.add(platform);
					break;
				case '2':
					// -- we create blue circles as our enemies.
					Circle enemy = new Circle(j * 60, i * 60, 30);
					enemy.fillProperty().set(enemyColor);
					gameRoot.getChildren().add(enemy);
					enemies.add(enemy);
					break;
				case '3':
					// -- extra lives
					//Ellipse heart = new Ellipse(j * 60.0, i * 60.0, 20.0, 30.0);
					//heart.fillProperty().set(Color.RED);
					ImageView heart = new ImageView();
					
					heart.setImage(new Image("file:heart.png", 50, 50, true, false));
					heart.setTranslateX(j * 60);
					heart.setTranslateY(i * 60);
					gameRoot.getChildren().add(heart);
					
                    hearts.add(heart);
					break;
				}
			}
		}

		player = createRectangle(0, 300, 40, 40, Color.rgb(rn.nextInt(255), rn.nextInt(255), rn.nextInt(255)));

		// -- handles scrolling.
		player.translateXProperty().addListener((obs, old, newValue) -> {
			int offset = newValue.intValue(); // -- player's location
			if (offset > 640 && offset < (levelWidth - 640)) {
				// -- distance between player and left side of level
				// -- this pushes gameRoot to left and player moves to the right
				gameRoot.layoutXProperty().set(-(offset - 640));
			}
		});
		this.getChildren().addAll(bg, gameRoot, uiRoot);

	}

	/**
	 * Handles the creation of rectangle actors (i.e. players, platforms, etc.)
	 * 
	 * @param x horizontal position of the center of the rectangle in pixels
	 * @param y vertical position of the center of the rectangle in pixels
	 * @param w width of the rectangle
	 * @param h height of the rectangle
	 * @param c color of rectangle
	 * @return Node
	 */
	private Node createRectangle(int x, int y, int w, int h, Color c) {
		Rectangle entity = new Rectangle(w, h);
		entity.translateXProperty().set(x);
		entity.translateYProperty().set(y);
		entity.fillProperty().set(c);
		gameRoot.getChildren().add(entity);
		return entity;
	}

	public GameWorld() {
		updateWorld();
	}
	
	private void moveRight(int value) {
		boolean movingRight = value > 0;
		for (int i = 0; i < Math.abs(value); i++) {
			for (Node platform : platforms) {
				if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
					if (movingRight) {
						if (player.translateXProperty().get() + 40 == platform.translateXProperty().get()) {
							return;
						}
						else {
							if (player.translateXProperty().get() == platform.translateXProperty().get() + 60) {
								return;
							}
						}
					}
				}
			}
			// -- if we are not touching a platform, move right
			player.translateXProperty().set(player.translateXProperty().get() + (movingRight ? 1 : -1));
		}
	}

	private void moveY(int value) {
		boolean movingDown = value > 0;
		for (int i = 0; i < Math.abs(value); i++) {
			for (Node platform : platforms) {
				if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
					if (movingDown) {
						// -- if we land on top of the platform
						if (player.translateYProperty().get() + 40 == platform.translateYProperty().get()) {
							canJump = true;
							player.translateYProperty().set(player.translateYProperty().get() - 1);
							return;
						}

					}
					else {
						// if we hit the bottom of a platform
						if (player.translateYProperty().get() == platform.translateYProperty().get() + 60) 
						{
							return;
						}
					}
				}
			}
			// -- if we are not touching a platform, move down.
			player.translateYProperty().set(player.translateYProperty().get() + (movingDown ? 1 : -1));
		}
	}
	
	// Makes sure we can't jump twice
	private void jump() {
		if (canJump) {
			playerVelocity = playerVelocity.add(0, -30);
			canJump = false;
		}
	}
	
	// Handles ending the game
	private void enemyIntersectsPlayer(Stage st) {
		if (!touching) {
			for (Node enemy : enemies) {
	    		if(player.getBoundsInParent().intersects(enemy.getBoundsInParent()) && canPlaceLabel) {
	    			touching = true;
	    			touchedEnemy = (Circle)enemy;
	    			//	player.setTranslateX(player.getTranslateX() + 120);
	    			if(HAS_EXTRA_LIFE) {
	    				HAS_EXTRA_LIFE = false;
	    			}
	    			else {
	        			Label gameOver = new Label("Game over");
	        			gameOver.fontProperty().set(new Font("Helvetica", 50));
		        		gameOver.setTranslateX(player.getTranslateX());
		    	    	gameOver.setTranslateY(300);
	    	            gameOver.translateXProperty().set(player.translateXProperty().get());
		    			gameOver.translateYProperty().set(300);
				   	    
			    		SCORE += player.getTranslateX();
			    		DEFAULT_PLAYER_VELOCITY = 0;
			    		this.gameRoot.getChildren().remove(player);
			    		this.gameRoot.getChildren().add(gameOver);
	    			    this.gameRoot.getChildren().add(getWorldCount(gameOver));
	        			this.gameRoot.getChildren().add(getScore(gameOver));
	        			
	        			Button restart = new Button("Restart");
	        			restart.setOnAction(event ->{
	        				WORLD_COUNT = 0;
	        				DEFAULT_PLAYER_VELOCITY = 1;
	        				HAS_EXTRA_LIFE = true;
	        				st.setScene(Main.createContent(st));
	        				});
	        			restart.setTranslateX(player.getTranslateX());
	        			restart.setTranslateY(player.getTranslateY() - 90);
	        			this.gameRoot.getChildren().add(restart);
	    	    		
	    	    		canPlaceLabel = false;
	    	    		Main.getTimer().stop();
	    		    }
			    }
			}
		}
		else {
	    	if(!(player.getBoundsInParent().intersects(touchedEnemy.getBoundsInParent()) && canPlaceLabel)) {
	    		touching = false;
	    		touchedEnemy = null;
	    	}
		}
	}
	
	//handles getting and extra life
	private void playerGetsHeart() {
		for (ImageView heart : hearts) {
    		if(player.getBoundsInParent().intersects(heart.getBoundsInParent())) {
    			HAS_EXTRA_LIFE = true;
		    }
		}
	}
	
	// Displays which world you got to once you died
	private Label getWorldCount(Label gameOver) {
		Label worldCount = new Label("World Count: " + GameWorld.WORLD_COUNT);
		worldCount.fontProperty().set(new Font("Rockwell", 25));
		worldCount.translateXProperty().set(player.translateXProperty().get());
		worldCount.translateYProperty().set(gameOver.getTranslateY() + 50);

		return worldCount;
	}
		
	private Label getScore(Label gameOver) {
		Label score = new Label("Score: " + GameWorld.SCORE);
		score.fontProperty().set(new Font("Rockwell", 20));
		score.translateXProperty().set(player.translateXProperty().get());
		score.translateYProperty().set(gameOver.getTranslateY() + 90);

		return score;
	}
	
	private void atWorldEdge(Stage st, Scene scene) {
		if(canExpand) {
			st.setTitle("New Level");
			st.setScene(Main.createContent(st));
			canExpand = false;
			SCORE += player.getTranslateX();
		}
	}
	
	/**
	 * Consistently checks to check and take input from the user
	 */
	public void update(Stage st, Scene sc) {
        moveRight(GameWorld.DEFAULT_PLAYER_VELOCITY);
		if (isPressed(KeyCode.W) && player.getTranslateY() >= 5) {
			jump();
		}
		if (isPressed(KeyCode.D) && player.getTranslateX() + 40 <= levelWidth - 5) {
			moveRight(GameWorld.DEFAULT_PLAYER_VELOCITY + 4);
		}
		if (playerVelocity.getY() < 10) {
			playerVelocity = playerVelocity.add(0, 1);
		}
		enemyIntersectsPlayer(st);
		playerGetsHeart();
		if(player.getTranslateX() >= levelWidth - 2) {
		    atWorldEdge(st, sc);
		}
		moveY((int) playerVelocity.getY());

	}
	
	/**
	 * Allows for us to change our 
	 */
	private boolean isPressed(KeyCode key) {
		return keys.getOrDefault(key, false);
	}

	public Map<KeyCode, Boolean> getKeys() {
		return keys;
	}
}