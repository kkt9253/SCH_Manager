package sch_helper.sch_manager.domain.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshToken {

    private String username;
    private String refreshToken;
}
