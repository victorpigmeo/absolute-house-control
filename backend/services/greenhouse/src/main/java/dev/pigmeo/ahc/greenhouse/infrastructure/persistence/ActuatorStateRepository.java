package dev.pigmeo.ahc.greenhouse.infrastructure.persistence;

import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

@Repository
public interface ActuatorStateRepository extends JpaRepository<ActuatorState, Long> {

  int SAVE_STATE_MAX_ATTEMPTS = 3;

  Optional<ActuatorState> findByDevice(String device);

  default boolean isOn(String device) {
    return findByDevice(device).map(ActuatorState::isOn).orElse(false);
  }

  /**
   * Find-then-save is a check-then-act race under concurrent calls for the same device: a duplicate
   * first insert throws {@link DataIntegrityViolationException} (unique constraint on {@code
   * device}), and a lost update on an existing row throws {@link
   * ObjectOptimisticLockingFailureException} ({@code @Version} check). Retrying picks up the
   * competing write and reapplies this call's state on top of it.
   */
  default void saveState(String device, boolean on) {
    for (int attempt = 1; attempt <= SAVE_STATE_MAX_ATTEMPTS; attempt++) {
      try {
        ActuatorState state = findByDevice(device).orElseGet(() -> new ActuatorState(device, on));
        state.setOn(on);
        save(state);
        return;
      } catch (DataIntegrityViolationException | ObjectOptimisticLockingFailureException e) {
        if (attempt == SAVE_STATE_MAX_ATTEMPTS) {
          throw e;
        }
      }
    }
  }
}
