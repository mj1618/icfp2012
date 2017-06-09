import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Arrays;


public class Runner {

	
	
	public static void main(String args[]){

		String[] command = {"java", "-jar", "C:\\Users\\MattUpstairs\\Documents\\icfp\\robot.jar","2>err"};
        ProcessBuilder probuilder = new ProcessBuilder( command );
        probuilder.directory(new File("C:\\Users\\MattUpstairs\\Documents\\icfp\\"));
        
        //You can set up your work directory
        
        final Process process;
		try {
			process = probuilder.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				process.destroy();
				System.out.println("terminated");
			}
		});
		
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        
        File f = new File(args[0]);
        FileReader fr=null;
		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedReader grid = new BufferedReader(fr);
        String line;
        
        Simulator sim = new Simulator();
        
        sim.readGrid(grid);

        //sim.printGrid(bw);

    	sim.printGrid(bw);
    	try {
			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        while(sim.complete==false){
        	char m=' ';

        	//System.out.println("printing grid");
        	Process ps;
			/*try {
				//ps = Runtime.getRuntime().exec (new String[]{"cmd","cls"});

	        	// ps.waitFor();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} */
        	
        	
        	try {
				m = (char) br.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	System.out.println("\nMove: "+m);
        	
        	
        	sim.move(m);
        	sim.printGrid();
        	
        	if(args.length>1){
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	}
        	//System.out.println("finished moving");
        }
		
		
	}
	
	
}
