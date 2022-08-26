package com.programming.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUrlAndAwsEc2Instance {
    private String s3Url;

    private String ec2IpAddress;

}
