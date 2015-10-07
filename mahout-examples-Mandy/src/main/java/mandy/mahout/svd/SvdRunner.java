package mandy.mahout.svd;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.math.hadoop.decomposer.DistributedLanczosSolver;
import org.apache.mahout.math.hadoop.decomposer.EigenVerificationJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SVD runner use to run svd algorithm on input like:<br>
 * 1.1,2.4,1,4.3,...<br>
 * ...<br>
 * and reduce dimension<br>
 * There are three jobs in this section:<br>
 * (1) prepare the text input to vectors which the second job needed;<br>
 * (2) do the DistributedLanczosSolver job ,which is the same as in mahout;<br>
 * (3) dimension reduction : transform the input to the reduced output;<br>
 * 
 * @author fansy
 * 
 */
public class SvdRunner extends AbstractJob {

	private static final String PREPAREVECTORPATH = "/prepareVector";
	private static final String TRANSFORMEDVECTOR = "/transformedVector";
	private Map<String, List<String>> parsedArgs;

	private static final Logger log = LoggerFactory.getLogger(SvdRunner.class);

	@Override
	public int run(String[] args) throws Exception {
		if (prepareArgs(args) != 0) {
			return -1;
		}

		/*
		 * prepare vectors job
		 */
		log.info("prepare Vector job begins...");
		String inputPath = AbstractJob.getOption(parsedArgs, "--input");
		String outputPath = AbstractJob.getOption(parsedArgs, "--tempDir")
				+ SvdRunner.PREPAREVECTORPATH;
		String regex = ",";
		if (AbstractJob.getOption(parsedArgs, "--splitterPattern") != null) {
			regex = AbstractJob.getOption(parsedArgs, "--splitterPattern");
		}
		String column = AbstractJob.getOption(parsedArgs, "--numCols");
		String[] job1Args = new String[] { "-i", inputPath, "-o", outputPath,
				"-regex", regex, "-nc", column };
		int job1Result = ToolRunner.run(getConf(), new PrepareSvdVector(),
				job1Args);
		if (job1Result != 0) {
			return -1;
		}

		log.info("svd algorithm job begins...");
		// replace the input
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i") || args[i].equals("--input")) {
				args[i + 1] = AbstractJob.getOption(parsedArgs, "--tempDir")
						+ SvdRunner.PREPAREVECTORPATH;
				break;
			}
		}
		int job2Result = ToolRunner.run(new DistributedLanczosSolver().job(),
				args);
		if (job2Result != 0) {
			return -1;
		}

		log.info("transform job begins...");
		inputPath = outputPath;
		outputPath = AbstractJob.getOption(parsedArgs, "--output")
				+ SvdRunner.TRANSFORMEDVECTOR;
		String eigenPath = AbstractJob.getOption(parsedArgs, "--output") + "/"
				+ EigenVerificationJob.CLEAN_EIGENVECTORS;
		String[] job3Args = new String[] { "-i", inputPath, "-o", outputPath,
				"-nc", column, "-e", eigenPath };
		int job3Result = ToolRunner.run(getConf(), new SvdReductionTransform(),
				job3Args);
		if (job3Result != 0) {
			return -1;
		}
		return 0;
	}

	/**
	 * prepare arguments
	 * 
	 * @param args
	 *            : input arguments
	 * @return 0 if nothing wrong;
	 * @throws IOException
	 */
	private int prepareArgs(String[] args) throws IOException {
		addInputOption();
		addOutputOption();
		addOption("numRows", "nr", "Number of rows of the input matrix");
		addOption("numCols", "nc", "Number of columns of the input matrix");
		addOption("rank", "r",
				"Desired decomposition rank (note: only roughly 1/4 to 1/3 "
						+ "of these will have the top portion of the spectrum)");
		addOption("symmetric", "sym",
				"Is the input matrix square and symmetric?");
		addOption(
				"workingDir",
				"wd",
				"Working directory path to store Lanczos basis vectors "
						+ "(to be used on restarts, and to avoid too much RAM usage)");
		// options required to run cleansvd job
		addOption(
				"cleansvd",
				"cl",
				"Run the EigenVerificationJob to clean the eigenvectors after SVD",
				false);
		addOption("maxError", "err", "Maximum acceptable error", "0.05");
		addOption("minEigenvalue", "mev",
				"Minimum eigenvalue to keep the vector for", "0.0");
		addOption("inMemory", "mem",
				"Buffer eigen matrix into memory (if you have enough!)",
				"false");

		addOption("splitterPattern", "regex",
				"the char used to split the input text   Default Value:"
						+ " \",\" ", false);
		this.parsedArgs = parseArguments(args);
		if (this.parsedArgs == null) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * SvdRunner main
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Configuration conf = new Configuration();// mandy
		// conf.set("mapred.job.tracker", "ajila-server-01:54311");// mandy
		/*
		 * String HDFS = "hdfs://ajila-server-01:54310";
		 * 
		 * String inPath = HDFS + "/user/hdfs/mix_data"; String inFile = inPath
		 * + "/eric_data1.txt"; String outPath = inPath + "/result-svd/seq/";
		 */
		// ToolRunner.run(conf, new SvdRunner(), args);
		ToolRunner.run(new Configuration(), new SvdRunner(), args);
	}
}

