package com.example.demo.controllers;

import com.example.demo.entities.DemandePret;
import com.example.demo.entities.ScoreCredit;
import com.example.demo.repositories.DemandePretRepository;
import com.example.demo.services.RiskScoringService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin("*")
public class DemandePretController {

    private final RiskScoringService riskScoringService;
    private final DemandePretRepository demandePretRepository;

    public DemandePretController(RiskScoringService riskScoringService, DemandePretRepository demandePretRepository) {
        this.riskScoringService = riskScoringService;
        this.demandePretRepository = demandePretRepository;
    }

    @PostMapping("/soumettre")
    public ResponseEntity<DemandePret> soumettreDemande(@RequestBody @NonNull DemandePret demandePret) {
        Objects.requireNonNull(demandePret, "demandePret ne peut pas être null");
        DemandePret savedDemande = demandePretRepository.save(demandePret);
        Long demandeId = Objects.requireNonNull(savedDemande.getId(), "L'ID de la demande enregistrée ne peut pas être null");
        ScoreCredit scoreCredit = riskScoringService.traiterDemande(demandeId);
        if (scoreCredit == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        Optional<DemandePret> updatedDemande = demandePretRepository.findById(demandeId);
        return updatedDemande.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandePret> getDemande(@PathVariable @NonNull Long id) {
        Objects.requireNonNull(id, "id ne peut pas être null");
        return demandePretRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<DemandePret>> getAllDemandes() {
        List<DemandePret> demandes = demandePretRepository.findAll();
        return ResponseEntity.ok(demandes);
    }
}
