package mandy.clustering.resultAnalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class PointsIndexResult {

	private TreeMap<String, ArrayList<Integer>> clusterIdtoIndex;
	private Integer index;
	private String fileName;

	public PointsIndexResult(String fileName) throws IOException {

		this.fileName = fileName;
		clusterIdtoIndex = new TreeMap<String, ArrayList<Integer>>();
		index = 0;
		FileInputStream fstream = new FileInputStream(this.fileName);
		// "/home/mansi/Desktop/clusteredPoints"
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		PrintWriter out = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/ClusteredPointsIndex_k4.txt", true));
		// Read File Line By Line
		String strLine0 = br.readLine();
		String strLine1 = br.readLine();// read in two header lines
		while ((strLine = br.readLine()) != null) {

			index++;
			dataProcess(strLine);

		}
		resultDisplay(out);
		br.close();
		out.close();

	}

	private void dataProcess(String data) {
		String[] clusterResult = data.split(" ");
		if (clusterIdtoIndex.containsKey(clusterResult[1])) {// clusterResult[1]
																// is the
																// cluster ID
			clusterIdtoIndex.get(clusterResult[1]).add(index);// index: number
																// of the rows
																// of that
																// datapoint

		} else {

			ArrayList<Integer> indexValue = new ArrayList<Integer>();
			indexValue.add(index);
			clusterIdtoIndex.put(clusterResult[1], indexValue);
		}

	}

	public TreeMap<String, ArrayList<Integer>> getClusterIdtoIndex() {
		return this.clusterIdtoIndex;
	}

	private void resultDisplay(PrintWriter out0) {

		for (String key : clusterIdtoIndex.keySet()) {
			System.out.println("clusterID: " + key);
			out0.println("");
			out0.println("clusterID: " + key);
			Integer indexA = 0;
			for (int i = 0; i < clusterIdtoIndex.get(key).size(); i++) {
				System.out.println(clusterIdtoIndex.get(key).get(indexA));
				out0.print(clusterIdtoIndex.get(key).get(indexA) + " ");
				indexA++;
			}

		}

	}

}
