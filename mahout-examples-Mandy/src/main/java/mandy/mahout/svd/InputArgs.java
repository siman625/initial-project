package mandy.mahout.svd;


//import org.apache.hadoop.conf.Configuration;
//import org.apache.mahout.math.hadoop.decomposer.DistributedLanczosSolver;

import mandy.mahout.svd.SvdRunner;
public class InputArgs {
	public static void main(String[] args) throws Exception {
	//Configuration conf = new Configuration();// mandy
	//conf.set("mapred.job.tracker", "ajila-server-01:54311");// mandy
	
	  String HDFS = "hdfs://ajila-server-01:54310";
	  
	  String inPath = HDFS + "/user/hdfs/mix_data";
		// String inFile = inPath+ "/eric_data1.txt";
		// String outPath = inPath + "/result-svd/seq/";
		String[] arg = new String[] { "-i", inPath, "-o",
				inPath + "/result-svd/final0/", "-nr", "60128", "-nc", "17",
				"-r", "3", "--cleansvd", "true", "--tempDir",
				HDFS + "/svd/temp0" };
		SvdRunner.main(arg);

	}
	
}
