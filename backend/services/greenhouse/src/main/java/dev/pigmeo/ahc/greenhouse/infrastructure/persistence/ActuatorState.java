package dev.pigmeo.ahc.greenhouse.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** The last-known on/off state of a single greenhouse actuator (e.g. "led", "fan"). */
@Entity
@Table(
    name = "actuator_state",
    schema = "greenhouse",
    uniqueConstraints = @UniqueConstraint(columnNames = "device"))
public class ActuatorState extends BaseEntity {

  @Column(nullable = false, updatable = false)
  private String device;

  @Column(name = "is_on", nullable = false)
  private boolean on;

  protected ActuatorState() {}

  public ActuatorState(String device, boolean on) {
    this.device = device;
    this.on = on;
  }

  public String getDevice() {
    return device;
  }

  public boolean isOn() {
    return on;
  }

  public void setOn(boolean on) {
    this.on = on;
  }
}
