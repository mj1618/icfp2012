
public class PathPoint {

	public PathPoint(Point r, int i) {
		p=(Point) r.clone();
		nMoves=i;
	}
	Point p;
	int nMoves;
	
}
