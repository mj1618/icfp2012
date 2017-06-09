
public class Point {

	final static char L = 'L';
	final static char R = 'R';
	final static char U = 'U';
	final static char D = 'D';
	final static char W = 'W';
	final static char A = 'A';
	public Point(int x2, int y2) {
		x=x2;
		y=y2;
	}

	
	public Point(Point r) {
		this.x=r.x;
		this.y=r.y;
		
	}


	public Point left(){
		return new Point(x-1,y);
	}
	
	public Point right(){
		return new Point(x+1, y);
	}
	
	public Point up(){
		return new Point(x,y+1);
	}
	
	public Point down(){
		return new Point(x,y-1);
	}
	
	public int x,y;

	public int compareTo(Object o2) {
		Point p = (Point)o2;
		if( this.y<p.y || (this.y==p.y&&this.x<p.x) )
			return 1;
		else return -1;
	}
	
	public Object clone(){
		return new Point(x,y);
	}
	
	public String toString(){
		return "("+x+","+y+")";
	}


	public void set(int i, int j) {
		x=i;
		y=j;
	}


	public Point move(char move) {
		switch(move){
		case 'L':return left();
		case 'R':return right();
		case 'U': return up();
		case 'D':return down();
		default:return null;
		}
	}

	public boolean equals(Object o){
		Point p = (Point)o;
		return p.x==x&&p.y==y;
	}
	
	public static Character getDirection(Point from, Point to) {
		if(to.x-from.x==1)return R;
		if(to.y-from.y==1)return U;
		if(to.x-from.x==-1)return L;
		if(to.y-from.y==-1)return D;
		return null;
	}


	public Point minus(Point lam) {
		
		return new Point(x-lam.x,y-lam.y);
	}


	public Point add(Point a) {
		// TODO Auto-generated method stub
		return new Point(x+a.x,y+a.y);
	}
}
