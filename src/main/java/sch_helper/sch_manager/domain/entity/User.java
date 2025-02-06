package sch_helper.sch_manager.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sch_helper.sch_manager.domain.Role;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
