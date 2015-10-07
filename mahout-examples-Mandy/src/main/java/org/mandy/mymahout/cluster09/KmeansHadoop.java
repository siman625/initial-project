package org.mandy.mymahout.cluster09;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.mahout.clustering.canopy.CanopyDriver;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.clustering.conversion.InputDriver;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.CosineDistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.common.distance.SquaredEuclideanDistanceMeasure;
import org.apache.mahout.common.distance.WeightedEuclideanDistanceMeasure;
import org.apache.mahout.common.distance.ManhattanDistanceMeasure;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.utils.clustering.ClusterDumper;
import org.mandy.mymahout.hdfs.HdfsDAO;
import org.mandy.mymahout.recommendation.ItemCFHadoop;
import org.apache.mahout.clustering.display.DisplayClustering;
import java.io.*;
import org.apache.mahout.clustering.evaluation.ClusterEvaluator;
import org.apache.mahout.clustering.fuzzykmeans.FuzzyKMeansDriver;
import org.apache.mahout.clustering.Cluster;
public class KmeansHadoop {
	private static final String HDFS = "hdfs://namenode:54310";// "hdfs://ajila-server-01:54310";//
																// HDFS =

	// "hdfs://172.16.231.134:49000";//

	public static void main(String[] args) throws Exception {
		// String localFile = "datafile/randomData2.csv";
		// String localFile = "datafile/Ave_data3_f6_transposed.txt";
		// String localFile = "datafile/f6.res.pc1&2.csv";
		// String localFile = "datafile/pca.res.pc1-2.csv";// 19:00
		// String localFile = "datafile/Ave_data1_f12_transposed.txt";
		// String localFile = "datafile/iris.txt";// April 1
		// / String localFile = "datafile/eric_data1_normed.txt";
		// String localFile = "datafile/item.csv";
		// String localFile = "datafile/eric_data(1).txt";
		// String localFile = "datafile/scaled_dataset3";
		// String localFile = "datafile/dataset4-1day-part1-normalized";
		String localFile = "datafile/Ave_data4_f16_jan27_transposed.txt";//
		// dataset4
		// String localFile = "datafile/dataset4-1week*3-part1-normalized"; //
		// String localFile = "datafile/dataset1-4&11.csv";
		// String localFile = "datafile/data4-week1-pca.res.x";
		// String localFile = "datafile/data4-week1-pca1&2&3.csv";
		// String localFile = "datafile/randomData.csv";
		String inPath = HDFS + "/user/hdfs/mix_data";
		String seqFile = inPath + "/seqfile";
		// String seqFile = HDFS + "/user/hdfs/matrixmult";//Mandy
		String seeds = inPath + "/seeds";
		String outPath = inPath + "/result/";
		String clusteredPoints = outPath + "/clusteredPoints";
		JobConf conf = config();
		// conf.set("mapred.job.tracker", "ajila-server-01:54311");
		// conf.set("fs.default.name", "hdfs://ajila-server-01:54310");
		// conf.set("mapred.tasktracker.map.tasks.maximum", "1");
		// conf.set("mapred.tasktracker.reduce.tasks.maximum", "16");

		conf.set("mapred.job.tracker", "namenode:54311");
		conf.set("fs.default.name", "hdfs://namenode:54310");

		conf.setNumMapTasks(2);
		conf.setNumReduceTasks(8);
		// conf.set("mapred.job.tracker", "172.16.231.134:49001");
		// conf.set("fs.default.name", "hdfs://172.16.231.134:49000");
		// conf.setNumMapTasks(1);
		conf.set("dfs.permissions", "false");
		// conf.set("mapreduce.job.maps", "4");
		conf.set("dfs.block.size", "1258291200");// 6
		// conf.set("mapred.map.tasks", "16");
		conf.set("mapred.min.split.size", "1258291200" + ""); // 1048576=1M
		// conf.set("mapred.max.split.size", "268435456");
		HdfsDAO hdfs = new HdfsDAO(HDFS, conf);
		hdfs.rmr(inPath);
		hdfs.mkdirs(inPath);
		hdfs.copyFile(localFile, inPath);
		// hdfs.copyFile(localFile2, inPath);
		hdfs.ls(inPath);

		InputDriver.runJob(new Path(inPath), new Path(seqFile),
				"org.apache.mahout.math.RandomAccessSparseVector"); // convert
																	// to vector

		int k = 4;
		Path seqFilePath = new Path(seqFile);
		Path clustersSeeds = new Path(seeds);
		DistanceMeasure measure = new EuclideanDistanceMeasure();
		// DistanceMeasure measure = new CosineDistanceMeasure();
		/*
		 * DistanceMeasure measure = new WeightedEuclideanDistanceMeasure();
		 * measure.distance(new DenseVector( new double[] { 1, 0.0000001,
		 * 100000000 }), new DenseVector( new double[] { 1, 0.000000001,
		 * 2000000000 }));
		 */
		clustersSeeds = RandomSeedGenerator.buildRandom(conf, seqFilePath,
				clustersSeeds, k, measure);
		KMeansDriver.run(conf, seqFilePath, clustersSeeds, new Path(outPath),
				0.01, 100, true, 0.1, false);
		/*
		 * canopy & fuzzy k-means clustering
		 */

		//Path canopyOutput = new Path(outPath, "canopies");
		//CanopyDriver.run(conf, seqFilePath, canopyOutput, measure, 80, 55,
		// false, 0.0, false);
		//KMeansDriver.run(conf, seqFilePath, new Path(canopyOutput,
		// "clusters-0-final"), new Path(outPath), 0.01, 100, true, 0.1,false);

		// FuzzyKMeansDriver.run(conf, seqFilePath, clustersSeeds, new Path(
		// outPath), 0.01, 100, 1.1f, true, true, 0.0, false);//
		// FuzzyKMeansDriver.run(conf, seqFilePath, new Path(canopyOutput,
		// "clusters-0-final"), new Path(outPath), 0.01, 10, 1.3f, true,
		// true, 0.0, false);
		// Parameter:(directoryContainingConvertedInput,
									// new Path(canopyOutput,
									// "clusters-0-final"), output,
														// convergenceDelta,
														// maxIteration,fuzziness,
														// true, true, 0.0,
														// false);
		Path outGlobPath = new Path(outPath, "clusters-*-final");
		Path clusteredPointsPath = new Path(clusteredPoints);
		System.out
				.printf("Dumping out clusters from clusters: %s and clusteredPoints: %s\n",
						outGlobPath, clusteredPointsPath);
		ClusterDumper clusterDumper = new ClusterDumper(outGlobPath,
				clusteredPointsPath);
		// clusterDumper.printClusters(null);
		// clusterDumper.
		// displayCluster(clusterDumper);

		Map<Integer, List<VectorWritable>> representativePoints = new TreeMap<Integer, List<VectorWritable>>();
		ClusterEvaluator clusterEvaluator = new ClusterEvaluator(
				representativePoints, loadClusters(clusterDumper), measure);
		Double interClusterDensity = clusterEvaluator.interClusterDensity();
		System.out.println("interClusterDesity:" + interClusterDensity);// may
																		// not
																		// work(modified
																		// by
																		// Mandy)
	}

