package mandy.dataPreprocessing;

import java.io.*;

public class DataFileChangeAve4 {

	// private static String baseID = new String("RCA80004c2");
	private static String cellID = new String("RCA80004c2");
	// private static String baseID2 = new String("L0002");
	// private static String cellID2 = new String("2");
	private static final String[] timeArray = { "1/27/2015 0:00",
			"1/27/2015 1:00", "1/27/2015 2:00", "1/27/2015 3:00",
			"1/27/2015 4:00", "1/27/2015 5:00", "1/27/2015 6:00",
			"1/27/2015 7:00", "1/27/2015 8:00", "1/27/2015 9:00",
			"1/27/2015 10:00", "1/27/2015 11:00", "1/27/2015 12:00",
			"1/27/2015 13:00", "1/27/2015 14:00", "1/27/2015 15:00",
			"1/27/2015 16:00", "1/27/2015 17:00", "1/27/2015 18:00",
			"1/27/2015 19:00", "1/27/2015 20:00", "1/27/2015 21:00",
			"1/27/2015 22:00", "1/27/2015 23:00" };
	/*
	 * "1/25/2015 0:00", "1/25/2015 1:00", "1/25/2015 2:00", "1/25/2015 3:00",
	 * "1/25/2015 4:00", "1/25/2015 5:00", "1/25/2015 6:00", "1/25/2015 7:00",
	 * "1/25/2015 8:00", "1/25/2015 9:00", "1/25/2015 10:00", "1/25/2015 11:00",
	 * "1/25/2015 12:00", "1/25/2015 13:00", "1/25/2015 14:00",
	 * "1/25/2015 15:00", "1/25/2015 16:00", "1/25/2015 17:00",
	 * "1/25/2015 18:00", "1/25/2015 19:00", "1/25/2015 20:00",
	 * "1/25/2015 21:00", "1/25/2015 22:00", "1/25/2015 23:00"
	 */

	private static double[] dataArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private static double[] sumArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// store
																	// summation
																					// of
																					// each
																					// column
	private static int[] countArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// store
																	// average
																	// values of
																	// each
																	// column

	// private static double[] aveArray = {0, 0, 0, 0, 0, 0, 0, 0};

	public static void main(String[] args) throws IOException {

		FileInputStream fstream = new FileInputStream(
				"/home/mansi/Desktop/data4-f16-jan27.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		PrintWriter out = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/data4_f16_jan27_transposed.txt", false));
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
		fstream = new FileInputStream("/home/mansi/Desktop/data4-f16-jan27.csv");
		br = new BufferedReader(new InputStreamReader(fstream));

		PrintWriter out2 = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/Ave_data4_f16_jan27_transposed.txt", true));
		// Read File Line By Line
		// baseID = "L0002";
		cellID = "RCA80004c2";
		for (int i = 0; i < dataArray.length; i++) {
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
		if (dataFeatures[1].equals(cellID)) {
			dataArray[indexT] = Double.valueOf(dataFeatures[2]);
			sumArray[indexT] += Double.valueOf(dataFeatures[2]);
			countArray[indexT]++;

		} else {

			printResult(out);
			// baseID = dataFeatures[1];
			cellID = dataFeatures[1];
			dataArray = new double[24]; // without clearing dataArray, last line
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
		int indexT=getIndexOfTime(dataFeatures[0]);
		if (dataFeatures[1].equals(cellID)) {
			dataArray[indexT] = Double.valueOf(dataFeatures[2]);

		} else {

			printResult2(out2);
			// baseID = dataFeatures[1];
			cellID = dataFeatures[1];

			for (int i = 0; i < dataArray.length; i++) {
				dataArray[i] = sumArray[i] / countArray[i];
			}
			// clear dataArray with average values
			dataArray[indexT] = Double.valueOf(dataFeatures[2]);

		}
	}

	private static void printResult(PrintWriter out) {
		System.out.println(dataArray[0] + " " + dataArray[1] + " "
				+ dataArray[2] + " " + dataArray[3] + " " + dataArray[4] + " "
				+ dataArray[5] + " " + dataArray[6] + " " + dataArray[7] + " "
				+ dataArray[8] + " " + dataArray[9] + " " + dataArray[10] + " "
				+ dataArray[11] + " " + dataArray[12] + " " + dataArray[13]
				+ " " + dataArray[14] + " " + dataArray[15] + " "
				+ dataArray[16] + " " + dataArray[17] + " " + dataArray[18]
				+ " " + dataArray[19] + " " + dataArray[20] + " "
				+ dataArray[21] + " " + dataArray[22] + " " + dataArray[23]
				+ " " + cellID);
		out.println(dataArray[0] + " " + dataArray[1] + " " + dataArray[2]
				+ " " + dataArray[3] + " " + dataArray[4] + " " + dataArray[5]
				+ " " + dataArray[6] + " " + dataArray[7] + " " + dataArray[8]
				+ " " + dataArray[9] + " " + dataArray[10] + " "
				+ dataArray[11] + " " + dataArray[12] + " " + dataArray[13]
				+ " " + dataArray[14] + " " + dataArray[15] + " "
				+ dataArray[16] + " " + dataArray[17] + " " + dataArray[18]
				+ " " + dataArray[19] + " " + dataArray[20] + " "
				+ dataArray[21] + " " + dataArray[22] + " " + dataArray[23]
				+ " " + cellID); // write to the file

	}

	private static void printResult2(PrintWriter out) {
		System.out.println(dataArray[0] + " " + dataArray[1] + " "
				+ dataArray[2] + " " + dataArray[3] + " " + dataArray[4] + " "
				+ dataArray[5] + " " + dataArray[6] + " " + dataArray[7] + " "
				+ dataArray[8] + " " + dataArray[9] + " " + dataArray[10] + " "
				+ dataArray[11] + " " + dataArray[12] + " " + dataArray[13]
				+ " " + dataArray[14] + " " + dataArray[15] + " "
				+ dataArray[16] + " " + dataArray[17] + " " + dataArray[18]
				+ " " + dataArray[19] + " " + dataArray[20] + " "
				+ dataArray[21] + " " + dataArray[22] + " " + dataArray[23]);
		out.println(dataArray[0] + " " + dataArray[1] + " " + dataArray[2]
				+ " " + dataArray[3] + " " + dataArray[4] + " " + dataArray[5]
				+ " " + dataArray[6] + " " + dataArray[7] + " " + dataArray[8]
				+ " " + dataArray[9] + " " + dataArray[10] + " "
				+ dataArray[11] + " " + dataArray[12] + " " + dataArray[13]
				+ " " + dataArray[14] + " " + dataArray[15] + " "
				+ dataArray[16] + " " + dataArray[17] + " " + dataArray[18]
				+ " " + dataArray[19] + " " + dataArray[20] + " "
				+ dataArray[21] + " " + dataArray[22] + " " + dataArray[23]); // write
																				// to
																				// the
																				// file

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
