package mandy.clustering.resultAnalysis;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
//import org.apache.mahout.clustering.canopy.CanopyDriver;
//import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.clustering.conversion.InputDriver;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.DistanceMeasure;
//import org.apache.mahout.common.distance.CosineDistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.common.distance.SquaredEuclideanDistanceMeasure;
import org.apache.mahout.common.iterator.sequencefile.PathFilters;
import org.apache.mahout.common.iterator.sequencefile.PathType;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileDirValueIterable;
import org.apache.mahout.math.RandomAccessSparseVector;
//import org.apache.mahout.common.distance.SquaredEuclideanDistanceMeasure;
//import org.apache.mahout.common.distance.WeightedEuclideanDistanceMeasure;
//import org.apache.mahout.common.distance.ManhattanDistanceMeasure;
//import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.utils.clustering.ClusterDumper;
import org.mandy.mymahout.hdfs.HdfsDAO;
import org.mandy.mymahout.recommendation.ItemCFHadoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;






//import org.apache.mahout.clustering.display.DisplayClustering;
import java.io.*;

import org.apache.mahout.clustering.evaluation.ClusterEvaluator;
import org.apache.mahout.clustering.evaluation.RepresentativePointsDriver;
//import org.apache.mahout.clustering.fuzzykmeans.FuzzyKMeansDriver;
import org.apache.mahout.clustering.Cluster;

import com.google.common.collect.Lists;
public class ClusterInfoDisplay {
	public static final String DISTANCE_MEASURE_KEY = "org.apache.mahout.clustering.measure";
	public static final String STATE_IN_KEY = "org.apache.mahout.clustering.stateIn";
	private static final String HDFS = "hdfs://namenode:54310";// "hdfs://ajila-server-01:54310";//
	private static final Logger log = LoggerFactory
			.getLogger(ClusterInfoDisplay.class);
	private static List<Cluster> clusters;
	public static void main(String[] args) throws Exception {

		int numIters = 5;
		// JobConf conf = config();
		Configuration conf = new Configuration();
		// String localFile = "datafile/iris.txt";
		String inPath = HDFS + "/user/hdfs/mix_data";
		// String seqFile = inPath + "/seqfile";
		// String seqFile = HDFS + "/user/hdfs/matrixmult";//Mandy
		// String seeds = inPath + "/seeds";
		String outPath = inPath + "/result/";
		String clusteredPoints = outPath + "/clusteredPoints";
		// DistanceMeasure measure = new EuclideanDistanceMeasure();
		DistanceMeasure measure = new EuclideanDistanceMeasure();
		Path outGlobPath = new Path(outPath, "clusters-*-final");
		Path clusteredPointsPath = new Path(clusteredPoints);
		conf.set(DISTANCE_MEASURE_KEY, measure.getClass().getName());
		// conf.set(STATE_IN_KEY, "tmp/representative/representativePoints-0");
		conf.set(RepresentativePointsDriver.STATE_IN_KEY,
				"tmp/representative/representativePoints-" + numIters);
		// ClusterEvaluator clusterEvaluator = new ClusterEvaluator(conf,
		// clusteredPointsPath);
		// System.out.println("interClusterDensity()"
		// + clusterEvaluator.interClusterDensity());//
		clusters = loadClusters(conf, outGlobPath);
		if (clusters == null) {
			System.out.println("Wrong!");
		}
		System.out
				.printf("Dumping out clusters from clusters: %s and clusteredPoints: %s\n",
						outGlobPath, clusteredPointsPath);
		ClusterDumper clusterDumper = new ClusterDumper(outGlobPath,
				clusteredPointsPath);
		// clusterDumper.printClusters(null);// format: 1.0 :
											// [distance=1.8639943406835715]: 4
											// = [5.100, 2.500, 3.000, 1.100]
		displayCluster(clusterDumper, measure);
		// displayStd(clusters);
		calculateStd(clusterDumper, measure);

		/*
		 * Map<Integer, List<VectorWritable>> representativePoints = new
		 * TreeMap<Integer, List<VectorWritable>>(); ClusterEvaluator
		 * clusterEvaluator = new ClusterEvaluator( representativePoints,
		 * loadClusters(clusterDumper), measure); Double interClusterDensity =
		 * clusterEvaluator.interClusterDensity();
		 * System.out.println("interClusterDesity:" + interClusterDensity);//
		 * may // not
		 */// work(modified
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

	public static void displayCluster(ClusterDumper clusterDumper,
			DistanceMeasure measure)
			throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(
				"/home/mansi/Desktop/result_display.txt", true));
		out.println("x y z group");
		System.out.println(intraClusterDensity(clusterDumper, measure));

