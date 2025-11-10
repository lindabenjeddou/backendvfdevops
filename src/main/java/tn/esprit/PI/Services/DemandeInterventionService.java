package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.Curative;
import tn.esprit.PI.entity.DemandeIntervention;
import tn.esprit.PI.entity.DemandeInterventionDTO;
import tn.esprit.PI.entity.Preventive;
import tn.esprit.PI.entity.StatutDemande;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.TesteurRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DemandeInterventionService {

    private static final Logger logger = LoggerFactory.getLogger(DemandeInterventionService.class);

    @Autowired
    private DemandeInterventionRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TesteurRepository testeurRepository;

    /* ---------- Mapping util (row native -> DTO) ---------- */
    private DemandeInterventionDTO mapRowToDTO(Map<String, Object> row) {
        DemandeInterventionDTO dto = new DemandeInterventionDTO();
        dto.setId(((Number) row.get("id")).longValue());
        dto.setDescription((String) row.get("description"));
        dto.setDateDemande((java.util.Date) row.get("date_demande"));
        dto.setStatut(StatutDemande.valueOf((String) row.get("statut")));
        dto.setPriorite((String) row.get("priorite"));
        dto.setDemandeurId(row.get("demandeur") != null ? ((Number) row.get("demandeur")).longValue() : null);
        dto.setTypeDemande((String) row.get("type_demande"));
        dto.setDateCreation((java.util.Date) row.get("date_creation"));
        dto.setDateValidation((java.util.Date) row.get("date_validation"));
        dto.setConfirmation(row.get("confirmation") != null ? ((Number) row.get("confirmation")).intValue() : 0);
        dto.setTesteurCodeGMAO((String) row.get("testeur_code_gmao"));
        dto.setTechnicienAssigneId(row.get("technicien_id") != null ? ((Number) row.get("technicien_id")).longValue() : null);
        dto.setPanne((String) row.get("panne"));
        dto.setUrgence(row.get("urgence") != null ? (Boolean) row.get("urgence") : null);
        dto.setFrequence((String) row.get("frequence"));
        dto.setProchainRDV((java.util.Date) row.get("prochainrdv"));
        return dto;
    }

    /* ========================== LECTURE ========================== */

    public Optional<DemandeInterventionDTO> getDemandeById(Long id) {
        try {
            logger.info("Récupération de la demande avec ID: {}", id);
            List<Map<String, Object>> results = repository.findAllWithNullSafeDates();
            logger.info("Nombre de résultats de la requête native: {}", results.size());
            
            if (results.isEmpty()) {
                logger.warn("Aucun résultat de la requête native. Utilisation du fallback JPA.");
                return getFallbackDemandeById(id);
            }
            
            return results.stream()
                    .filter(r -> ((Number) r.get("id")).longValue() == id)
                    .findFirst()
                    .map(this::mapRowToDTO);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de la demande ID {}: {}", id, e.getMessage());
            e.printStackTrace();
            return getFallbackDemandeById(id);
        }
    }
    
    // Méthode fallback utilisant JPA standard
    private Optional<DemandeInterventionDTO> getFallbackDemandeById(Long id) {
        try {
            logger.info("Utilisation du fallback JPA pour l'ID: {}", id);
            return repository.findById(id).map(this::createDTOFromEntity);
        } catch (Exception e) {
            logger.error("Erreur dans le fallback pour ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    public List<DemandeInterventionDTO> getAllDemandes() {
        try {
            logger.info("Récupération de toutes les demandes");
            List<Map<String, Object>> results = repository.findAllWithNullSafeDates();
            logger.info("Nombre total de résultats: {}", results.size());
            
            if (results.isEmpty()) {
                logger.warn("Aucun résultat de la requête native. Utilisation du fallback JPA.");
                return getFallbackAllDemandes();
            }
            
            List<DemandeInterventionDTO> dtos = results.stream()
                    .map(this::mapRowToDTO)
                    .toList();
            logger.info("Nombre de DTOs mappés: {}", dtos.size());
            return dtos;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de toutes les demandes: {}", e.getMessage());
            e.printStackTrace();
            return getFallbackAllDemandes();
        }
    }
    
    // Méthode fallback utilisant JPA standard
    private List<DemandeInterventionDTO> getFallbackAllDemandes() {
        try {
            logger.info("Utilisation du fallback JPA pour toutes les demandes");
            List<DemandeIntervention> entities = repository.findAll();
            logger.info("Nombre d'entités récupérées via JPA: {}", entities.size());
            return entities.stream()
                    .map(this::createDTOFromEntity)
                    .toList();
        } catch (Exception e) {
            logger.error("Erreur dans le fallback pour toutes les demandes: {}", e.getMessage());
            return List.of();
        }
    }

    /** Toutes les entités (si nécessaire ailleurs) */
    public List<DemandeIntervention> getAll() {
        return repository.findAll();
    }

    /** 🔹 Interventions assignées au technicien (DTO) */
    public List<DemandeInterventionDTO> getDemandesByTechnicien(Long technicienId) {
        try {
            logger.info("Récupération des demandes pour le technicien ID: {}", technicienId);
            List<Map<String, Object>> results = repository.findAllByTechnicienIdWithNullSafeDates(technicienId);
            logger.info("Nombre de résultats pour le technicien {}: {}", technicienId, results.size());
            
            if (results.isEmpty()) {
                logger.warn("Aucun résultat de la requête native. Utilisation du fallback JPA.");
                return getFallbackDemandesByTechnicien(technicienId);
            }
            
            return results.stream()
                    .map(this::mapRowToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des demandes pour technicien {}: {}", technicienId, e.getMessage());
            e.printStackTrace();
            return getFallbackDemandesByTechnicien(technicienId);
        }
    }
    
    // Méthode fallback utilisant JPA standard
    private List<DemandeInterventionDTO> getFallbackDemandesByTechnicien(Long technicienId) {
        try {
            logger.info("Utilisation du fallback JPA pour technicien ID: {}", technicienId);
            List<DemandeIntervention> entities = repository.findAll();
            List<DemandeInterventionDTO> filtered = entities.stream()
                    .filter(d -> d.getTechnicienAssigne() != null && d.getTechnicienAssigne().getId().equals(technicienId))
                    .map(this::createDTOFromEntity)
                    .collect(Collectors.toList());
            logger.info("Nombre de demandes trouvées via fallback pour technicien {}: {}", technicienId, filtered.size());
            return filtered;
        } catch (Exception e) {
            logger.error("Erreur dans le fallback pour technicien {}: {}", technicienId, e.getMessage());
            return List.of();
        }
    }

    /** 🔁 Alias pour compatibilité avec le contrôleur */
    public List<DemandeInterventionDTO> getByTechnicien(Long technicienId) {
        return getDemandesByTechnicien(technicienId);
    }

    /* ======================== AFFECTATIONS ======================== */

    public DemandeInterventionDTO assignTechnicianToIntervention(Long interventionId, Long technicienId) {
        try {
            boolean exists = repository.findAllWithNullSafeDates().stream()
                    .anyMatch(r -> ((Number) r.get("id")).longValue() == interventionId);
            if (!exists) throw new RuntimeException("Intervention non trouvée avec l'ID: " + interventionId);

            if (!userRepository.existsById(technicienId))
                throw new RuntimeException("Technicien non trouvé avec l'ID: " + technicienId);

            int rows = repository.assignTechnicianNative(interventionId, technicienId);
            if (rows == 0) throw new RuntimeException("Aucune ligne mise à jour pour l'ID: " + interventionId);

            Map<String, Object> updated = repository.findAllWithNullSafeDates().stream()
                    .filter(r -> ((Number) r.get("id")).longValue() == interventionId)
                    .findFirst().orElseThrow(() -> new RuntimeException("Impossible de récupérer l'intervention mise à jour"));

            return mapRowToDTO(updated);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'affectation: " + e.getMessage());
        }
    }

    public DemandeInterventionDTO assignTesteurToIntervention(Long interventionId, String testeurCodeGMAO) {
        try {
            boolean exists = repository.findAllWithNullSafeDates().stream()
                    .anyMatch(r -> ((Number) r.get("id")).longValue() == interventionId);
            if (!exists) throw new RuntimeException("Intervention non trouvée avec l'ID: " + interventionId);

            if (!testeurRepository.existsById(testeurCodeGMAO))
                throw new RuntimeException("Testeur/Équipement non trouvé avec le code: " + testeurCodeGMAO);

            int rows = repository.assignTesteurNative(interventionId, testeurCodeGMAO);
            if (rows == 0) throw new RuntimeException("Aucune ligne mise à jour pour l'ID: " + interventionId);

            Map<String, Object> updated = repository.findAllWithNullSafeDates().stream()
                    .filter(r -> ((Number) r.get("id")).longValue() == interventionId)
                    .findFirst().orElseThrow(() -> new RuntimeException("Impossible de récupérer l'intervention mise à jour"));

            return mapRowToDTO(updated);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'affectation du testeur: " + e.getMessage());
        }
    }

    public DemandeInterventionDTO confirmerIntervention(Long interventionId) {
        try {
            boolean exists = repository.findAllWithNullSafeDates().stream()
                    .anyMatch(r -> ((Number) r.get("id")).longValue() == interventionId);
            if (!exists) throw new RuntimeException("Intervention non trouvée avec l'ID: " + interventionId);

            int rows = repository.confirmerInterventionNative(interventionId);
            if (rows == 0) throw new RuntimeException("Aucune ligne mise à jour pour l'ID: " + interventionId);

            Map<String, Object> updated = repository.findAllWithNullSafeDates().stream()
                    .filter(r -> ((Number) r.get("id")).longValue() == interventionId)
                    .findFirst().orElseThrow(() -> new RuntimeException("Impossible de récupérer l'intervention confirmée"));

            return mapRowToDTO(updated);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la confirmation: " + e.getMessage());
        }
    }

    /* ======================= UPDATE / DELETE ======================= */

    public DemandeInterventionDTO updateDemande(Long id, DemandeInterventionDTO dto) {
        try {
            if (!repository.existsById(id))
                throw new RuntimeException("Demande d'intervention non trouvée avec l'ID: " + id);

            int rowsUpdated = repository.updateDemandeBasicFields(
                    id,
                    dto.getDescription(),
                    dto.getStatut() != null ? dto.getStatut().name() : null,
                    dto.getPriorite(),
                    dto.getTechnicienAssigneId()
            );
            if (rowsUpdated == 0)
                throw new RuntimeException("Aucune ligne mise à jour pour l'ID: " + id);

            Map<String, Object> updated = repository.findAllWithNullSafeDates().stream()
                    .filter(row -> ((Number) row.get("id")).longValue() == id)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Impossible de récupérer la demande mise à jour"));

            return mapRowToDTO(updated);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    public void deleteDemande(Long id) {
        repository.deleteById(id);
    }

    /* (optionnel) mapping entité -> DTO si besoin dans d'autres flows */
    private DemandeInterventionDTO createDTOFromEntity(DemandeIntervention demande) {
        DemandeInterventionDTO dto = new DemandeInterventionDTO();
        dto.setId(demande.getId());
        dto.setDescription(demande.getDescription());
        dto.setDateDemande(demande.getDateDemande());
        dto.setStatut(demande.getStatut());
        dto.setPriorite(demande.getPriorite());
        dto.setDemandeurId(demande.getDemandeur() != null ? demande.getDemandeur().getId() : null);
        dto.setTypeDemande(demande.getType_demande());
        try {
            dto.setDateCreation(demande.getDateCreation());
            dto.setDateValidation(demande.getDateValidation());
            dto.setConfirmation(demande.getConfirmation() != null ? demande.getConfirmation() : 0);
            dto.setTesteurCodeGMAO(demande.getTesteur() != null ? demande.getTesteur().getCodeGMAO() : null);
            dto.setTechnicienAssigneId(demande.getTechnicienAssigne() != null ? demande.getTechnicienAssigne().getId() : null);
        } catch (Exception ignored) {
            dto.setDateCreation(null);
            dto.setDateValidation(null);
            dto.setConfirmation(0);
            dto.setTesteurCodeGMAO(null);
            dto.setTechnicienAssigneId(null);
        }
        if (demande instanceof Curative cur) {
            dto.setPanne(cur.getPanne());
            dto.setUrgence(cur.isUrgence());
        }
        if (demande instanceof Preventive prev) {
            dto.setFrequence(prev.getFrequence());
            dto.setProchainRDV(prev.getProchainRDV());
        }
        return dto;
    }
}
