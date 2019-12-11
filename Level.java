package compSciClub;

import java.util.ArrayList;
import java.util.Random;

public class Level {
	private ArrayList<String> first;

	public Level() {
		first = new ArrayList<>();
		first.add("00000000000000000000000000000000");
		first.add("00000000000000000000000000000000");
		first.add("00000000000000000000000000000000");
		first.add("00000000000000000000000000000000");
		first.add("00000000000000000000000000000000");
		first.add("00000000000000000000000000000000");
		first.add("00000000000000000000000000000000");
		first.add("00000000000000000000000000000000");
		first.add("00000000000000000000000000000000");			
		first.add(buildEnemyRow());
		first.add("11111111111111111111111111111111");
		first.add("11111111111111111111111111111111");
	}
	public String buildEnemyRow() {
		Random rn = new Random();
		String enemyRow = "";
		for(int i = 0; i < 7; i++) {
			enemyRow += "000";
			int determiner = rn.nextInt(10);
			if (determiner < 3) {      // 30% chance
    			enemyRow += "0";
			}
			else if (determiner < 6) { // 30% chance
				enemyRow += "1";
			}
			else if (determiner < 9) { // 30% chance
				enemyRow += "2";
			}
			else {                     // 10% chance
				enemyRow += "3";
			}
			enemyRow += "0000";
		}
		return enemyRow;
	}
	public ArrayList<String> getFirst() {
		return first;
	}
}