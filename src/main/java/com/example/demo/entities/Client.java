package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Client extends Utilisateur {

    @Column(name = "revenu_mensuel", precision = 15, scale = 2)
    private BigDecimal revenuMensuel;

    @Column(name = "charges_fixes", precision = 15, scale = 2)
    private BigDecimal chargesFixes;

    @Column(length = 100)
    private String profession;

    @Column(name = "situation_familiale", length = 100)
    private String situationFamiliale;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<DemandePret> demandePrets = new ArrayList<>();
}
