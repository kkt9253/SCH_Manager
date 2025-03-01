package sch_helper.sch_manager.domain.menu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MenuImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menuImage_id")
    private Long id;

    // HYANGSEOL1-2025-02-17-week , 이러한 형태로 이미지 구별할 수 있기 때문에 week 필요 X, 또한 이러한 형태로 하면 나중에 해당 엔티티 재사용성있게 사용할 수 있을 듯
    @Column(name = "image_name", nullable = false)
    private String imageName;

    @Lob
    @Column(name = "image_binary", nullable = false)
    //private String imageBinary;
    private byte[] imageBinary;
}
