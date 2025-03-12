package sch_helper.sch_manager.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sch_helper.sch_manager.domain.menu.entity.MenuImage;

import java.util.Optional;

@Repository
public interface MenuImageRepository extends JpaRepository<MenuImage, String> {

    Optional<MenuImage> findByImageName(String imageName);
}