		/*
		 * for (WeightedVectorWritable point : clusterDumper
		 * .getClusterIdToPoints().get(center)) { Vector v = point.getVector();
		 * String output = v.get(0) + " " + v.get(1) + " " + v.get(2) + " " +
		 * center; System.out.println(output); } }
		 */
		out.close();
	}

	public static double intraClusterDensity(ClusterDumper clusterDumper,
			DistanceMeasure measure) {
		double avgDensity = 0;
		int count = 0;
		for (Element elem : intraClusterDensities(clusterDumper, measure)
				.nonZeroes()) {
			double value = elem.get();
			if (!Double.isNaN(value)) {
				avgDensity += value;
				count++;
			}
		}
		avgDensity = avgDensity / count; // clusters.isEmpty() ? 0 : avgDensity
											// / count;
		log.info("Average Intra-Cluster Density = {}", avgDensity);
		return avgDensity;
	}

	/*
	 * public static double meanSquaredError(){
	 * 
	 * 
	 * 
	 * }
	 */



	public static Vector intraClusterDensities(ClusterDumper clusterDumper,
			DistanceMeasure measure) {
		Vector densities = new RandomAccessSparseVector(Integer.MAX_VALUE);
		int count;
		double max;
		double min;
		double sum;
		Iterator<Integer> keys = clusterDumper.getClusterIdToPoints().keySet()
				.iterator();
		while (keys.hasNext()) {
			Integer center = keys.next();
			count = 0;
			max = Double.NEGATIVE_INFINITY;
			min = Double.POSITIVE_INFINITY;
			sum = 0;
			List<WeightedPropertyVectorWritable> pointsInCluster = clusterDumper
					.getClusterIdToPoints().get(center);
			for (int i = 0; i < pointsInCluster.size(); i++) {
				for (int j = i + 1; j < pointsInCluster.size(); j++) {
					Vector v1 = pointsInCluster.get(i).getVector();
					Vector v2 = pointsInCluster.get(j).getVector();
					double d = measure.distance(v1, v2);
					min = Math.min(d, min);
					max = Math.max(d, max);
					sum += d;
					count++;
				}
			}
			double density = (sum / count - min) / (max - min);
			densities.set(center, density);
			log.info("Intra-Cluster Density[{}] = {}", center, density);
			System.out.println(sum / count);
		}
		return densities;

	}

	/*
	 * public static void displayStd(ClusterDumper clusterDumper,
	 * DistanceMeasure measure) { // calculateStd(clusterDumper,measure,keys);
	 * 
	 * }
	 */
	/*
	 * calculate mean squared error
	 */
	public static void calculateStd(ClusterDumper clusterDumper,
			DistanceMeasure measure) {
		// DistanceMeasure measure2 = new SquaredEuclideanDistanceMeasure();
		int count;
		int totalCount = 0;
		double sum;
		double total = 0;
		Iterator<Integer> keys = clusterDumper.getClusterIdToPoints().keySet()
				.iterator();
		while (keys.hasNext()) {
			Integer center = keys.next();
			Vector centroidVector = new RandomAccessSparseVector(
					Integer.MAX_VALUE);
			count = 0;
			sum = 0;
			List<WeightedPropertyVectorWritable> pointsInCluster = clusterDumper
					.getClusterIdToPoints().get(center);
			centroidVector = pointsInCluster.get(0).getVector();
			count++;
			/* calculate the centroid of cluster */
			for (int i = 1; i < pointsInCluster.size(); i++) {

				Vector v1 = pointsInCluster.get(i).getVector();

				centroidVector = centroidVector.plus(v1);
				count++;
			}
			centroidVector = centroidVector.divide(count);
			/* calculate mean squared error */
			for (int i = 0; i < pointsInCluster.size(); i++) {

				Vector v1 = pointsInCluster.get(i).getVector();
				double d = measure.distance(v1, centroidVector);
				// double d2 = measure2.distance(v1, centroidVector);
				sum += d;
				// System.out.println("  d:" + d + "  d2:" + d2 + "  centorid:"
				// + centroidVector + "  v1:" + v1);
			}
			System.out.println("count" + count + "  ID: " + center + "  "
					+ sum / count);
			total += sum;
			totalCount += count;
		}
		System.out.println("total:  " + total + "   totalCount: " + totalCount
				+ "  mse:  " + total / totalCount);

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
	private static List<Cluster> loadClusters(Configuration conf,
			Path clustersIn) {
		List<Cluster> clusters = Lists.newArrayList();
		for (ClusterWritable clusterWritable : new SequenceFileDirValueIterable<ClusterWritable>(
				clustersIn, PathType.LIST, PathFilters.logsCRCFilter(), conf)) {
			System.out.println("test");
			Cluster cluster = clusterWritable.getValue();
			clusters.add(cluster);
		}
		System.out.println("Clusters: " + clusters);
		return clusters;
	}

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
	/*
	 * public Map<Integer, Vector> interClusterDistances() { Map<Integer,
	 * Vector> distances = new TreeMap<Integer, Vector>(); for (int i = 0; i <
	 * clusters.size(); i++) { Cluster clusterI = clusters.get(i); //
	 * clusters.get(i) RandomAccessSparseVector row = new
	 * RandomAccessSparseVector( Integer.MAX_VALUE);
	 * distances.put(clusterI.getId(), row); for (int j = i + 1; j <
	 * clusters.size(); j++) { Cluster clusterJ = clusters.get(j); double d =
	 * measure.distance(clusterI.getCenter(), clusterJ.getCenter());
	 * row.set(clusterJ.getId(), d); } } return distances; }
	 */

}

