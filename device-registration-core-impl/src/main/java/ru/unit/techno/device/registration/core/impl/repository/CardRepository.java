
package ru.unit.techno.device.registration.core.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.unit.techno.device.registration.core.impl.entity.CardEntity;

public interface CardRepository extends JpaRepository<CardEntity, Long> {
    CardEntity findByGroup_GroupId(Long id);
}