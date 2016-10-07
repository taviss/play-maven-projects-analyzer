package models;

import lombok.Data;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by octavian.salcianu on 8/17/2016.
 */

@Entity
@Data
@Table(name = "sonar_hosts")
public class Sonar {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address", nullable = false)
    @Constraints.Required
    @Constraints.MaxLength(128)
    private String address;

    @Column(name = "username", nullable = false)
    @Constraints.Required
    @Constraints.MaxLength(128)
    private String user;

    @Column(name = "password", nullable = false)
    @Constraints.Required
    @Constraints.MaxLength(128)
    private String password;
}
