package com.programming.projectservice.config;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AwsConfig {

    private final static String accessKeyID = "AKIA4WSQFS3IBRZA5W4N";
    private final static String secretAccessKey = "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";
    private final static String region = "eu-west-3";


    @Bean
    public AmazonS3 s3Client(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyID,secretAccessKey);
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region).build();
    }


    /*@Bean
    public Ec2Client ec2Client(){
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyID,secretAccessKey);
        return Ec2Client.builder()
                .region(Region.EU_WEST_3)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

     */

    @Bean
    public AmazonEC2 ec2Client(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyID,secretAccessKey);
        return AmazonEC2ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region).build();
    }


}
