import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
public class Simulator {

	public char grid[][];
	public int nx=0;
	public int ny;
	public int nLambdas;
	public int score=0;
	public Point r;
	public int nLambdasRemaining=0;
	public int nMoves=0;
	
	public boolean complete = false;
	public boolean won = false;

	public HashMap<String,Point> lambdas;
	HashMap<String,Point> rocks;
	
	public Point lift;
	
	ArrayList<Move> moves;
	
	
	
	
	public boolean aborted=false;
	static boolean debug=true;
	
	
	
	final char L = 'L';
	final char R = 'R';
	final char U = 'U';
	final char D = 'D';
	final char W = 'W';
	final char A = 'A';
	final char EARTH='.';
	final char LAMBDA='\\';
	final char ROBOT = 'R';
	final char ROCK = '*';
	final char CLOSED='L';
	final char WALL='#';
	final char OPEN='O';
	final char EMPTY=' ';
	

	public Simulator(){
		rocks = new HashMap<String,Point>();
		moves = new ArrayList<Move>();
		lambdas = new HashMap<String,Point>();
	}
	
	

	
	public void readGrid(BufferedReader br){
		
		ArrayList<String> lines = new ArrayList<String>();
		
		while(true){
			String line=null;
			try {
				line = br.readLine();
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			if(line==null)break;
			int i;
			for( i = line.length()-1; i>0&&line.charAt(i)==' '; i--);
			line = line.substring(0, i+1);
			//if(debug)System.out.println("line: "+line);
			lines.add(new String(line));
			
			if(line.length()>nx)nx=line.length();
			
			
		}
		
		
		ny = lines.size();
		
		//System.out.println("nx: "+nx+"; ny:"+ny);
		
		grid = new char[nx][ny];
		
		//grid[5][5]='M';
		//System.out.println("m:"+grid[5][5]);
		
		for(int y=ny-1; y>=0; y--){
			String line = lines.get(ny-y-1);
			//System.out.println("line2: "+line);
			for(int x=0; x<nx; x++){
				if(x<line.length())grid[x][y] = line.charAt(x);
				else grid[x][y] = ' ';
				
				if(grid[x][y]==ROCK){
					Point p = new Point(x,y);
					rocks.put(p.toString(),p);
				}
				
				if(grid[x][y]==ROBOT){
					r=new Point(x,y);
				}
				
				if(grid[x][y]==LAMBDA){
					nLambdasRemaining++;
					lambdas.put(new Point(x,y).toString(),new Point(x,y));
				}
				
				if(grid[x][y]==CLOSED){
					lift = new Point(x,y);
				}
				
				
			}
		}
		
		
		//if(debug)			printGrid();
		
		
	}
	
	public void readGrid(char othergrid[][]){
		nx = othergrid.length;
		ny = othergrid[0].length;
		grid = new char[nx][ny];
		for(int y=ny-1; y>=0; y--){
			
			//System.out.println("line2: "+line);
			for(int x=0; x<nx; x++){
				
				grid[x][y]=othergrid[x][y];
				
				if(grid[x][y]==ROCK){
					Point p = new Point(x,y);
					rocks.put(p.toString(),p);
				}
				
				if(grid[x][y]==ROBOT){
					r=new Point(x,y);
				}
				
				if(grid[x][y]==LAMBDA){
					nLambdasRemaining++;
					lambdas.put(new Point(x,y).toString(),new Point(x,y));
				}
				
				if(grid[x][y]==CLOSED){
					lift = new Point(x,y);
				}
				
				
			}
		}
		
	}
	
	
	private ArrayList<Point> getSortedRocks() {
		
		ArrayList<Point> ra = new ArrayList<Point>(rocks.values());
		Collections.sort(ra, new Comparator(){
			public int compare(Object o1, Object o2) {
               return ((Point)o1).compareTo(o2);
            }
		});
		
		return ra;
	}

	public void printGrid(){
		for(int y=ny-1; y>=0; y--){
			for(int x=0; x<nx; x++){
					System.out.print(grid[x][y]);
				
			}
				System.out.println();
		}
	}
	public void printGrid(BufferedWriter bw) {
		for(int y=ny-1; y>=0; y--){
			for(int x=0; x<nx; x++){
				try {
					bw.write(grid[x][y]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			try {
				bw.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public Simulator(char gridother[][]){
		grid = new char[nx][ny];
		
		for(int x = 0; x<nx; x++)
			for(int y =0; y<ny; y++)
				grid[x][y]=gridother[x][y];
	}
	
	public char get(Point p){
		if(inBounds(p))return grid[p.x][p.y];
		else return 'x';
	}
	
	
	Move nextMove;
	public void newMove(){
		nextMove = new Move();
		nextMove.nLambdas=nLambdas;
		nextMove.nLambdasRemaining=this.nLambdasRemaining;
		nextMove.score=score;
		nextMove.r=(Point) this.r.clone();
		
		for(Point rock:getSortedRocks()){
			nextMove.rocks.put(rock.toString(),(Point) rock.clone());
		}
	}
	public boolean move(char m){
		nMoves++;
		
		newMove();
		
		//nextMove.rocks=(ArrayList<Point>) this.rocks.clone();
		
		
		
		score--;
		boolean valid = false;
		switch(m){
		case L:			valid =  moveLeft(); break;
		case R:			valid =  moveRight();break;
		case U:			valid =  moveUp();break;
		case D:			valid =  moveDown();break;
		case W:			valid =  true;break;
		case A:			valid =  abort();break;
		default:		valid =  false;break;
		}
		
		
		moves.add(nextMove);
		
		newMove();
		
		rejig();
		
		moves.add(nextMove);
		
		checkLost();
		return valid;
	}
	
	
	private void checkLost() {
		if(nMoves >=nx*ny ){
			complete=true;
			won=false;
		}
		
		if(get(r.up())==ROCK){
			complete=true;
			won=false;
		}
		
	}
	public char get(int x, int y){
		if(x<0||y<0||x>=nx||y>=ny)return 'x';
		return grid[x][y];
	}
	

	char newGrid[][];
	
	public void setNew(int x,int y, char newv){
		newGrid[x][y] = newv;
		
		nextMove.addChange(new Point(x,y), grid[x][y], newv);
		
	}
	
	private void rejig(){
		
		newGrid = new char[nx][ny];
		
		for(int x = 0; x<nx; x++)
			for(int y =0; y<ny; y++)
				newGrid[x][y]=grid[x][y];
		
		for(Point p:getSortedRocks()){
			int x = p.x;
			int y = p.y;
			
			if(y==0){
				
				continue;
			}
			
			//System.out.println("x: "+x+"; y:"+y+"; x,y:"+get(x,y)+"; x,y-1"+get(x,y-1));
			if(get(x,y)==ROCK&&get(x,y-1)==EMPTY){
				//System.out.println("match");
				setNew(x,y,EMPTY);
				setNew(x,y-1,ROCK);
				
				changeRock(x,y,x,y-1);
			}
			
			else if(get(x,y)==ROCK&&get(x,y-1)==ROCK&&get(x+1,y)==EMPTY&&get(x+1,y+1)==EMPTY){
				setNew(x,y,EMPTY);
				
				p.x=x+1;
				p.y=y-1;
				
				setNew(x+1,y-1,ROCK);
				changeRock(x,y,x+1,y-1);
			}
			
			else if(get(x,y)==ROCK&&get(x,y-1)==ROCK&&(get(x+1,y)!=EMPTY||get(x+1,y-1)!=EMPTY)&&get(x-1,y)==EMPTY&&get(x-1,y-1)==EMPTY){
				setNew(x,y,EMPTY);
				
				p.x=x-1;
				p.y=y-1;
				
				setNew(x-1,y-1,ROCK);
				changeRock(x,y,x-1,y-1);
			}
			else if(get(x,y)==ROCK&&get(x,y-1)==LAMBDA&&get(x+1,y)==EMPTY&&get(x+1,y-1)==EMPTY){
				setNew(x,y,EMPTY);
				p.x=x+1;
				p.y=y-1;
				setNew(x+1,y-1,ROCK);
				changeRock(x,y,x+1,y-1);
			}
			else if(get(x,y)==CLOSED&&nLambdasRemaining==0){
				setNew(x,y,OPEN);
			}
			
		}
		
		
		grid = newGrid;
		
		
		
	}
	
	private void changeRock(int x, int y, int i, int j) {
		rocks.remove(new Point(x,y).toString());
		rocks.put(new Point(i,j).toString(), new Point(i,j));
	}




	private boolean abort() {
		score += 25*nLambdas;
		complete=true;
		aborted=true;
		return true;
	}

	private boolean moveDown() {
		Point newr = r.down();
		
		char newv = get(newr);
		
		
		if(inBounds(newr)==false)return false;
		
		
		if(newv==EMPTY||newv==EARTH){
			set(r,EMPTY);
			r = newr;
			set(r,ROBOT);
			return true;
		}
		
		if(newv==LAMBDA){
			addLambda();
			set(r,EMPTY);
			r = newr;
			set(r,ROBOT);
			return true;
		}
		
		if(newv == OPEN){
			complete();
			return true;
		}
		
		return false;
	}

	private boolean moveUp() {
		
		Point newr = r.up();
		
		char newv = get(newr);
		
		
		if(inBounds(newr)==false)return false;
		
		
		if(newv==EMPTY||newv==EARTH){
			set(r,EMPTY);
			r = newr;
			set(r,ROBOT);
			return true;
		}
		
		if(newv==LAMBDA){
			addLambda();
			set(r,EMPTY);
			r = newr;
			set(r,ROBOT);
			return true;
		}
		
		if(newv == OPEN){
			complete();
			return true;
		}
		
		return false;
	}

	private boolean moveRight() {
		Point newr = r.right();
		
		char newv = get(newr);
		
		
		if(inBounds(newr)==false)return false;
		
		
		if(newv==EMPTY||newv==EARTH){
			set(r,EMPTY);
			r = newr;
			set(r,ROBOT);
			return true;
		}
		
		if(newv==ROCK){
			
			if(inBounds(newr.right())&&get(newr.right())==EMPTY){
				set(r,EMPTY);
				r = newr;
				set(r,ROBOT);
				set(r.right(),ROCK);
				return true;
			}
			else return false;
			
		}
		
		if(newv==LAMBDA){
			addLambda();
			set(r,EMPTY);
			r = newr;
			set(r,ROBOT);
			return true;
		}
		
		if(newv == OPEN){
			complete();
			return true;
		}
		
		return false;
	}

	public void set(Point a, char v){

		nextMove.addChange(a, grid[a.x][a.y], v);
		grid[a.x][a.y] = v;
	}

	public boolean inBounds(Point p){
		return p.x>=0 && p.x<nx && p.y>=0 && p.y<ny;
	}
	
	private boolean moveLeft() {
		
		Point newr = r.left();
		
		char newv = get(newr);
		
		
		if(inBounds(newr)==false)return false;
		
		
		if(newv==EMPTY||newv==EARTH){
			set(r,EMPTY);
			r = newr;
			set(r,ROBOT);
			return true;
		}
		
		if(newv==ROCK){
			
			if(inBounds(newr.left())&&get(newr.left())==EMPTY){
				set(r,EMPTY);
				r = newr;
				set(r,ROBOT);
				set(r.left(),ROCK);
				return true;
			}
			else return false;
			
		}
		
		if(newv==LAMBDA){
			addLambda();
			set(r,EMPTY);
			r = newr;
			set(r,ROBOT);
			return true;
		}
		
		if(newv == OPEN){
			complete();
			return true;
		}
		
		return false;
		
	}

	
	
	
	private void complete() {
		this.complete=true;
		won=true;
		score += nLambdas*50;
		
	}

	public int nRemaining(){
		return nLambdasRemaining;
	}
	private void addLambda() {
		nLambdas++;
		lambdas.remove(r.toString());
		nLambdasRemaining--;
		score+=25;
	}
	
	public Object clone(){
		Simulator s = new Simulator();
		
		
		s.readGrid(this.grid);
		
		s.complete=complete;
		
		s.nMoves=nMoves;
		s.score=this.score;
		
		s.won=this.won;
		
		return s;
	}

	public void reverse() {
		for(int i = 0 ; i<2 ; i++){
			nMoves--;
			Move m = moves.get(moves.size()-1);
			
			moves.remove(moves.size()-1);
			
			this.nLambdas=m.nLambdas;
			this.nLambdasRemaining=m.nLambdasRemaining;
			this.r=m.r;
			this.rocks=m.rocks;
			this.score=m.score;
			
			for(Change change:m.changes){
				grid[change.p.x][change.p.y] = change.oldv; 
			}
			
		}
		
	}


	HashMap<String,ArrayList<Point>> trocks = new HashMap<String, ArrayList<Point>>();
	int tupto=0;
	public char get(int x, int y, int time) {
		
		if(tupto<time){
		
			int t = time;
			for(t-=tupto;t>0;t--){
				move(W);
				trocks.put(""+(time-t), (ArrayList<Point>)getSortedRocks());
				
			}
			
			trocks.put(""+time, (ArrayList<Point>)getSortedRocks());
			
			tupto=time;
		}
		
		if(trocks.get(""+time).contains(new Point(x,y))){
			return ROCK;
		}else return get(x,y);
	}
}
