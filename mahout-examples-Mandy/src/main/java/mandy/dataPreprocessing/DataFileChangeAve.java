package mandy.dataPreprocessing;

import java.io.*;

public class DataFileChangeAve {

	private static String baseID = new String("L0002");
	private static String cellID = new String("2");
	// private static String baseID2 = new String("L0002");
	// private static String cellID2 = new String("2");
	private static final String[] timeArray = { "18/11/2013 6:00",
			"18/11/2013 6:15", "18/11/2013 6:30", "18/11/2013 6:45",
			"18/11/2013 14:00", "18/11/2013 14:15", "18/11/2013 14:30",
			"18/11/2013 14:45" };
	private static double[] dataArray = { 0, 0, 0, 0, 0, 0, 0, 0 };
	private static double[] sumArray = { 0, 0, 0, 0, 0, 0, 0, 0 };// store
																	// summation
																					// of
																					// each
																					// column
	private static int[] countArray = { 0, 0, 0, 0, 0, 0, 0, 0 };// store
																	// average
																	// values of
																	// each
																	// column

	// private static double[] aveArray = {0, 0, 0, 0, 0, 0, 0, 0};

	public static void main(String[] args) throws IOException {

		FileInputStream fstream = new FileInputStream(
				"/home/mansi/Desktop/data1-f12.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		PrintWriter out = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/data1_f12_transposed.txt", false));
		// Read File Line By Line
		String strLine0 = br.readLine();// read in the column title
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console
			dataProcess(strLine, out);
			// System.out.println(strLine);
		}
		printResult(out); // print the last line
		// Close the input stream
		br.close();
		out.close();


		// read in file again
		fstream = new FileInputStream("/home/mansi/Desktop/data1-f12.csv");
		br = new BufferedReader(new InputStreamReader(fstream));

		PrintWriter out2 = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/Ave_data1_f12_transposed.txt", true));
		// Read File Line By Line
		baseID = "L0002";
		cellID = "2";
		for (int i = 0; i < 8; i++) {
			dataArray[i] = sumArray[i] / countArray[i];
		}
		strLine0 = br.readLine();// read in the column title
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console
			dataProcess2(strLine, out2);
			// System.out.println(strLine);
		}
		printResult2(out2); // print the last line
		// Close the input stream
		br.close();
		out2.close();

	}

	private static void dataProcess(String data, PrintWriter out) {
		String[] dataFeatures = data.split(",");
		int indexT = getIndexOfTime(dataFeatures[0]);
		if ((dataFeatures[1].equals(baseID))
				&& (dataFeatures[2].equals(cellID))) {
			dataArray[indexT] = Double.valueOf(dataFeatures[3]);
			sumArray[indexT] += Double.valueOf(dataFeatures[3]);
			countArray[indexT]++;

		} else {

			printResult(out);
			baseID = dataFeatures[1];
			cellID = dataFeatures[2];
			dataArray = new double[8]; // without clearing dataArray, last line
										// value resides in the place of missing
										// value.
			// dataArray = new String[] { "0", "0", "0", "0", "0", "0", "0", "0"
			// };
			dataArray[indexT] = Double.valueOf(dataFeatures[3]);
			sumArray[indexT] += Double.valueOf(dataFeatures[3]);
			countArray[indexT]++;
		}

	}

	private static void dataProcess2(String data, PrintWriter out2) {
		

		String[] dataFeatures = data.split(",");
		int indexT=getIndexOfTime(dataFeatures[0]);
		if ((dataFeatures[1].equals(baseID))
				&& (dataFeatures[2].equals(cellID))) {
			dataArray[indexT] = Double.valueOf(dataFeatures[3]);

		} else {

			printResult2(out2);
			baseID = dataFeatures[1];
			cellID = dataFeatures[2];

			for (int i = 0; i < 8; i++) {
				dataArray[i] = sumArray[i] / countArray[i];
			}
			// clear dataArray with average values
			dataArray[indexT] = Double.valueOf(dataFeatures[3]);

		}
	}

	private static void printResult(PrintWriter out) {
		System.out.println(dataArray[0] + " " + dataArray[1] + " "
				+ dataArray[2] + " " + dataArray[3] + " " + dataArray[4] + " "
				+ dataArray[5] + " " + dataArray[6] + " " + dataArray[7] + " "
				+ baseID + " " + cellID);
		out.println(dataArray[0] + " " + dataArray[1] + " " + dataArray[2]
				+ " " + dataArray[3] + " " + dataArray[4] + " " + dataArray[5]
				+ " " + dataArray[6] + " " + dataArray[7] + " " + baseID + " "
				+ cellID); // write to the file

	}

	private static void printResult2(PrintWriter out) {
		System.out.println(dataArray[0] + " " + dataArray[1] + " "
				+ dataArray[2] + " " + dataArray[3] + " " + dataArray[4] + " "
				+ dataArray[5] + " " + dataArray[6] + " " + dataArray[7]);
		out.println(dataArray[0] + " " + dataArray[1] + " " + dataArray[2]
				+ " " + dataArray[3] + " " + dataArray[4] + " " + dataArray[5]
				+ " " + dataArray[6] + " " + dataArray[7]); // write to the file

	}

	private static int getIndexOfTime(String time) {
		for (int i = 0; i < timeArray.length; i++) {
			if (time.compareTo(timeArray[i]) == 0) {
				return i;
			}

		}
		return -1;

	}

}
