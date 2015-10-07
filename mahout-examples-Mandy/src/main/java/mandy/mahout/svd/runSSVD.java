package mandy.mahout.svd;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.math.hadoop.stochasticsvd.SSVDSolver;
import org.mandy.mymahout.cluster09.ReadAndWriteSeq;
import org.mandy.mymahout.hdfs.HdfsDAO;
import java.io.IOException;

public class runSSVD {
	private static final String HDFS = "hdfs://ajila-server-01:54310";

	public static void main(String[] args) throws IOException {
		String jobTracker = "ajila-server-01:54311";
		String localFile = "datafile/eric_data1.txt";
		String inPath = HDFS + "/user/hdfs/mix_data";
		String inFile = inPath + "/eric_data1.txt";
		String outPath = inPath + "/result-ssvd/seq/";
		Configuration conf = new Configuration();
		HdfsDAO hdfs = new HdfsDAO(HDFS, conf);
		hdfs.rmr(inPath);
		hdfs.mkdirs(inPath);
		hdfs.copyFile(localFile, inPath);
		hdfs.ls(inPath);
		ReadAndWriteSeq.readAndWriteSeq(inFile, outPath, jobTracker, conf);
		runSSVDOnVectors(inFile, outPath + "ssvd-result", 4, 1, 30000, 1);

	}

	private static void runSSVDOnVectors(String inputPath, String outputPath,
	// inputPath should point to a DistributedRowMatrix
			int rank, int oversampling, int blocks, int reduceTasks)
			throws IOException {
		Configuration conf = new Configuration();
		// get number of reduce tasks from config?
		SSVDSolver solver = new SSVDSolver(conf, new Path[] { new Path(
				inputPath) }, new Path(outputPath), blocks, rank, oversampling,
				reduceTasks);
		solver.setcUHalfSigma(true);
		solver.setcVHalfSigma(true);
		solver.run();
	}
}
