package com.github.megachucky.kafka.streams.machinelearning;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;


/**
 * @author Kai Waehner (www.kai-waehner.de)
 * 
 *         Creates a new Kafka Streams application for Image Recognition. 
 *         The application uses the CNN model "inception5h" (built with
 *         TensorFlow) to infer messages sent to Kafka topic "ImageInputTopic".
 *         The outcome of model inference is sent to Kafka topic
 *         "ImageOutputTopic".
 *
 */
public class Kafka_Streams_TensorFlow_Serving_gRPC_Image_Recognition_Example {

	private static final String imageInputTopic = "ImageInputTopic";
	private static final String imageOutputTopic = "ImageOutputTopic";
	
	
	// Prediction Value
	private static String imageClassification = "unknown";
	private static String imageProbability = "unknown";

	public static void main(final String[] args) throws Exception {
			
			// Create TensorFlow object
		
//		    String modelDir = "src/main/resources/generatedModels/CNN_inception5h";
		    
//		    Path pathGraph = Paths.get(modelDir, "tensorflow_inception_graph.pb");
//		    byte[] graphDef = Files.readAllBytes(pathGraph);
		    		    
//		    Path pathModel = Paths.get(modelDir, "imagenet_comp_graph_label_strings.txt");
//		    List<String> labels = Files.readAllLines(pathModel, Charset.forName("UTF-8"));
		      
//			final List<Map.Entry<String, Double>> list; 

			// Configure Kafka Streams Application
			final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
			final Properties streamsConfiguration = new Properties();
			// Give the Streams application a unique name. The name must be unique
			// in the Kafka cluster
			// against which the application is run.
			streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-tensorflow-image-recognition-example");
			// Where to find Kafka broker(s).
			streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			
		
			// Specify default (de)serializers for record keys and for record
			// values.
			streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
			streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
			
			// In the subsequent lines we define the processing topology of the
			// Streams application.
			final KStreamBuilder builder = new KStreamBuilder();

			// Construct a `KStream` from the input topic "ImageInputTopic", where
			// message values represent lines of text 
			final KStream<String, String> imageInputLines = builder.stream(imageInputTopic);

			
			// Stream Processor (in this case 'foreach' to add custom logic, i.e. apply the analytic model)
			imageInputLines.foreach((key, value) -> {
				
				System.out.println("Start of new message");
			    
//			        System.out.println("Invalid args");
//			        System.out.println("Usage: <host:port> <image>");
//			        System.out.println("\tExample: localhost:9090 ~/Pictures/cat.jpg");
//			        
			      

			      String server = "localhost";
			      int port = 9090;
			      String imagePath = "src/main/resources/TensorFlow_Images/dog.jpg";

			      TensorflowObjectRecogniser recogniser = new TensorflowObjectRecogniser(server, port);

			      System.out.println("Image = " + imagePath);
			      InputStream jpegStream;
				try {
					jpegStream = new FileInputStream(imagePath);
				
				List<Map.Entry<String, Double>> list;
			      list = recogniser
			          .recognise(jpegStream);
			      System.out.println("LIST:");
			      System.out.println(list);
			      recogniser.close();
			      jpegStream.close();
			      
					KStream<String, Object> transformedMessage = imageInputLines
							.mapValues(value2 -> "Prediction: What is the content of this picture? => " + list);
				
					// Send prediction information to Output Topic
					transformedMessage.to(imageOutputTopic);
				
				
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
				
				
				

//					imageClassification = "unknown";
//					imageProbability = "unknown";
//					
//					String imageFile = value;

//				    Path pathImage = Paths.get(imageFile);
//				    byte[] imageBytes;
//					try {
//						imageBytes = Files.readAllBytes(pathImage);
						
//						try (Tensor image = constructAndExecuteGraphToNormalizeImage(imageBytes)) {
//						      float[] labelProbabilities = executeInceptionGraph(graphDef, image);
//						      int bestLabelIdx = maxIndex(labelProbabilities);
//						      
//						      
//						      imageClassification = labels.get(bestLabelIdx);
//						      
//						      imageProbability = Float.toString(labelProbabilities[bestLabelIdx] * 100f);
//						      
//						      System.out.println(
//						          String.format(
//						              "BEST MATCH: %s (%.2f%% likely)",
//						              imageClassification, labelProbabilities[bestLabelIdx] * 100f));
//						    }
//						
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//
				}
			);

	// airlineInputLines.print();

	// Transform message: Add prediction information
//	KStream<String, Object> transformedMessage = imageInputLines
//			.mapValues(value -> "Prediction: What is the content of this picture? => " + imageClassification + ", probability = " + imageProbability);


		
			


	// Start Kafka Streams Application to process new incoming images from the Input Topic
	final KafkaStreams streams = new KafkaStreams(builder,
			streamsConfiguration);
	
	streams.cleanUp();
	
	streams.start();
	
	System.out.println("Image Recognition Microservice is running...");
	
	System.out.println("Input to Kafka Topic " + imageInputTopic + "; Output to Kafka Topic " + imageOutputTopic);

	// Add shutdown hook to respond to SIGTERM and gracefully close Kafka
	// Streams
	Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

	// ########################################################################################
	// Private helper class for construction and execution of the pre-built TensorFlow model
	// ########################################################################################

//	private static Tensor constructAndExecuteGraphToNormalizeImage(byte[] imageBytes) {
//		//Graph construction: using the OperationBuilder class to construct a graph to decode, resize and normalize a JPEG image.
//		
//		try (Graph g = new Graph()) {
//			GraphBuilder b = new GraphBuilder(g);
//			// Some constants specific to the pre-trained model at:
//			// https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip
//			//
//			// - The model was trained with images scaled to 224x224 pixels.
//			// - The colors, represented as R, G, B in 1-byte each were
//			// converted to
//			// float using (value - Mean)/Scale.
//			final int H = 224;
//			final int W = 224;
//			final float mean = 117f;
//			final float scale = 1f;
//
//			// Since the graph is being constructed once per execution here, we
//			// can use a constant for the
//			// input image. If the graph were to be re-used for multiple input
//			// images, a placeholder would
//			// have been more appropriate.
//			final Output input = b.constant("input", imageBytes);
//			final Output output = b
//					.div(b.sub(
//							b.resizeBilinear(b.expandDims(b.cast(b.decodeJpeg(input, 3), DataType.FLOAT),
//									b.constant("make_batch", 0)), b.constant("size", new int[] { H, W })),
//							b.constant("mean", mean)), b.constant("scale", scale));
//			try (Session s = new Session(g)) {
//				return s.runner().fetch(output.op().name()).run().get(0);
//			}
//		}
//	}
//
//	private static float[] executeInceptionGraph(byte[] graphDef, Tensor image) {
//		try (Graph g = new Graph()) {
//			
//			// Model loading: Using Graph.importGraphDef() to load a pre-trained Inception model.
//			g.importGraphDef(graphDef);
//			
//			// Graph execution: Using a Session to execute the graphs and find the best label for an image.
//			try (Session s = new Session(g);
//					Tensor result = s.runner().feed("input", image).fetch("output").run().get(0)) {
//				final long[] rshape = result.shape();
//				if (result.numDimensions() != 2 || rshape[0] != 1) {
//					throw new RuntimeException(String.format(
//							"Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
//							Arrays.toString(rshape)));
//				}
//				int nlabels = (int) rshape[1];
//				return result.copyTo(new float[1][nlabels])[0];
//			}
//		}
//	}
//
//	private static int maxIndex(float[] probabilities) {
//		int best = 0;
//		for (int i = 1; i < probabilities.length; ++i) {
//			if (probabilities[i] > probabilities[best]) {
//				best = i;
//			}
//		}
//		return best;
//	}
//
//	// In the fullness of time, equivalents of the methods of this class should
//	// be auto-generated from
//	// the OpDefs linked into libtensorflow_jni.so. That would match what is
//	// done in other languages
//	// like Python, C++ and Go.
//	static class GraphBuilder {
//		GraphBuilder(Graph g) {
//			this.g = g;
//		}
//
//		Output div(Output x, Output y) {
//			return binaryOp("Div", x, y);
//		}
//
//		Output sub(Output x, Output y) {
//			return binaryOp("Sub", x, y);
//		}
//
//		Output resizeBilinear(Output images, Output size) {
//			return binaryOp("ResizeBilinear", images, size);
//		}
//
//		Output expandDims(Output input, Output dim) {
//			return binaryOp("ExpandDims", input, dim);
//		}
//
//		Output cast(Output value, DataType dtype) {
//			return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().output(0);
//		}
//
//		Output decodeJpeg(Output contents, long channels) {
//			return g.opBuilder("DecodeJpeg", "DecodeJpeg").addInput(contents).setAttr("channels", channels).build()
//					.output(0);
//		}
//
//		Output constant(String name, Object value) {
//			try (Tensor t = Tensor.create(value)) {
//				return g.opBuilder("Const", name).setAttr("dtype", t.dataType()).setAttr("value", t).build().output(0);
//			}
//		}
//
//		private Output binaryOp(String type, Output in1, Output in2) {
//			return g.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0);
//		}
//
//		private Graph g;
//	}

}
