package org.mandy.mymahout.recommendation;

import org.apache.hadoop.mapred.JobConf;
import org.apache.mahout.cf.taste.hadoop.item.RecommenderJob;
import org.mandy.mymahout.hdfs.HdfsDAO;
public class ItemCFHadoop {
	private static final String HDFS = "hdfs://localhost:54310";

	public static void main(String[] args) throws Exception {
		String localFile = "datafile/item.csv";
		String inPath = HDFS + "/user/hdfs/userCF";
		String inFile = inPath + "/item.csv";
		String outPath = HDFS + "/user/hdfs/userCF/result/";
		String outFile = outPath + "/part-r-00000";
		String tmpPath = HDFS + "/tmp/" + System.currentTimeMillis();
		JobConf conf = config();
		// conf.set("mapred.job.tracker", "localhost:54311");
		// conf.set("fs.default.name", "hdfs://localhost:54310");
		HdfsDAO hdfs = new HdfsDAO(HDFS, conf);
		hdfs.rmr(inPath);
		hdfs.mkdirs(inPath);
		hdfs.copyFile(localFile, inPath);
		hdfs.ls(inPath);
		hdfs.cat(inFile);
		StringBuilder sb = new StringBuilder();
		sb.append("--input ").append(inPath);
		sb.append(" --output ").append(outPath);
		sb.append(" --booleanData true");
		sb.append(" --similarityClassname org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.EuclideanDistanceSimilarity");
		sb.append(" --tempDir ").append(tmpPath);
		args = sb.toString().split(" ");
		RecommenderJob job = new RecommenderJob();
		job.setConf(conf);
		job.run(args);
		hdfs.cat(outFile);
	}

	public static JobConf config() {
		JobConf conf = new JobConf(ItemCFHadoop.class);
		conf.setJobName("ItemCFHadoop");
		conf.addResource("classpath:/hadoop/core-site.xml");
		conf.addResource("classpath:/hadoop/hdfs-site.xml");
		conf.addResource("classpath:/hadoop/mapred-site.xml");
		return conf;
	}
}