package org.mandy.mymahout.cluster09;

import org.apache.hadoop.conf.Configuration;
import org.apache.mahout.math.hadoop.decomposer.DistributedLanczosSolver;
import org.mandy.mymahout.hdfs.HdfsDAO;


public class RunSvd {

	/**
	 * call svd algorithm
	 * 
	 * @throws Exception
	 */
	private static final String HDFS = "hdfs://ajila-server-01:54310";
	public static void main(String[] args) throws Exception {

		String jobTracker = "ajila-server-01:54311";
		String localFile = "datafile/eric_data1.txt";
		// String localFile = "datafile/iris.txt";
		String inPath = HDFS + "/user/hdfs/mix_data";
		String inFile = inPath + "/eric_data1.txt";//
		String outPath = inPath + "/result-svd/seq/";
		Configuration conf = new Configuration();
		HdfsDAO hdfs = new HdfsDAO(HDFS, conf);
		hdfs.rmr(inPath);
		hdfs.mkdirs(inPath);
		hdfs.copyFile(localFile, inPath);
		hdfs.ls(inPath);
		ReadAndWriteSeq.readAndWriteSeq(inFile, outPath, jobTracker, conf);

		/*
		 * String[] arg = new String[] { "-jt", "ajila-server-01:54311", "-fs",
		 * "ajila-server-01:54310", "-i", outPath, "-o",
		 * inPath+"/result-svd/final/", "-nr", "60128", "-nc", "17", "-r", "3",
		 * "-sym", "square", "--cleansvd", "true", "--tempDir", HDFS+"/svd/temp"
		 * };
		 */
		String[] arg = new String[] { "-jt", "ajila-server-01:54311", "-fs",
				"ajila-server-01:54310", "-i", outPath, "-o",
				inPath + "/result-svd/final/", "-nr", "60128", "-nc", "14",
				"-r", "3", "--cleansvd", "true", "--tempDir",
				HDFS + "/svd/temp" };
		DistributedLanczosSolver.main(arg);
	}

}
