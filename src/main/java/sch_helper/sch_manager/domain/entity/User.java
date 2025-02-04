package sch_helper.sch_manager.domain.entity;

import jakarta.persistence.*;
import sch_helper.sch_manager.domain.Role;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id")
    private String login_id;
    private String password;
    private String username;
    private Role role;
}
