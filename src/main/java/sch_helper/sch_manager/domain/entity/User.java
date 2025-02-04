package sch_helper.sch_manager.domain.entity;

import jakarta.persistence.*;
import sch_helper.sch_manager.domain.Role;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private Role role;
}
