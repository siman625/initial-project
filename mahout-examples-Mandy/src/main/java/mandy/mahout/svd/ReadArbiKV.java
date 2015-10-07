package mandy.mahout.svd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

//import org.apache.crunch.CrunchRuntimeException;
//import org.apache.crunch.types.writable.WritableDeepCopier;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;

public class ReadArbiKV {

	/**
	 * ????<key,value>????
	 */
	public static Configuration conf = new Configuration();
	// public static WritableDeepCopier<Writable> wdc;
	static String fPath = "";
	static String trainPath = "";
	static {
		conf.set("mapred.job.tracker", "ubuntu:9001");
		fPath = "hdfs://ubuntu:9000/home/mahout/mahout-work-mahout/labelindex"; // ????
	}

	public static void main(String[] args) throws IOException {
		readFromFile(fPath);
		// readFromFile(trainPath);
	}

	/**
	 * ??????
	 * 
	 * @param fPath
	 * @return
	 * @throws IOException
	 */
	public static Map<Writable, Writable> readFromFile(String fPath)
			throws IOException {
		FileSystem fs = FileSystem.get(URI.create(fPath), conf);
		Path path = new Path(fPath);
		Map<Writable, Writable> map = new HashMap<Writable, Writable>();
		SequenceFile.Reader reader = null;
		try {
			reader = new SequenceFile.Reader(fs, path, conf);
			Writable key = (Writable) ReflectionUtils.newInstance(
					reader.getKeyClass(), conf);
			Writable value = (Writable) ReflectionUtils.newInstance(
					reader.getValueClass(), conf);
			@SuppressWarnings("unchecked")
			Class<Writable> writableClassK = (Class<Writable>) reader
					.getKeyClass();
			@SuppressWarnings("unchecked")
			Class<Writable> writableClassV = (Class<Writable>) reader
					.getValueClass();
			while (reader.next(key, value)) {
				// Writable k=; // ????Writable??????
				Writable k = deepCopy(key, writableClassK); // Writable ?????
				Writable v = deepCopy(value, writableClassV);
				map.put(k, v);
				// System.out.println(key.toString()+", "+value.toString());
				// System.exit(-1);// ????????
			}
		} finally {
			IOUtils.closeStream(reader);
		}
		return map;
	}

	/**
	 * Writable ????? ??WritableDeepCopier
	 * 
	 * @param fPath
	 * @return
	 * @throws IOException
	 */
	public static Writable deepCopy(Writable source,
			Class<Writable> writableClass) {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOutStream);
		Writable copiedValue = null;
		try {
			source.write(dataOut);
			dataOut.flush();
			ByteArrayInputStream byteInStream = new ByteArrayInputStream(
					byteOutStream.toByteArray());
			DataInput dataInput = new DataInputStream(byteInStream);
			copiedValue = writableClass.newInstance();
			copiedValue.readFields(dataInput);
		} catch (Exception e) {
			// throw new CrunchRuntimeException("Error while deep copying " +
			// source, e);
		}
		return copiedValue;
	}

}

