package com.portfolio.inventory.infrastructure.out.storage;

import com.portfolio.inventory.domain.repository.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class SupabaseStorageAdapter implements FileStoragePort {

  private final RestClient restClient;
  private final String uploadUrl;
  private final String publicUrlPrefix;
  private final String apiKey;

  public SupabaseStorageAdapter(
    @Value("${supabase.url}") String supabaseUrl,
    @Value("${supabase.key}") String apiKey,
    @Value("${supabase.bucket.name}") String bucketName,
    @Value("${supabase.bucket.folder}") String folderName) {

    this.apiKey = apiKey;

    // Estructura: /bucket/carpeta/archivo
    this.uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + folderName + "/";
    this.publicUrlPrefix = supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + folderName + "/";

    this.restClient = RestClient.create();
  }

  @Override
  public String uploadFile(String fileName, byte[] fileData, String contentType) {
    String cleanFileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    String uniqueFileName = UUID.randomUUID() + "_" + cleanFileName;

    restClient.post()
      .uri(uploadUrl + uniqueFileName)
      .header("Authorization", "Bearer " + apiKey)
      .header("apikey", apiKey)
      .header("Content-Type", contentType)
      .body(fileData)
      .retrieve()
      .toBodilessEntity();

    return publicUrlPrefix + uniqueFileName;
  }
}