package org.example.basicapp;

import java.util.List;
import java.util.Map;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.SendTaskFailureRequest;
import com.amazonaws.services.stepfunctions.model.SendTaskSuccessRequest;

//import org.apache.http.auth.Credentials;

/**
 * Hello world!
 *
 */

public class App {
    private static String bucket_name = "shankha-batch-jobs-160821";

    public static void main(String[] args) {
        System.out.println("Hello World!");

        Map<String, String> env = System.getenv();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        String taskToken = env.get("TASK_TOKEN");
        System.out.println("task token " + taskToken);

        System.out.format("Objects in S3 bucket %s:\n", bucket_name);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
        ListObjectsV2Result result = s3.listObjectsV2(bucket_name);
        List<S3ObjectSummary> objects = result.getObjectSummaries();

        ClientConfiguration clientConfiguration = new ClientConfiguration();
        //clientConfiguration.setSocketTimeout((int) TimeUnit.SECONDS.toMillis(70));
       
        AWSStepFunctions client = AWSStepFunctionsClientBuilder.standard().withRegion(Regions.US_EAST_2)
                //.withCredentials(new EnvironmentVariableCredentialsProvider())
                //.withCredentials(st)
                .withClientConfiguration(clientConfiguration).build();

        boolean myfile = false;
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
            if (os.getKey().equals("myfile")) {
                System.out.println("my file exists");
                myfile = true;
            } 
        }

        if(myfile) {
            System.out.println("my file exists. Sending task failure");
            SendTaskFailureRequest failureRequest = new SendTaskFailureRequest();
            failureRequest.setCause("{}");
            failureRequest.setTaskToken(taskToken);
            client.sendTaskFailure(failureRequest);
        } else {
            System.out.println("my file exists. Sending task success");
            SendTaskSuccessRequest successRequest = new SendTaskSuccessRequest();
            successRequest.setOutput("{}");
            successRequest.setTaskToken(taskToken);
            client.sendTaskSuccess(successRequest);
        }

    }
}
