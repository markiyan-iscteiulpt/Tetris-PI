package TetrisPI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Tetris extends JFrame {
	
	private static final long serialVersionUID = -4722429764792514382L;
	private static final long FRAME_TIME = 1000L / 50L;
	private static final int TYPE_COUNT = TileType.values().length;
	private BoardPanel board;
	private SidePanel side;
	private boolean isPaused;
	private boolean isNewGame;
	private boolean isGameOver;
	private int level;
	private int score;
	private  Random random;
	private Clock logicTimer;
	private TileType currentType;
	private TileType nextType;
	private int currentCol;
	private int currentRow;
	private int currentRotation;
	private int dropCooldown;
	private float gameSpeed;
	private Sound sound;
	private Time time;
	private Menu menu;
	
	
	public boolean stop = true;
	
	public Tetris(Menu m) {
		super("Tetris");
		
		this.menu = m;
		
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
				
		this.board = new BoardPanel(this);
		this.side = new SidePanel(this);
		
		add(board, BorderLayout.CENTER);
		add(side, BorderLayout.EAST);
		pack();
		sound = new Sound();
		
		keyListener();
		
		
		setLocationRelativeTo(null);
		
		setVisible(true);
	}
	
	
	private void keyListener(){

		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				
				case 39:
					sound.nextSound();
					break;
								
				case 38:
					sound.stop();
					break;
					
				case 40:
					sound.play(true);
					break;
				
				case KeyEvent.VK_S:
					if(!isPaused && dropCooldown == 0) {
						logicTimer.setCyclesPerSecond(25.0f);
					}
					break;
					
				case KeyEvent.VK_A:
					if(!isPaused && board.isValidAndEmpty(currentType, currentCol - 1, currentRow, currentRotation)) {
						currentCol--;
					}
					break;
					
				case KeyEvent.VK_D:
					if(!isPaused && board.isValidAndEmpty(currentType, currentCol + 1, currentRow, currentRotation)) {
						currentCol++;
					}
					break;
					
				case KeyEvent.VK_Q:
					if(!isPaused) {
						rotatePiece((currentRotation == 0) ? 3 : currentRotation - 1);
					}
					break;
				
				case KeyEvent.VK_E:
					if(!isPaused) {
						rotatePiece((currentRotation == 3) ? 0 : currentRotation + 1);
					}
					break;
					
				case KeyEvent.VK_P:
					if(!isGameOver && !isNewGame) {
						isPaused = !isPaused;
						logicTimer.setPaused(isPaused);
						time.pause();
						
					}
					if(!isPaused) {
						time.resume();
					}
					if(sound.isPlaying()){
						sound.stop();
					}else{
						sound.play();
					}
					menu.setVisible(true);
					setVisible(false);
					break;

				case KeyEvent.VK_ENTER:
					if(isGameOver || isNewGame) {
						resetGame();
					}
					break;
				
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				switch(e.getKeyCode()) {
		
				case KeyEvent.VK_S:
					logicTimer.setCyclesPerSecond(gameSpeed);
					logicTimer.reset();
					break;
				}
				
			}
			
		});
	}
	

	public void startGame() {
		time = new Time();
		time.pause();
		
		this.random = new Random();
		this.isNewGame = true;
		this.gameSpeed = 1.0f;
		
		this.logicTimer = new Clock(gameSpeed);
		logicTimer.setPaused(true);
		
		resetGame();
		
		while(true) {
			long start = System.nanoTime();
			
			logicTimer.update();
			
			if(logicTimer.hasElapsedCycle()) {
				updateGame();
			}
		
			if(dropCooldown > 0) {
				dropCooldown--;
			}
			
			renderGame();
			
			long delta = (System.nanoTime() - start) / 1000000L;
			if(delta < FRAME_TIME) {
				try {
					Thread.sleep(FRAME_TIME - delta);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	
	public void enter(){
		if(isGameOver || isNewGame) {
			resetGame();
		}
		System.out.println("Ok");
	}
	
	
	public void updateGame() {
		if(!sound.isPlaying() && this.isPaused){
			sound.verify();
		}
		
		
		if(board.isValidAndEmpty(currentType, currentCol, currentRow + 1, currentRotation)) {
			currentRow++;
		} else {
			board.addPiece(currentType, currentCol, currentRow, currentRotation);
			
			checkClearedLines();
			
			increaseSpeed();
			
			dropCooldown = 25;
			
			diffLevel();
			
			spawnPiece();
		}		
	}
	
	
	public void renderGame() {
		board.repaint();
		side.repaint();
	}
	
	
	public void resetGame() {
		if(isGameOver){
			this.time = new Time();
		}else{
		time.start();
		}
		this.level = 0;
		this.score = 0;
		this.gameSpeed = 1.0f;
		this.nextType = generateRandomPiece();
		this.isNewGame = false;
		this.isGameOver = false;		
		board.clear();
		logicTimer.reset();
		logicTimer.setCyclesPerSecond(gameSpeed);
		spawnPiece();
	}
		
	
	private void spawnPiece() {
		
		this.currentType = nextType;
		randomPosition();
		this.currentRotation = 0;
		this.nextType = generateRandomPiece();
		randomRotation();
		if(!board.isValidAndEmpty(currentType, currentCol, currentRow, currentRotation)) {
			this.isGameOver = true;
			time.pause();
			logicTimer.setPaused(true);
		}		
	}


	private void rotatePiece(int newRotation) {
		
		int newColumn = currentCol;
		int newRow = currentRow;
		
		int left = currentType.getLeftInset(newRotation);
		int right = currentType.getRightInset(newRotation);
		int top = currentType.getTopInset(newRotation);
		int bottom = currentType.getBottomInset(newRotation);
		
		
		if(currentCol < -left) {
			newColumn -= currentCol - left;
		} else if(currentCol + currentType.getDimension() - right >= BoardPanel.COL_COUNT) {
			newColumn -= (currentCol + currentType.getDimension() - right) - BoardPanel.COL_COUNT + 1;
		}
		
		if(currentRow < -top) {
			newRow -= currentRow - top;
		} else if(currentRow + currentType.getDimension() - bottom >= BoardPanel.ROW_COUNT) {
			newRow -= (currentRow + currentType.getDimension() - bottom) - BoardPanel.ROW_COUNT + 1;
		}
		
		if(board.isValidAndEmpty(currentType, newColumn, newRow, newRotation)) {
			currentRotation = newRotation;
			currentRow = newRow;
			currentCol = newColumn;
		}
	}
	
	
	private void checkClearedLines(){
		int cleared = board.checkLines();
//		float varx = (float) (1.2f * Math.sqrt((-2)*Math.log(rand1))*Math.cos((2*Math.PI)*rand2) + (5/5f));
		
		if(cleared > 2){
			bonustime();
		}
		
		
		if(cleared > 0) {
			score += cleared * 100;
		}
	}
	
	/*
	 * Funcao Weibull
	 * Variavel continua
	 */
	private void bonustime(){
	/*	double a = random.nextDouble();
		double b = 1/3.4f;
		double c = -Math.log(a);
		double d = 1/2f;
		double e = Math.pow(c, d);
		double f = e * b;*/
//		System.out.println(f*100);
	}
	
	
	/*
	 * Funcao Exponencial
	 * Variavel continua
	 */
	private void increaseSpeed(){
		double a = random.nextDouble();
		double log = Math.log(a);
		float mxx =   1/25f;
		double x = -mxx * log;
		float fl = (float) x;
		this.gameSpeed+=fl;
		logicTimer.setCyclesPerSecond(gameSpeed);
		logicTimer.reset();
//		System.out.println(logicTimer.getMillisPerCycle());
	}
	
	
	private void diffLevel(){
		String[] array = new String(Integer.toString(score)).split("");
		String nivel;
		if(score<100){
			nivel = "0";
		}else if(score<=1000){
			nivel = array[0];
		}else{
			nivel = array[0];
			nivel +=array[1];
		}
		level = Integer.parseInt(nivel);
	}
	
	private void randomRotation(){
		int rotation = randInt(0, 3);
		rotatePiece(rotation);
	}
	
	
	/*
	 *Distribuicao Uniforme
	 *Variavel discreta
	 *		
	 *		0  -  1/7  -  14.28% 
	 *		1  -  1/7  -  14.28%
	 *		2  -  1/7  -  14.28%
	 *		3  -  1/7  -  14.28%
	 *		4  -  1/7  -  14.28%
	 *		5  -  1/7  -  14.28%
	 *		6  -  1/7  -  14.28%
	 *
	 */
	private TileType generateRandomPiece(){
		ArrayList<Variable> figuras = new ArrayList<Variable>();
		
			if(level < 5){
				for(int k = 0; k < TYPE_COUNT ; k++){
					float summ = 0;
					int comb = combination(TYPE_COUNT, k);
					float pexp = (float) Math.pow(1/7f, k);
					float va = (float) Math.pow((1-(1/7f)), (TYPE_COUNT-k));
					float res = pexp * va * comb;
					figuras.add(new Variable(k, (float) res +summ));
				}
			}else{
				float summ = 0;
				for(int i = 0; i < TYPE_COUNT; i++){
					figuras.add(new Variable(i, summ+=1/7f));
				}
			}
				
				
				figuras.sort(new Comparator<Variable>() {
					@Override
					public int compare(Variable arg0, Variable arg1) {
						int result = Float.compare(arg0.getProb(), arg1.getProb());
						if (result == 0)
						  result = Float.compare(arg0.getProb(), arg1.getProb());
						return result;
					}
				});
				
				
			float summ =0 ;
			for(Variable v:figuras){
				System.out.println("Tipo: " + v.getType() + " , Prob.: " + v.getProb());
				summ+=v.getProb();
			}
			System.out.println("SOMA: " + summ);
			
			int piece = 0;
			float a = randFloat(0, figuras.get(TYPE_COUNT-1).getProb());
				if(a > 0 && a <= figuras.get(0).getProb()) {
					piece = figuras.get(0).getType();
				}else if(a > figuras.get(0).getProb() && a <= figuras.get(1).getProb()){
					piece = figuras.get(1).getType();
				}else if(a > figuras.get(1).getProb() && a <= figuras.get(2).getProb()){
					piece = figuras.get(2).getType();
				}else if(a > figuras.get(2).getProb() && a <= figuras.get(3).getProb()){
					piece = figuras.get(3).getType();
				}else if(a > figuras.get(3).getProb() && a <= figuras.get(4).getProb()){
					piece = figuras.get(4).getType();
				}else if(a > figuras.get(4).getProb() && a <= figuras.get(5).getProb()){
					piece = figuras.get(5).getType();
				}else if(a > figuras.get(5).getProb() && a <= figuras.get(6).getProb()){
					piece = figuras.get(6).getType();
				}
//			System.out.println("Random: " + a + " Peca: " + piece);
			
			
		return TileType.values()[piece];
	}
	
	
	
	
	
	
	public int combination(int n, int k){
	    return factorial(n) / (factorial(k)  * factorial(n - k));
	}


	
	public static int factorial(int n){
        int ret = 1;
        for (int i = 1; i <= n; ++i) ret *= i;
        return ret;
    }
	
	
	private void randomPosition(){
		int randomPos = randInt(0, 7);
		this.currentCol = randomPos;
		this.currentRow = currentType.getSpawnRow();
	}
	
	
	public int randInt(int min, int max) {
	    int randomNum = this.random.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public float randFloat(float min, float max) {
	    float randomNum = this.random.nextFloat() * (max - min) + min;
	    return randomNum;
	}
	
	
	public boolean isPaused() {
		return isPaused;
	}
	
	
	public boolean isGameOver() {
		return isGameOver;
	}
	

	public boolean isNewGame() {
		return isNewGame;
	}
	

	public int getScore() {
		return score;
	}
	

	public int getLevel() {
		return level;
	}
	

	public TileType getPieceType() {
		return currentType;
	}
	
	
	public TileType getNextPieceType() {
		return nextType;
	}
	
	
	public int getPieceCol() {
		return currentCol;
	}
	
	
	public int getPieceRow() {
		return currentRow;
	}
	
	
	public int getPieceRotation() {
		return currentRotation;
	}

	public Time getTime() {
		return time;
	}

	public JPanel getBoardPanel() {
		return this.board;
	}



	public void startMenu() {
		System.out.println("kkek");
		JButton b = new JButton("BUTTON");
		JPanel pan = new JPanel();
		pan.setBackground(Color.GRAY);
		pan.add(b);
		this.getBoardPanel().add(pan);
		this.repaint();
	}
}
