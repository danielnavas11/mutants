package ar.com.ml.ibmcloud.projects.mutants.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="count_mutant_dna", columnDefinition="INT default 0")
    private Long count_mutant_dna;

    @Column(name="count_human_dna", columnDefinition="INT default 0")
    private Long count_human_dna;

    @Column(name="ratio", columnDefinition="INT default 0")
    private Double ratio;
}
