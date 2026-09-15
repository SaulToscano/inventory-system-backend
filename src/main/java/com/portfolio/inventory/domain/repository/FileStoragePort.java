package com.portfolio.inventory.domain.repository;

public interface FileStoragePort {
  /**
   * Uploads a file and returns the public URL to access it.
   */
  String uploadFile(String fileName, byte[] fileData, String contentType);
}