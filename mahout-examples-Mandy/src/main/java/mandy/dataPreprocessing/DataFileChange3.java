package mandy.dataPreprocessing;

import java.io.*;


public class DataFileChange3 {

	private static String NodeID = new String("SS10021");
	// private static String cellID = new String("2");
	private static final String[] timeArray = { "5/1/2014 5:00",
			"5/1/2014 6:00",
			"5/1/2014 7:00", "5/1/2014 8:00", "5/1/2014 9:00",
			"5/1/2014 10:00", "5/1/2014 11:00", "5/1/2014 12:00",
			"5/1/2014 13:00", "5/1/2014 14:00", "5/1/2014 15:00",
			"5/1/2014 16:00", "5/1/2014 16:45", "5/1/2014 18:00",
			"5/1/2014 19:00", "5/1/2014 20:00", "5/1/2014 21:00",
			"5/1/2014 22:00", "5/1/2014 23:00", "5/2/2014 0:00",
			"5/2/2014 1:00", "5/2/2014 2:00", "5/2/2014 3:00", "5/2/2014 4:00",
 };
	private static double[] dataArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private static double[] sumArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// store
	// summation
	// of
	// each
	// column
	private static int[] countArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// store

	// average
	// values of
	// each
	// column

	public static void main(String[] args) throws IOException {

		FileInputStream fstream = new FileInputStream(
				"/home/mansi/Desktop/dataset3-f15.txt.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		PrintWriter out = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/dataset3-f15_transposed.txt", false));
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
		fstream = new FileInputStream(
				"/home/mansi/Desktop/dataset3-f15.txt.csv");
		br = new BufferedReader(new InputStreamReader(fstream));

		PrintWriter out2 = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/Ave_data3_f15_transposed.txt", true));
		// Read File Line By Line
		NodeID = "SS10021";
		for (int i = 0; i < timeArray.length; i++) {
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
		if (dataFeatures[1].equals(NodeID)) {
			dataArray[indexT] = Double.valueOf(dataFeatures[2]);
			sumArray[indexT] += Double.valueOf(dataFeatures[2]);
			countArray[indexT]++;

		} else {

			printResult(out);
			NodeID = dataFeatures[1];
			dataArray = new double[25]; // without clearing dataArray, last line
										// value resides in the place of missing
										// value.
			// dataArray = new String[] { "0", "0", "0", "0", "0", "0", "0", "0"
			// };
			dataArray[indexT] = Double.valueOf(dataFeatures[2]);
			sumArray[indexT] += Double.valueOf(dataFeatures[2]);
			countArray[indexT]++;
		}

	}

	private static void dataProcess2(String data, PrintWriter out2) {

		String[] dataFeatures = data.split(",");
		int indexT = getIndexOfTime(dataFeatures[0]);
		if (dataFeatures[1].equals(NodeID)) {
			dataArray[indexT] = Double.valueOf(dataFeatures[2]);

		} else {

			printResult2(out2);
			NodeID = dataFeatures[1];

			for (int i = 0; i < timeArray.length; i++) {
				dataArray[i] = sumArray[i] / countArray[i];
			}
			// clear dataArray with average values
			dataArray[indexT] = Double.valueOf(dataFeatures[2]);

		}
	}

	private static void printResult(PrintWriter out) {

		String a = "";
		for (int n = 0; n < timeArray.length; n++) {
			a = a + dataArray[n] + " ";
		}
		a = a + NodeID;
		System.out.println(a);

		/*
		 * System.out.println(dataArray[0] + " " + dataArray[1] + " " +
		 * dataArray[2] + " " + dataArray[3] + " " + dataArray[4] + " " +
		 * dataArray[5] + " " + dataArray[6] + " " + dataArray[7] + " " +
		 * dataArray[8] + " " + dataArray[9] + " " + dataArray[10] + " " +
		 * dataArray[11] + " " + dataArray[12] + " " + dataArray[13] + " " +
		 * dataArray[14] + " " + dataArray[15] + " " + dataArray[16] + " " +
		 * dataArray[17] + " " + dataArray[18] + " " + dataArray[19] + " " +
		 * dataArray[20] + " " + dataArray[21] + " " + dataArray[22] + " " +
		 * dataArray[23] + " " + NodeID);
		 */

		out.println(a); // write to the file
	}

	private static void printResult2(PrintWriter out) {
		String a = "";
		for (int n = 0; n < timeArray.length; n++) {
			a = a + dataArray[n] + " ";
		}
		System.out.println(a);
		/*
		 * System.out.println(dataArray[0] + " " + dataArray[1] + " " +
		 * dataArray[2] + " " + dataArray[3] + " " + dataArray[4] + " " +
		 * dataArray[5] + " " + dataArray[6] + " " + dataArray[7] + " " +
		 * dataArray[8] + " " + dataArray[9] + " " + dataArray[10] + " " +
		 * dataArray[11] + " " + dataArray[12] + " " + dataArray[13] + " " +
		 * dataArray[14] + " " + dataArray[15] + " " + dataArray[16] + " " +
		 * dataArray[17] + " " + dataArray[18] + " " + dataArray[19] + " " +
		 * dataArray[20] + " " + dataArray[21] + " " + dataArray[22] + " " +
		 * dataArray[23]);
		 */
		out.println(a); // write
																				// to
																				// the
																				// file
	}
	private static int getIndexOfTime(String time) {
		int i = 0;
		for (i = 0; i < timeArray.length; i++) {
			if (time.compareTo(timeArray[i]) == 0) {
				return i;
			}

		}
		return i;

	}

}

