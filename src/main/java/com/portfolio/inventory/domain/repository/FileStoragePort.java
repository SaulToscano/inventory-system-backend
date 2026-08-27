package com.portfolio.inventory.domain.repository;

public interface FileStoragePort {
  /**
   * Sube un archivo y retorna la URL pública para acceder a él.
   */
  String uploadFile(String fileName, byte[] fileData, String contentType);
}