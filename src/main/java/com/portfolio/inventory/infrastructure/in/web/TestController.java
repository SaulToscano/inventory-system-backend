package com.portfolio.inventory.infrastructure.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
public class TestController {

  @GetMapping("/ping")
  public String ping() {
    return "¡Login exitoso! Tu backend está validando el JWT de Supabase correctamente.";
  }
}