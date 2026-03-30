package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Table(name = "justificatifs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Justificatif implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_fichier", nullable = false, length = 255)
    private String nomFichier;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private TypeJustificatif type;

    @Column(name = "url_stockage", nullable = false, columnDefinition = "TEXT")
    private String urlStockage;

    @ManyToOne(optional = false)
    @JoinColumn(name = "demande_pret_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DemandePret demandePret;
}
