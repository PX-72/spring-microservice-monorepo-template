package com.example.greeting.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "greetings")
public class GreetingEntity {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "message", nullable = false, length = 512)
  private String message;

  protected GreetingEntity() {}

  public GreetingEntity(UUID id, String message) {
    this.id = id;
    this.message = message;
  }

  public UUID getId() {
    return this.id;
  }

  public String getMessage() {
    return this.message;
  }
}
