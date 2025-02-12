package sch_helper.sch_manager.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sch_helper.sch_manager.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
