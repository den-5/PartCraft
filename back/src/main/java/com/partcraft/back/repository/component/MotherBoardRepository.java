package com.partcraft.back.repository.component;

import com.partcraft.back.entity.component.MotherBoard;
import com.partcraft.back.repository.ComponentsRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotherBoardRepository extends ComponentsRepository<MotherBoard, Long> {
}
