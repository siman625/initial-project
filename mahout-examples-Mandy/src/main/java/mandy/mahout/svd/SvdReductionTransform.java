package mandy.mahout.svd;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Dimension Reduction<br>
 * 
 * the last job to transform the input to the right one
 * 
 * @author fansy
 * 
 */
public class SvdReductionTransform extends AbstractJob {
	private final static String EIGENPATH = "/eigenPath";
	private final static String VECTORCOLUMN = "vectorColumn";
	private static final Logger log = LoggerFactory
			.getLogger(SvdReductionTransform.class);

	@Override
	public int run(String[] args) throws Exception {
		addInputOption();
		addOutputOption();
		addOption("numCols", "nc", "Number of columns of the input matrix");
		addOption("eigenPath", "e", "eigen vectors path");

		if (parseArguments(args) == null) {
			return -1;
		}
		Path input = getInputPath();
		Path output = getOutputPath();
		String eigenPath = getOption("eigenPath");
		String column = getOption("numCols");

		Configuration conf = new Configuration(getConf() != null ? getConf()
				: new Configuration());
		conf.set(EIGENPATH, eigenPath);
		try {
			int col = Integer.parseInt(column);
			conf.setInt(VECTORCOLUMN, col);
		} catch (Exception e) {
			return -2; // format exception:-2
		}

		log.info("delete file " + output);
		HadoopUtil.delete(conf, output); // delete output

		Job job = new Job(conf, "prepare svd vector from " + input.toUri());
		job.setJarByClass(SvdReductionTransform.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(VectorWritable.class);

		SequenceFileInputFormat.addInputPath(job, input);
		SequenceFileOutputFormat.setOutputPath(job, output);

		job.setMapperClass(TransMapper.class);
		job.setNumReduceTasks(0);

		boolean succeeded = job.waitForCompletion(true);
		if (!succeeded) {
			throw new IllegalStateException("Job failed!");
		}
		return 0;
	}

	public static class TransMapper extends
			Mapper<LongWritable, VectorWritable, NullWritable, VectorWritable> {
		List<Vector> list = Lists.newArrayList();
		int column;
		int transCol;

		@Override
		public void setup(Context cxt) throws IOException {
			log.info("in the first row in setup()");
			column = cxt.getConfiguration().getInt(VECTORCOLUMN, -1);
			String eigenPath = cxt.getConfiguration().get(EIGENPATH);
			log.info("eigenPath:" + eigenPath);
			log.info("cxt.getConfiguration().get(\"mapred.job.tracker\")"
					+ cxt.getConfiguration().get("mapred.job.tracker"));
			Map<Writable, Writable> eigenMap = null;
			try {
				eigenMap = ReadArbiKV.readFromFile(eigenPath);// , cxt
				// .getConfiguration().get("mapred.job.tracker"));
			} catch (Exception e) {
				log.info("???????");
				// e.printStackTrace();
			}
			Iterator<Entry<Writable, Writable>> eigenIter = eigenMap.entrySet()
					.iterator();
			// initial eigen vectors
			while (eigenIter.hasNext()) {
				Map.Entry<Writable, Writable> set = eigenIter.next();
				VectorWritable eigenV = (VectorWritable) set.getValue();
				if (eigenV.get().size() == column) {
					list.add(eigenV.get());
				}
			}
			log.info("the last row in setup()" + list.size());
			transCol = list.size();
		}

		@Override
		public void map(LongWritable key, VectorWritable value, Context cxt)
				throws IOException, InterruptedException {

			Vector transVector = new DenseVector(transCol);
			for (int i = 0; i < transCol; i++) {
				double d = value.get().dot(list.get(i)); // dot multiply
				transVector.setQuick(i, d);
			}
			VectorWritable vector = new VectorWritable(transVector);
			cxt.write(NullWritable.get(), vector);
		}
	}

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new Configuration(), new SvdReductionTransform(), args);
	}
}

