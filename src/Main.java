import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class Main {
	final char L = 'L';
	final char R = 'R';
	final char U = 'U';
	final char D = 'D';
	final char W = 'W';
	final char A = 'A';
	final char[] MOVES = {L,D,U,R};
	final char EARTH='.';
	final char LAMBDA='\\';
	final char ROBOT = 'R';
	final char ROCK = '*';
	final char CLOSED='L';
	final char WALL='#';
	final char OPEN='O';
	final char EMPTY=' ';
	public static int maxDepth=7;
	
	Simulator game;
	static boolean debug = false;
	
	
	HashMap<String,TrickyMove> lambsucc;
	
	class TrickyMove{
		char layout[][];
		Point lam;
		Point success;
		char succMoves[];
		Point actual;
		public TrickyMove(char l[][],Point lam, Point s, char []s2){
			layout=l;
			this.lam=lam;
			success=s;
			succMoves=s2;
			
		}
		
		public boolean equivelant(Point pos){
			Point start = pos.minus(lam);
			Point end = pos.add(new Point(layout.length-1,layout[0].length-1).minus(lam));
			if(game.inBounds(start)==false||game.inBounds(end)==false)return false;
			char layout2[][] = new char[layout.length][layout[0].length];
			
			
			for(int x = start.x, i=0; x<=end.x; i++,x++){
				for(int y = start.y, j=0; y<=end.y; j++,y++){
					if(layout[i][j]=='%')continue;
					if(layout[i][j]=='&'&&game.get(x,y)!=ROCK)continue;
					
					if(layout[i][j]==EMPTY&&game.get(x,y)==ROBOT)continue;
					if(layout[i][j]!=game.get(x,y))return false;
				}
			}
			return true;
		}

		public void setActual(Point lam2) {
			actual = lam2.add(success.minus(lam));
			
		}

		public String getSuccessString() {
			// TODO Auto-generated method stub
			return actual.toString();
		}
	}
	
	ArrayList<TrickyMove> trickys;
	public void tricky(){
		trickys = new ArrayList<TrickyMove>();
		{
			char [][]layout = new char[2][3];
			
			layout[0][2]='&';
			layout[1][2]='&';
			
			layout[0][0] = LAMBDA;
			layout[0][1] = ROCK;
			layout[1][0]='%';
			layout[1][1]=EMPTY;
			char succmoves[] = {R,D};
			trickys.add(new TrickyMove(
					layout,
					new Point(0,0),
					new Point(-1,1),
					succmoves				
					));
		}
		{
			char [][]layout = new char[2][3];

			layout[0][2]='&';
			layout[1][2]='&';
			layout[1][0] = LAMBDA;
			layout[1][1] = ROCK;
			layout[0][0]='%';
			layout[0][1]=EMPTY;
			char succmoves[] = {L,D};
			trickys.add(new TrickyMove(
					layout,
					new Point(0,0),
					new Point(2,1),
					succmoves				
					));
		}
		{
			char [][]layout = {{LAMBDA}};
			char succmoves[] = {};
			trickys.add(new TrickyMove(
					layout,
					new Point(0,0),
					new Point(0,0),
					succmoves				
					));
		}

		
	}
	
	
	public Main(){
		
		tricky();
		
		game = new Simulator();
		if(debug){
			try {
				game.readGrid(new BufferedReader(new FileReader("C:\\Users\\MattUpstairs\\Documents\\icfp\\contest10.map")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else game.readGrid(new BufferedReader(new InputStreamReader(System.in)));
		
		refreshSuccess();
		
		
		NULLPATH = new Path(null, L);

	}
	
	
	private void refreshSuccess() {
		

		lambsucc = new HashMap<String,TrickyMove>();
		
		if(game.nLambdasRemaining==0){
			
			char ms[] = {};
			TrickyMove tm = new TrickyMove(null,null,null,ms);
			
			lambsucc.put(game.lift.toString(), tm);
			return;
		}
		
		
		for(Point lam : game.lambdas.values()){
			for(TrickyMove tm:trickys){
				if(tm.equivelant(lam)){
					tm.setActual(lam);
					lambsucc.put(tm.getSuccessString(), tm);
				}
			}
		}
		
	}


	Simulator sim;
	
	
	
	public char getNextMove(){
		if(nextMoves.size()!=0){
			return nextMoves.poll();
		}
		
		refreshSuccess();
		sim = (Simulator)game.clone();
		
		return shortestPath();//max();
	}
	
	
	Path NULLPATH;
	
	class Path{
		public Point current;
		//public HashMap<String,Point> previous;
		
		Point from=null;
		char firstMove;
		
		int nMoves=1;
		
		public Path(Point c, char first){
			if(c==null)current = null;
			else current = (Point) c.clone();
			firstMove = first;
			//previous = new HashMap<String,Point>();
		}
		
		
		public Path(Point c, Path j){
			current = (Point) c.clone();
			//previous = new HashMap<String,Point>();
			from = j.current;
			firstMove = j.firstMove;
			nMoves=j.nMoves+1;
		}


		public boolean isValid() {
			
			if(current.x<0||current.x>sim.nx||current.y<0||current.y>sim.ny)return false;
			if(pointPath[current.x][current.y]==NULLPATH)return false;
			if(pointPath[current.x][current.y]!=null && pointPath[current.x][current.y].nMoves<=nMoves)return false;
			
			char gridxy = sim.get(current.x,current.y,nMoves);
			
			if(gridxy!=EMPTY 
					&& gridxy!=EARTH
					&& gridxy!=LAMBDA
					&& gridxy!=OPEN)return false;
			if(sim.get(current.x,current.y+1,nMoves)==ROCK)return false;
			
			return true;
		}


		public boolean isSuccess() {
			
			TrickyMove tm = lambsucc.get(current.toString());
			
			if(tm==null)return false;
			else return true;
			
		}
		
	}
	
	
	
	Path pointPath[][];
	
	public char shortestPath(){
		
		pointPath = new Path[sim.nx][sim.ny];
		ArrayList<Point> allEndings = new ArrayList<Point>();
		
		
		
		pointPath[sim.r.x][sim.r.y] = new Path(new Point(sim.r),L);
		allEndings.add(new Point(sim.r));
		
		boolean first = true;
		
		while(allEndings.size()!=0){
			ArrayList<Point> newAllEndings = new ArrayList<Point>();
			for(Point p : allEndings){
				
				Path path = pointPath[p.x][p.y];
				
				for(char move:MOVES){
					
					
					
					Path newPath;
					if(!first)newPath= new Path(path.current.move(move), path);
					else {
						newPath = new Path(path.current.move(move), move);
						
					}
					
					if(newPath.isValid()){
						
						if(newPath.isSuccess()){
							return getPath(newPath);
						}
						
						else{
							newAllEndings.add(new Point(newPath.current));
							pointPath[newPath.current.x][newPath.current.y] = newPath;
						}
					}
					else if(pointPath[newPath.current.x][newPath.current.y]==null)pointPath[newPath.current.x][newPath.current.y] = NULLPATH;
				}
				
			}
			first = false;
			allEndings = newAllEndings;
		}
		return A;
	}
	
	
	
	
	
	
	Queue<Character> nextMoves = new LinkedList<Character>();
	
	
	
	private char getPath(Path p) {

		TrickyMove tm = lambsucc.get(p.current.toString());
		
		ArrayList<Character> moves = new ArrayList<Character>();
		
		while(p.from!=null){
			Path p2 = pointPath[p.from.x][p.from.y];
			
			moves.add(Point.getDirection(p2.current,p.current));
			p=p2;
		}
		
		nextMoves.add(p.firstMove);
		for(int i = moves.size()-1; i>=0; i--){
			nextMoves.add(moves.get(i));
		}
		
		
		for(char m2:tm.succMoves){
			nextMoves.add(m2);
		}
		
		
		return nextMoves.poll();
	}


	HashMap<String, PathPoint> map;
	static BufferedWriter err;
	private static void errwrite(String string) {
		if(debug==false)return;
		if(err==null){
			FileWriter fstream = null;
			try {
				fstream = new FileWriter("out.txt");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			err = new BufferedWriter(fstream);
		}
		try {
			err.write(string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private void printGrid() {
		//if(debug)sim.printGrid();
	}
	/*public char max(){
		
		map = new HashMap<String,PathPoint>();
		
		map.put(sim.r.toString(), new PathPoint((Point)sim.r.clone(),0));
		
		char maxMove=' ';
		float maxv = Float.NEGATIVE_INFINITY;
		
		for(char m:MOVES){
			sim.move(m);
			float v = maxi(m,1);
			sim.reverse();
			printGrid();
			errwrite(m+":"+v+"\n");
			if(v>=maxv){
				maxv=v;
				maxMove = m;
			}
			printGrid();
		}
		
		return maxMove;
		
	}
	





	public float maxi(char m,int depth){
		if(depth==maxDepth){
			return (float)sim.score-sim.nMoves; //HEURISTIC
		}
		
		
		
		PathPoint pp = map.get(sim.r.toString());
		
		if(pp!=null){
			if(pp.nMoves < sim.nMoves){
				return -10;
			}
			else map.put(sim.r.toString(), new PathPoint((Point) sim.r.clone(), sim.nMoves));
		}
		else map.put(sim.r.toString(), new PathPoint((Point) sim.r.clone(), sim.nMoves));
	
		
		
		if(sim.complete){
			if(sim.won)return Float.POSITIVE_INFINITY;
			//else if(sim.aborted)return sim.score*sim.nMoves/(sim.nx*sim.ny);
			else return Float.NEGATIVE_INFINITY;
		}
		
		float max = Float.NEGATIVE_INFINITY;
		
		for(char next:MOVES){
			sim.move(next);
			float a = maxi(next,depth+1);
			for(int i = 0; i<depth; i++){
				//errwrite("\t");
			}
			//errwrite(m+":");
			//errwrite(a+"\n");
			sim.reverse();
			if(a>max)max=a;
		}
		
		
		
		return max;
	}
	*/
	
	public static void main(String args[]){
		

		Main robot = new Main();
		while(true){

			
			//errwrite("test");
				char c = robot.getNextMove();
			
				robot.game.move(c);
				
				System.out.write(c);
			
			
		}
		
	}
	
	
}
