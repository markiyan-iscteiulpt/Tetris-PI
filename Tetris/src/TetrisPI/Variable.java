package TetrisPI;

public class Variable {
	
	private int type = 0;
	private float prob =0;
	
	public Variable(int type, Float prob) {
		this.type = type;
		this.prob = prob;
	}
	
	public int getType(){
		return this.type;
	}
	
	public float getProb(){
		return this.prob;
	}

}
