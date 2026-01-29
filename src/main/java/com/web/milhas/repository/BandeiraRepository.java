package com.web.milhas.repository;

import com.web.milhas.entity.BandeiraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BandeiraRepository extends JpaRepository<BandeiraEntity, Long> {
    List<BandeiraEntity> findByStatus(String status);
}