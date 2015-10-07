package org.mandy.mymahout.cluster09;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

public class ReadAndWriteSeq {

	private ReadAndWriteSeq() {
	}

	/**
	 * read in data and write to HDFS, of which the format is
	 * <Writable,VectorWritable>
	 * 
	 * @param input
	 *            input text file
	 * @param output
	 *            output HDFS file(seq format)
	 * @param jobtracker
	 *            the address of jobtracker
	 * @param regex
	 *            parser class being used, used to parse text file
	 * @return if task succeed
	 * @throws IOException
	 */
	public static boolean readAndWriteSeq(String input, String output,
			String jobtracker, Configuration conf) throws IOException {
		boolean flag = true;

		// Configuration conf=new Configuration();
		conf.set("mapred.job.tracker", jobtracker);

		FileSystem fsIn = FileSystem.get(URI.create(input), conf);
		FileSystem fsOut = FileSystem.get(URI.create(output), conf);
		HadoopUtil.delete(conf, new Path(output));
		Path pathIn = new Path(input);
		Path pathOut = new Path(output);

		SequenceFile.Writer writer = null;
		FSDataInputStream in = fsIn.open(pathIn);
		IntWritable key = new IntWritable(0);
		VectorWritable value = new VectorWritable();
		try {
			writer = SequenceFile.createWriter(fsOut, conf, pathOut,
					key.getClass(), value.getClass());
			String line = null;
			int length = 0;
			int row = 1;
			while ((line = in.readLine()) != null) {
				String[] vs = line.split(" ");
				length = vs.length;
				Vector vector = new RandomAccessSparseVector(length);
				for (int i = 0; i < length; i++) {
					vector.set(i, Double.parseDouble(vs[i]));
				}
				value.set(vector);
				key.set(row++);
				writer.append(key, value);
			}
		} catch (IOException e) {
			flag = false;
		} finally {
			IOUtils.closeStream(writer);
			in.close();
		}
		return flag;
	}

}
