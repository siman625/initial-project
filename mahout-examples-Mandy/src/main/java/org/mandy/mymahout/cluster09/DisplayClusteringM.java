package org.mandy.mymahout.cluster09;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.clustering.conversion.InputDriver;
import org.apache.mahout.clustering.evaluation.ClusterEvaluator;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.utils.clustering.ClusterDumper;
import org.mandy.mymahout.hdfs.HdfsDAO;
import org.mandy.mymahout.recommendation.ItemCFHadoop;

public class DisplayClusteringM {
	private static final String HDFS = "hdfs://namenode:54310";

	public static void main(String[] args) throws Exception {

		String inPath = HDFS + "/user/hdfs/mix_data";
		String outPath = inPath + "/result/";
		// JobConf conf = config();
		// conf.set("mapred.job.tracker", "ajila-server-01:54311");
		// conf.set("fs.default.name", "hdfs://ajila-server-01:54310");
		// conf.set("mapred.tasktracker.map.tasks.maximum", "8");
		// conf.set("dfs.permissions", "false");
		// conf.set("dfs.block.size", "8388608");
		// HdfsDAO hdfs = new HdfsDAO(HDFS, conf);
		// hdfs.rmr(inPath);
		// hdfs.mkdirs(inPath);
		// hdfs.copyFile(localFile, inPath);
		String clusteredPoints = outPath + "/clusteredPoints";
		Path outGlobPath = new Path(outPath, "clusters-*-final");
		Path clusteredPointsPath = new Path(clusteredPoints);
		System.out
				.printf("Dumping out clusters from clusters: %s and clusteredPoints: %s\n",
						outGlobPath, clusteredPointsPath);

		ClusterDumper clusterDumper = new ClusterDumper(outGlobPath,
				clusteredPointsPath);
		// clusterDumper.printClusters(null);
		// clusterDumper.
		displayCluster(clusterDumper);
		// Map<Integer, List<VectorWritable>> representativePoints = new
		// TreeMap<Integer, List<VectorWritable>>();
		// /ClusterEvaluator clusterEvaluator = new ClusterEvaluator(
		// representativePoints, loadClusters(clusterDumper), measure);
		// Double interClusterDensity = clusterEvaluator.interClusterDensity();
		// System.out.println("interClusterDesity:" + interClusterDensity);
	}

	/*
	 * public static JobConf config() { JobConf conf = new
	 * JobConf(ItemCFHadoop.class); conf.setJobName("ItemCFHadoop");
	 * conf.addResource("classpath:/hadoop/core-site.xml");
	 * conf.addResource("classpath:/hadoop/hdfs-site.xml");
	 * conf.addResource("classpath:/hadoop/mapred-site.xml"); return conf; }
	 */

	public static void displayCluster(ClusterDumper clusterDumper)
			throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/result_display.txt", false));
		out.println("x y z group");
		Iterator<Integer> keys = clusterDumper.getClusterIdToPoints().keySet()
				.iterator();
		while (keys.hasNext()) {
			Integer center = keys.next();
			// System.out.println("Center:" + center);
			for (WeightedVectorWritable point : clusterDumper
					.getClusterIdToPoints().get(center)) {
				Vector v = point.getVector();
				String output = v.get(0) + " " + v.get(1) + " " + v.get(2)
						+ " " + center;
				System.out.println(output);
				out.println(output);
			}
		}
		out.close();
	}

	/*
	 * public Map<Integer,List<VectorWritable>> convertFormat(ClusterDumper
	 * clusterDumper){
	 * 
	 * List<VectorWritable> vectorWritable= new ArrayList<VectorWritable>();
	 * 
	 * for(WeightedPropertyVectorWritable v :
	 * clusterDumper.getClusterIdToPoints().get(key)) vectorWritable.add
	 * Map<Integer,List<VectorWritable>> representativePoints = new
	 * TreeMap<Integer,List<VectorWritable>>();
	 * 
	 * }
	 */
	/*
	 * public static List<Cluster> loadClusters(ClusterDumper clusterDumper) {
	 * List<Cluster> clusters = new ArrayList<Cluster>(); Iterator<Integer> keys
	 * = clusterDumper.getClusterIdToPoints().keySet() .iterator(); while
	 * (keys.hasNext()) { Integer center = keys.next(); for
	 * (WeightedVectorWritable point : clusterDumper
	 * .getClusterIdToPoints().get(center)) { // Cluster v = (Cluster)point;
	 * clusters.add((Cluster) point); }
	 * 
	 * } return clusters; }
	 */

}
