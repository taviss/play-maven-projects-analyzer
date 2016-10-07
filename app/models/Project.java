package models;

import lombok.Data;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by octavian.salcianu on 8/17/2016.
 */

@Entity
@Data
@Table(name = "uploads")
@Indexed
public class Project {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @Constraints.Required
    @Constraints.MinLength(3)
    @Constraints.MaxLength(128)
    @Field
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp", nullable = false)
    private Date inputDate;

    @Column(name = "path", nullable = false)
    private String fileName;

    @Column(name = "project_name", nullable = false)
    @Field
    private String projectName;

    @Column(name = "project_key", nullable = false)
    private String projectKey;
}
