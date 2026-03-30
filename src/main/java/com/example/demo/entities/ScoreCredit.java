package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "score_credits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreCredit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valeur_score", precision = 5, scale = 2)
    private BigDecimal valeurScore;

    @Column(name = "niveau_risque", length = 100)
    private String niveauRisque;

    @Column(name = "recommandation_ia", columnDefinition = "TEXT")
    private String recommandationIA;

    @Column(name = "date_calcul")
    private LocalDateTime dateCalcul;

    @OneToOne(mappedBy = "scoreCredit")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DemandePret demandePret;
}
