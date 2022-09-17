package com.programming.projectservice.controllers;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.RunTaskRequest;
import com.amazonaws.services.ecs.model.StartTaskRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.programming.projectservice.dto.FileUrlAndAwsEc2Instance;
import com.programming.projectservice.exceptions.DataNotFound;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.lang.String.join;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/aws")
@AllArgsConstructor
public class AwsController {


    private final static String bucketName = "projectmanagement-jar";

    /**
     * From AwsConfig
     */
    private AmazonS3 amazonS3;

    //private Ec2Client ec2Client;



    private AmazonEC2 ec2Client;


    private AmazonECS amazonECS;



    @PostMapping("/s3/upload")
    public ResponseEntity<Object> uploadFileToS3(@RequestParam("file") MultipartFile file){

        try{
            System.out.println("-----File----");
            System.out.println(file);
            System.out.println("-------------");

            //String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

            String fileName = UUID.randomUUID()+"_"+file.getOriginalFilename();


            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentLength(file.getSize());
            metaData.setContentType(file.getContentType());

            PutObjectResult request = amazonS3.putObject(bucketName,fileName,file.getInputStream(),metaData);

            String s3FileUrl = String.valueOf(amazonS3.getUrl(bucketName,fileName));

            System.out.println("File Name "+fileName);
            System.out.println("File Url "+s3FileUrl);


            String ipv4Address = createEc2(s3FileUrl,fileName);


            return new ResponseEntity<>(new FileUrlAndAwsEc2Instance(s3FileUrl,ipv4Address),HttpStatus.CREATED);

        }catch (AmazonServiceException | IOException | InterruptedException e) {
            throw new DataNotFound(e.getMessage());
        }
    }


    public String createEc2(String s3JarUrl,String s3FileName) throws InterruptedException {

        String userData = "";
        userData = userData + "#!/bin/bash -x" + "\n";
        userData = userData + "wget "+s3JarUrl+ "\n";
        userData = userData + "java -jar "+s3FileName+ "\n";
        String base64UserData;
        base64UserData = new String( Base64.encodeBase64( userData.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        ///var/log/cloud-init-output.log

        RunInstancesRequest run_request = new RunInstancesRequest()
                .withImageId("ami-0100acace5e3950f7")
                .withInstanceType(InstanceType.T2Micro)
                .withMaxCount(1)
                .withMinCount(1)
                .withKeyName("ec2_projectmanagement")
                .withSecurityGroupIds(
                        List.of("sg-037c055d78feea0e2"))
                .withUserData(base64UserData);

        //wget url
        //java -jar s3FileName


        RunInstancesResult run_response =  ec2Client.runInstances(run_request);

        String instanceId = run_response.getReservation().getInstances().get(0).getInstanceId();

        //TimeUnit.SECONDS.sleep(5);

        Thread.sleep(5000);

        String publicIpAddress = "";

        DescribeInstancesRequest request = new DescribeInstancesRequest().withFilters(new Filter("instance-id", List.of(instanceId)));

        DescribeInstancesResult response = ec2Client.describeInstances(request);

        List <Reservation> list  = response.getReservations();

        for (Reservation res:list) {
            List <Instance> instanceList= res.getInstances();

            for (Instance instance:instanceList){

                publicIpAddress = instance.getPublicIpAddress();

                System.out.println("Public IP :" + instance.getPublicIpAddress());
                System.out.println("Public DNS :" + instance.getPublicDnsName());
                System.out.println("Instance State :" + instance.getState());
                System.out.println("Instance TAGS :" + instance.getTags());
            }
        }




        return publicIpAddress;
    }




    /*
    public String deployDocker(String s3JarUrl,String s3FileName) throws InterruptedException {
        RunTaskRequest runTaskRequest = new RunTaskRequest();
        runTaskRequest.setLaunchType("FARGATE");
        runTaskRequest.setCluster("project-management");
        runTaskRequest.setTaskDefinition("new-task");
        runTaskRequest.setCount(1);

        StartTaskRequest startTaskRequest = new StartTaskRequest();
        startTaskRequest.setContainerInstances();

        amazonECS.startTask()

    }


     */
   /* @GetMapping("/ec2/create")
    public String createEc2(String s3JarUrl){
        RunInstancesRequest runInstancesRequest = RunInstancesRequest.builder()
                .imageId("ami-0100acace5e3950f7")
                .instanceType(InstanceType.T2_MICRO)
                .minCount(1)
                .maxCount(1)
                .build();


        RunInstancesResponse response = ec2Client.runInstances(runInstancesRequest);
        String instanceId = response.instances().get(0).instanceId();
        System.out.println(response);


        Tag tag = Tag.builder()
                .key("Name")
                .value("First ProjectManagement AMI")
                .build();

        CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                .resources(instanceId)
                .tags(tag)
                .build();

        try {
            ec2Client.createTags(tagRequest);
            System.out.printf(
                    "Successfully started EC2 Instance %s based on AMI",
                    instanceId, "ami-0100acace5e3950f7");



            return instanceId;

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
    }

    */



}
