package dev.pigmeo.ahc.greenhouse.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import java.time.Instant;

/**
 * Shared columns for every JPA entity in this service: a surrogate autoincrement id, creation and
 * update timestamps, and an optimistic-locking version.
 */
@MappedSuperclass
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  private Instant updatedAt;

  @Version private Long version;

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
    // Hibernate manages incrementing this on every subsequent update; its own default starting
    // value would be 0, but this entity's version is required to start at 1.
    version = 1L;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Long getVersion() {
    return version;
  }
}
