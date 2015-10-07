package mandy.mahout.svd;


import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

/**
 * 
 * prepare the input to vector which the svd algorithm needed
 * 
 * @author fansy
 * 
 */
public class PrepareSvdVector extends AbstractJob {
	private final static String VECTORREGEX = "vectorRegex";
	private final static String VECTORCOLUMN = "vectorColumn";

	@Override
	public int run(String[] args) throws Exception {
		addInputOption();
		addOutputOption();
		addOption("numCols", "nc", "Number of columns of the input matrix");
		addOption("splitterPattern", "regex",
				"the char used to split the input text   Default Value:"
						+ " \",\" ", false);

		if (parseArguments(args) == null) {
			return -1;
		}
		Path input = getInputPath();
		Path output = getOutputPath();
		String regex = getOption("splitterPattern");
		String column = getOption("numCols");

		Configuration conf = new Configuration(getConf() != null ? getConf()
				: new Configuration());
		conf.set(VECTORREGEX, regex);
		try {
			int col = Integer.parseInt(column);
			conf.setInt(VECTORCOLUMN, col);
		} catch (Exception e) {
			return -2; // format exception:-2
		}
		HadoopUtil.delete(conf, output); // delete output

		Job job = new Job(conf, "prepare svd vector from " + input.toUri());
		job.setJarByClass(PrepareSvdVector.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setInputFormatClass(TextInputFormat.class);

		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(VectorWritable.class);

		FileInputFormat.addInputPath(job, input);
		SequenceFileOutputFormat.setOutputPath(job, output);

		job.setMapperClass(PrePareMapper.class);
		job.setNumReduceTasks(0);

		boolean succeeded = job.waitForCompletion(true);
		if (!succeeded) {
			throw new IllegalStateException("Job failed!");
		}

		return 0;
	}

	public static class PrePareMapper extends
			Mapper<LongWritable, Text, LongWritable, VectorWritable> {
		Pattern pattern;
		int column;

		@Override
		public void setup(Context cxt) {
			String regex = cxt.getConfiguration().get(VECTORREGEX);
			pattern = Pattern.compile(regex, 0);
			column = cxt.getConfiguration().getInt(VECTORCOLUMN, -1);
		}

		@Override
		public void map(LongWritable key, Text value, Context cxt)
				throws IOException, InterruptedException {
			String[] values = pattern.split(value.toString());
			int len = values.length;
			if (column != len) { // arguments wrong
				return;
			}
			Vector v = new DenseVector(column);
			for (int i = 0; i < len; i++) {
				try {
					v.setQuick(i, Double.parseDouble(values[i]));
				} catch (Exception e) {
					return;
				}
			}
			VectorWritable vector = new VectorWritable(v);
			cxt.write(key, vector);
		}
	}

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new Configuration(), new PrepareSvdVector(), args);
	}
}
