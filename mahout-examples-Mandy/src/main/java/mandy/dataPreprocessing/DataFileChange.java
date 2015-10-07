package mandy.dataPreprocessing;

import java.io.*;

public class DataFileChange {

	private static String baseID = new String("L0002");
	private static String cellID = new String("2");
	private static final String[] timeArray = { "18/11/2013 14:45",
			"18/11/2013 14:30", "18/11/2013 14:15", "18/11/2013 14:00",
			"18/11/2013 6:45", "18/11/2013 6:30", "18/11/2013 6:15",
			"18/11/2013 6:00" };
	private static String[] dataArray = { "0", "0", "0", "0", "0", "0", "0",
			"0" };
	private static String[] averageArray = { "0", "0", "0", "0", "0", "0", "0",
			"0" };// store average values of each column

	public static void main(String[] args) throws IOException {

		FileInputStream fstream = new FileInputStream(
				"/home/mansi/Desktop/data1-f12.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		PrintWriter out = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/data1_f12_transposed.txt", true));
		// Read File Line By Line
		String strLine0 = br.readLine();// read in the column title
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console
			dataProcess(strLine, out);
			// System.out.println(strLine);
		}
		System.out.println(dataArray[0] + " " + dataArray[1] + " "
				+ dataArray[2] + " " + dataArray[3] + " " + dataArray[4] + " "
				+ dataArray[5] + " " + dataArray[6] + " " + dataArray[7] + " "
				+ baseID + " " + cellID);
		// Close the input stream
		br.close();
		out.close();

	}

	private static void dataProcess(String data, PrintWriter out) {
		String[] dataFeatures = data.split(",");
		if ((dataFeatures[1].equals(baseID))
				&& (dataFeatures[2].equals(cellID))) {
			dataArray[getIndexOfTime(dataFeatures[0])] = dataFeatures[3];

		} else {

			System.out.println(dataArray[0] + " " + dataArray[1] + " "
					+ dataArray[2] + " " + dataArray[3] + " " + dataArray[4]
					+ " " + dataArray[5] + " " + dataArray[6] + " "
					+ dataArray[7] + " " + baseID + " " + cellID);
			out.println(dataArray[0] + " " + dataArray[1] + " " + dataArray[2]
					+ " " + dataArray[3] + " " + dataArray[4] + " "
					+ dataArray[5] + " " + dataArray[6] + " " + dataArray[7]
					+ " " + baseID + " " + cellID);
			baseID = dataFeatures[1];
			cellID = dataFeatures[2];
			dataArray = new String[8]; // without clearing dataArray, last line
										// value resides in the place of missing
										// value.
			// dataArray = new String[] { "0", "0", "0", "0", "0", "0", "0", "0"
			// };
			dataArray[getIndexOfTime(dataFeatures[0])] = dataFeatures[3];

		}

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
