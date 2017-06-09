import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class Test {

	
	public static void main(String args[]){

        
        Simulator sim = new Simulator();
        
        try {
			sim.readGrid(new BufferedReader(new FileReader("C:\\Users\\MattUpstairs\\Documents\\icfp\\contest10.map")));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        //sim.printGrid(bw);

    	sim.printGrid();
        while(sim.complete==false){
        	char m=' ';
      	
        	
        	try {
				m = (char) System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	System.out.println("\nMove: "+m);
        	
        	
        	sim.move(m);
        	sim.printGrid();
        	
        	//System.out.println("finished moving");
        }
		
		
	}
}
