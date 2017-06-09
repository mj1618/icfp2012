import java.util.ArrayList;
import java.util.HashMap;


public class Move {

	int score;
	
	int nLambdas;
	int nLambdasRemaining;
	Point r;
	HashMap<String, Point> rocks;
	
	
	ArrayList<Change> changes;
	
	public Move(){
		changes = new ArrayList<Change>();
		rocks = new HashMap<String, Point>();
	}
	
	public void addChange(Point p, char oldv, char newv){
		changes.add(new Change(p,oldv,newv));
	}
	
	public ArrayList<Change> getChanges(){
		return changes;
	}
	
}
