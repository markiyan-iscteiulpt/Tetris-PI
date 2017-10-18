package TetrisPI;

public class FrameRunnable implements Runnable{
	
	private Menu menu;
	private Tetris tetris;
	
	public FrameRunnable(Menu m) {
		this.menu = m;
	}
	
    public void run(){
       this.tetris = new Tetris(menu);
       tetris.startGame();
    }
    
    public Tetris getTetris(){
    	return this.tetris;
    }
}