	public static JobConf config() {
		JobConf conf = new JobConf(ItemCFHadoop.class);
		conf.setJobName("ItemCFHadoop");
		conf.addResource("classpath:/hadoop/core-site.xml");
		conf.addResource("classpath:/hadoop/hdfs-site.xml");
		conf.addResource("classpath:/hadoop/mapred-site.xml");
		return conf;
	}

	public static void displayCluster(ClusterDumper clusterDumper)
			throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/result_display.txt", true));
		out.println("x y z group");
		Iterator<Integer> keys = clusterDumper.getClusterIdToPoints().keySet()
				.iterator();
		while (keys.hasNext()) {
			Integer center = keys.next();
			// System.out.println("Center:" + center);
			for (WeightedVectorWritable point : clusterDumper
					.getClusterIdToPoints().get(center)) {
				Vector v = point.getVector();
				String output = v.get(14) + " " + v.get(15) + " " + v.get(16)
						+ " " + center;
				System.out.println(output);
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
	public static List<Cluster> loadClusters(ClusterDumper clusterDumper) {
		List<Cluster> clusters = new ArrayList<Cluster>();
		Iterator<Integer> keys = clusterDumper.getClusterIdToPoints().keySet()
				.iterator();
		while (keys.hasNext()) {
			Integer center = keys.next();
			for (WeightedVectorWritable point : clusterDumper
					.getClusterIdToPoints().get(center)) {
				// Cluster v = (Cluster)point;
				clusters.add((Cluster) point);
			}
			
		}
		return clusters;
	}
}


