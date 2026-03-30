package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "demande_prets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandePret implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "montant_demande", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantDemande;

    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Client client;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "score_credit_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ScoreCredit scoreCredit;

    @OneToMany(mappedBy = "demandePret", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Justificatif> justificatifs = new ArrayList<>();

    public BigDecimal calculerRatioEndettement() {
        if (client == null || client.getRevenuMensuel() == null || client.getChargesFixes() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal revenu = client.getRevenuMensuel();
        if (BigDecimal.ZERO.compareTo(revenu) >= 0) {
            return BigDecimal.ZERO;
        }
        return client.getChargesFixes().divide(revenu, 4, RoundingMode.HALF_UP);
    }
}
