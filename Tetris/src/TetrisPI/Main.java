package TetrisPI;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		try {
			new Menu();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
