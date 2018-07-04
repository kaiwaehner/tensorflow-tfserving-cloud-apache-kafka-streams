# This project is work in progress! No usable results yet...

<span style="color:red">**Please go to the Github project "[TensorFlow Serving + gRPC + Java + Kafka Streams](https://github.com/kaiwaehner/tensorflow-serving-java-grpc-kafka-streams)" to see a working example with TensorFlow Serving and Kafka Streams.**</span>



### NO WORKING CODE YET

#### GCP Example: TensorFlow Model deployed to Google ML Engine for interence in Kafka Streams Application on Kubernetes 
This project contains a demo to do model inference with Apache Kafka, Kafka Streams and a TensorFlow model deployed using [TensorFlow Serving](https://www.tensorflow.org/serving/) (leveraging [Google Cloud ML Engine](https://cloud.google.com/ml-engine/docs/tensorflow/deploying-models) in this example). The concepts are very similar for other ML frameworks and Cloud Providers, e.g. you could also use Apache MXNet and [AWS model server](https://github.com/awslabs/mxnet-model-server).

## TensorFlow Serving (using Google Cloud ML Engine)
The blog post "[How to deploy TensorFlow models to production using TF Serving](https://medium.freecodecamp.org/how-to-deploy-tensorflow-models-to-production-using-tf-serving-4b4b78d41700)" is a great explanation of how to export and deploy trained TensorFlow models to a TensorFlow Serving infrastructure. You can either deploy your own infrastructure anywhere or leverage a cloud service like Google Cloud ML Engine. A [SavedModel](https://www.tensorflow.org/programmers_guide/saved_model#build_and_load_a_savedmodel) is TensorFlow's recommended format for saving models, and it is the required format for deploying trained TensorFlow models using TensorFlow Serving or deploying on Goodle Cloud ML Engine

Things to do:
1. Create Cloud ML Engine
2. Deploy prebuilt TensorFlow Model
3. Create Kafka Cluster on GCP using Confluent Cloud
4. Implement Kafka Streams application
5. Deploy Kafka Streams application (e.g. to Google Kubernetes Engine)
6. Generate streaming data to test the combination of Kafka Streams and Google Cloud ML Engine


### Step 1: Create a TensorFlow model and export it to 'SavedModel' format.
I simply added an existing pretrained Image Recognition model built with TensorFlow (Inception V1). 

I also created a new model for predictions of census using the "[ML Engine getting started guide](https://cloud.google.com/ml-engine/docs/tensorflow/getting-started-training-prediction)". The data for training is in 'data' folder.

### Step 2: Deploy model to Google ML Engine
[Getting Started with Google ML Engine](https://cloud.google.com/ml-engine/docs/tensorflow/deploying-models)

### Step 3: Create Kafka Cluster using GCP Confluent Cloud
[Confluent Cloud - Apache Kafka as a Service](https://www.confluent.io/confluent-cloud/)

### TODO Implement and deploy Streams app
Issues today: Connection to Gooogle ML Engine from GCP Java APIs is not as easy as expected. Examples and demos are not self-explanatory and not working well. Much more trouble than using local TensorFlow Serving server. (also because of missing experience with GCP in general!)


