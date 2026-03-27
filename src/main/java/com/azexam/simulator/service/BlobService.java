package com.azexam.simulator.service;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

@Service
public class BlobService {
  
  private final BlobContainerClient blobContainerClient;

  public BlobService(
    @Value(".storage.connection-string}") String connectionString,
    @Value("${azure.storage.container-name}") String containerName
  ) {
    BlobServiceClient serviceClient = 
      new BlobServiceClientBuilder()
        .connectionString(connectionString)
        .buildClient();
    
      this.blobContainerClient = serviceClient.getBlobContainerClient(containerName);
  }

  public String downloadFile(String filename) {

    BlobClient blobClient = blobContainerClient.getBlobClient(filename);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    blobClient.downloadStream(outputStream);

    return outputStream.toString();
  }
}
