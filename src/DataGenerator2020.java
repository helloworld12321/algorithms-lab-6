import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


public class DataGenerator2020 {
    /**
     * See Readme for an overview of the data generating process. The result is
     * being written to the file given as the first command line argument.
     *
     * @param args
     *
     *
     *
     *            Author: Peter Dolan (based off work by Elena Machkasova)
     */

    // Parameters

    private static Random rand = new Random();
    private static double probTranspose = 0.1; // Probability randomly selected word will have two letters transposed
    private static int wc = 901325; // Words in text file counted using wc in terminal
    private static String fn = new String("t8.shakespeare.txt"); // Complete works of Shakespeare from https://ocw.mit.edu/ans7870/6/6.006/s08/lecturenotes/files/t8.shakespeare.txt

    public static void main(String[] args) throws FileNotFoundException,IOException {
        makeSet("data1.txt",40000);
        makeSet("data2.txt",40000);
        makeSet("data3.txt",40000);
    }

    private static void makeSet(String outFileName, int n) throws FileNotFoundException,IOException {
        PrintWriter out = new PrintWriter(outFileName);
        generateData(n, out);
        if (out != null) {
                out.close();
        }
    }
    // generate data and print it to a file:

    private static String mutate(String str){ // Used some code from https://www.geeksforgeeks.org/swapping-characters-string-java/ September 2020
	int n=str.length();
	if(n==0){return str;} // Nothing to do if string is empty

	str = str.replaceAll("[^a-zA-Z]","").toLowerCase(); //Remove non-word characters then make everything lowercase
	n = str.length(); //Update the string length variable
	if(n==0){return str;} //It's possible we caught some lonely punctuation and produced an empty string
	if(rand.nextDouble()<probTranspose){	
		int a = rand.nextInt(n); // random num 0 to n-1
	        int b = rand.nextInt(n); // might be the same as a.... that's fine.  It's okay if we don't swap any characters occasionally
		char ch[] = str.toCharArray(); // Convert to character array... since all characters are ascii this is safe
		char temp = ch[a];
		ch[a] = ch[b];
		ch[b] = temp;
		str = new String(ch); 
	}
	return str;
    }

    private static void generateData(int numElts, PrintWriter out) {

    ArrayList<String> words = new ArrayList<String>(); //Create Arraylist of Strings
	double p = (double) numElts/ (double) wc; //The probability an individual word should be included

	String incoming;
	try { // Code adapted from https://stackoverflow.com/questions/4574041/read-next-word-in-java [read Sep 19 2020]
		Scanner s = new Scanner(new File(fn)); //Open the file
		int cnt = 0;
		for(int i = 0 ; i<wc; i++){
			if(!s.hasNext()){System.out.println("Ran out of words at index "+i);return;}
			if(rand.nextDouble()<=p){ // Keep this word
				incoming=mutate(s.next());
				if(incoming.length()>0) {
					out.println(incoming);
				}
			} else {
				s.next(); //Read and discard
			}
		}
	} catch (IOException e){
		System.out.println("Error accessing file "+fn);
	}
    }
}
