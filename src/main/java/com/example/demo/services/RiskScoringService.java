package com.example.demo.services;

import com.example.demo.entities.DemandePret;
import com.example.demo.entities.ScoreCredit;
import com.example.demo.entities.StatutDemande;
import com.example.demo.repositories.DemandePretRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Transactional
public class RiskScoringService {

    private final DemandePretRepository demandePretRepository;
    private final AiService aiService;

    public RiskScoringService(DemandePretRepository demandePretRepository, AiService aiService) {
        this.demandePretRepository = demandePretRepository;
        this.aiService = aiService;
    }

    public ScoreCredit traiterDemande(Long demandeId) {
        if (demandeId == null) {
            throw new IllegalArgumentException("demandeId ne peut pas être null");
        }

        DemandePret demande = demandePretRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée pour l'id : " + demandeId));

        BigDecimal ratio = demande.calculerRatio();
        boolean isRefuse = ratio.compareTo(BigDecimal.valueOf(0.40)) > 0;
        demande.setStatut(isRefuse ? StatutDemande.REFUSE : StatutDemande.ACCEPTE);

        String recommandation = aiService.generateExplication(ratio.doubleValue(), demande.getMontantDemande().doubleValue());

        ScoreCredit scoreCredit = demande.getScoreCredit();
        if (scoreCredit == null) {
            scoreCredit = new ScoreCredit();
            demande.setScoreCredit(scoreCredit);
        }

        scoreCredit.setValeurScore(ratio.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        scoreCredit.setNiveauRisque(isRefuse ? "Élevé" : "Faible");
        scoreCredit.setRecommandationIA(recommandation);
        scoreCredit.setDateCalcul(LocalDateTime.now());
        scoreCredit.setDemandePret(demande);

        return demandePretRepository.save(demande).getScoreCredit();
    }
}
