package dev.pigmeo.ahc.greenhouse.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** A named LED schedule (e.g. "Veg", "Flora") with an ON and an OFF 6-field cron expression. */
@Entity
@Table(name = "light_cycle", schema = "greenhouse")
public class LightCycle extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(name = "on_cron", nullable = false)
  private String onCron;

  @Column(name = "off_cron", nullable = false)
  private String offCron;

  @Column(nullable = false)
  private boolean active;

  protected LightCycle() {}

  public LightCycle(String name, String onCron, String offCron) {
    this.name = name;
    this.onCron = onCron;
    this.offCron = offCron;
    this.active = false;
  }

  public String getName() {
    return name;
  }

  public String getOnCron() {
    return onCron;
  }

  public String getOffCron() {
    return offCron;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
