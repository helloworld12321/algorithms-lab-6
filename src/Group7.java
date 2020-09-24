import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.List;
import java.math.BigInteger;

public class Group7 {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {
        // testing the comparator:
		Data.test_Data(); // This MUST be commented out for your submission to the competition!

        if (args.length < 2) {
            System.out.println("Please run with two command line arguments: input and output file names");
            System.exit(0);
        }

        String inputFileName = args[0];
        String outFileName = args[1];

        // read as strings
        String [] data = readData(inputFileName);
        String [] toSort = data.clone();
        Data [] sorted = sort(toSort); // Warm up the VM
        toSort = data.clone();
        Thread.sleep(10); //to let other things finish before timing; adds stability of runs

        long start = System.currentTimeMillis(); // Begin the timing
        sorted = sort(toSort);
        long end = System.currentTimeMillis();   // End the timing

        System.out.println(end - start);     // Report the results
        writeOutResult(sorted, outFileName);
    }

    // YOUR SORTING METHOD GOES HERE.
    // You may call other methods and use other classes.
    // You may ALSO modify the methods, innner classes, etc, of Data[]
    // But not in way that transfers information from the warmup sort to the timed sort.
    // Note: you may change the return type of the method.
    // You would need to provide your own function that prints your sorted array to
    // a file in the exact same format that my program outputs
    private static Data[] sort(String[] toSort) {
        // The comparator remembers primes (locally to the sort method.)
        ArrayList<Long> primes = new ArrayList<>();
        // Start the array off with a few values computed ahead of time.
        primes.add(2L);
        primes.add(3L);
        primes.add(5L);
        primes.add(7L);
        primes.add(11L);
        primes.add(13L);
        primes.add(17L);
        primes.add(19L);

        Data[] toSortData = new Data[toSort.length];
        for (int i = 0; i < toSort.length; ++i) {
            toSortData[i] = new Data(toSort[i]);
        }
        Arrays.sort(toSortData, new GematriaComparator(primes));
        return toSortData;
    }

    private static void printArray(String[] Arr, int n) {
        for(int i = 0; i < n; i++) {
            System.out.println(Arr[i]);
        }
    }

    private static String[] readData(String inFile) throws FileNotFoundException,IOException {
        List<String> input = Files.readAllLines(Paths.get(inFile));
        // the string array is passed just so that the correct type can be created
        return input.toArray(new String[0]);
    }

    private static void writeOutResult(Data[] sorted, String outputFilename) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFilename);
        for (Data s : sorted) {
            out.println(s.word);
        }
        out.close();
    }

    private static class GematriaComparator implements Comparator<Data> {

        ArrayList<Long> knownPrimes;

        public GematriaComparator(ArrayList<Long> knownPrimes) {
            this.knownPrimes = knownPrimes;
        }

        public long toVal(char ch){ // This function is an ancient evil that has no place in a unicode-based world :(
            return (int) ch - (int) 'a' + 1;    //type-casting a ch to (int) turns it into an ascii value
        } //Warning:  this will work with non-lower-case ascii characters too.

        public long gematrify(String str){
            char[] ch=str.toCharArray();
            long gematria = 0;
            long multiplier=1;
            for(int i = str.length()-1; i>=0; i--){ //Work from the right to the left
                gematria += toVal(ch[i])*multiplier;
                multiplier = 2*multiplier;
            }
            return gematria;
        }

        long findNextPrime(){
            lookingForAPrime:
            for (long m = this.knownPrimes.get(this.knownPrimes.size() - 1); true; m += 2) {
                for (long prime : knownPrimes) {
                    if (m % prime == 0) {
                        continue lookingForAPrime;
                    }
                }
                // If we didn't find any divisors
                this.knownPrimes.add(m);
                return m;
            }
        }

        /**
         * Return the next prime given a index from knownPrimes
         * If no such prime exists in knownPrimes, finds a new one
         */
        long nextPrime(int index) {
            if (index + 1 < this.knownPrimes.size()) {
                return this.knownPrimes.get(index + 1);
            } else {
                return findNextPrime();
            }
        }

        @Override
        public int compare(Data s1, Data s2) {
            if (s1.word.equals(s2.word)) {
                return 0;
            }

            long g1 = gematrify(s1.word);
            long g2 = gematrify(s2.word);

            // in case of tie, compare lexicographically
            if (g1 == g2) {
                return s1.word.compareTo(s2.word);
            }

            int primeIndex = 0;
            long prime = this.knownPrimes.get(primeIndex);
            boolean done = false;
            boolean d1 = false;
            boolean d2 = false;
            while (!done) { //Since g1 != g2 as we strip divisors we either find a smaller one, or one of the two reaches 1 first
                if (g1 == 1) {
                    return -1;
                }
                if (g2==1) {
                    return 1;
                }
                // We know that g1 AND g2 have unprocessed divisors
                d1 = (g1 % prime == 0); //d1 is a BOOLEAN value
                d2 = (g2 % prime == 0); //d2 is a BOOLEAN VALUE
                if (!d1 && !d2) {
                    // Neither are divisible, move to next prime.
                    prime = nextPrime(primeIndex);
                    primeIndex++;
                    continue;
                }

                if (d1 && d2) {
                    // Both are divisible. Remove the divisor and continue.
                    g1 /= prime;
                    g2 /= prime;
                    continue;
                }

                if (d1) {
                    return -1;
                } else {
                    return 1;
                }
            }
            System.out.println("WARNING... you should't be here!");
            return(0); //This should NEVER happen
        }
    }



    private static class Data {
        public String word;     // The original string-- useful to outputting at the end.

        public Data(String inWord) {
            word = new String(inWord); // Make a copy of the string
        }

        public static void print_test(String s1,String s2){
            ArrayList<Long> primes = new ArrayList<>();
            // Start the array off with a few values computed ahead of time.
            primes.add(2L);
            primes.add(3L);
            primes.add(5L);
            primes.add(7L);
            primes.add(11L);
            primes.add(13L);
            primes.add(17L);
            primes.add(19L);

            Data testItem1 = new Data(s1);
            Data testItem2 = new Data(s2);
            GematriaComparator comparator = new GematriaComparator(primes);

            System.out.println("Defining: s1 = "+s1+" and s2 = "+s2);
            System.out.println("\tg1 = "+comparator.gematrify(s1)+ " and g2 = "+comparator.gematrify(s2));
            System.out.println("Compare: "+s1+" to "+s2+": ");
            System.out.println("Result="+comparator.compare(testItem1,testItem2));
            System.out.println("Compare: "+s2+" to "+s1+": ");
            System.out.println("Result="+comparator.compare(testItem2,testItem1));
            System.out.println("---");
        }

        public static void test_Data() {
            String s1,s2;

            print_test("cat","cat");      // Two of the same thing
            print_test("cat","dog");      // Two different
            print_test("cat","doh");      // both with even gematria
            System.out.println("---");
        }
    }
}