package anticancer.column;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

public class ColumnDivider {
	public Properties colprop = new Properties();

	public ColumnDivider() {
		try {
			colprop.load(new FileReader(new File("columns.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ColumnDivider cd = new ColumnDivider();
		Scanner sc = new Scanner(System.in);
		String inputFileName;
		String columnName;
		String outputFileName;
		
		System.out.println("Insert input file name");
		inputFileName = sc.nextLine();
		
		System.out.println("Insert column name:");
		columnName = sc.nextLine();

		if(!cd.colprop.contains(columnName)) {
			System.err.println(columnName + "doesn't exist!");
			System.exit(0);
		}
		
		outputFileName = "col" + columnName + "out";
		
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			
			Vector<String> input = new Vector<String>();
			
			String line = br.readLine();
			
			while(line != null) {
				String[] arr = line.split("\\s");
				line = arr[Integer.parseInt(cd.colprop.getProperty(columnName)) - 1];
				input.add(line);
				line = br.readLine();
			}
			
			Vector<String> output = new Vector<String>();
			
//			output = divideColumn(input, columnName); // TODO
			
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		sc.close();
	}
}
