package sch_helper.sch_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sch_helper.sch_manager.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
