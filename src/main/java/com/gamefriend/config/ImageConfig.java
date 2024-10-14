package com.gamefriend.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class ImageConfig {

  @Value("${cloud.vultr.credentials.access-key}")
  private String accessKey;

  @Value("${cloud.vultr.credentials.secret-key}")
  private String secretKey;

  @Value("${cloud.vultr.host.url}")
  private String url;

  @Bean
  public S3Client s3Client() {

    return S3Client.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .endpointOverride(URI.create(url))
        .region(Region.US_EAST_1)
        .build();
  }
}