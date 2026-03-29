package com.azexam.simulator.service;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

@Service
public class BlobService {
  
  private final BlobContainerClient blobContainerClient;

  public BlobService(
    @Value("${azure.storage.blob-endpoint:}") String blobEndpoint,
    @Value("${azure.storage.sas-token:}") String sasToken,
    @Value("${azure.storage.container-name}") String containerName
  ) {
    if (blobEndpoint != null && !blobEndpoint.isBlank() && sasToken != null && !sasToken.isBlank()) {
      String normalizedSasToken = sasToken.startsWith("?") ? sasToken.substring(1) : sasToken;
      this.blobContainerClient =
        new BlobContainerClientBuilder()
          .endpoint(blobEndpoint)
          .sasToken(normalizedSasToken)
          .containerName(containerName)
          .buildClient();
      return;
    }

    throw new IllegalStateException(
      "Azure Blob Storage configuration is missing. Set azure.storage.connection-string " +
      "or both azure.storage.blob-endpoint and azure.storage.sas-token."
    );
  }

  /**
   * Downloads a blob as a UTF-8 string.
   *
   * @param filename blob filename
   * @return file content as text
   */
  public String downloadFile(String filename) {

    BlobClient blobClient = blobContainerClient.getBlobClient(filename);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    blobClient.downloadStream(outputStream);

    return outputStream.toString();
  }
}
