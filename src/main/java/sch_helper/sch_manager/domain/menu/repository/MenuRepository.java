package sch_helper.sch_manager.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sch_helper.sch_manager.domain.menu.entity.Menu;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    boolean existsByUnique(String unique);
    Optional<Menu> findByUnique(String unique);
    void deleteByUnique(String unique);
}
