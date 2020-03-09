package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

public class TxMlQueries {

  public static String getPatientsWhoMissedAppointment(
      int returnVisitDateForDrugsConcept,
      int returnVisitDateConcept,
      int pharmacyEncounterType,
      int adultoSequimentoEncounterType,
      int pediatriaSeguimentoEncounterType,
      int masterCardDrugPickupEncounterType,
      int artPickupDateMasterCardConcept) {

    Map<String, Integer> valuesMap = new HashMap<>();

    valuesMap.put("returnVisitDateForDrugsConcept", returnVisitDateForDrugsConcept);
    valuesMap.put("returnVisitDateConcept", returnVisitDateConcept);
    valuesMap.put("pharmacyEncounterType", pharmacyEncounterType);
    valuesMap.put("adultoSequimentoEncounterType", adultoSequimentoEncounterType);
    valuesMap.put("pediatriaSeguimentoEncounterType", pediatriaSeguimentoEncounterType);
    valuesMap.put("masterCardDrugPickupEncounterType", masterCardDrugPickupEncounterType);
    valuesMap.put("artPickupDateMasterCardConcept", artPickupDateMasterCardConcept);
    String query =
        "SELECT "
            + "	patient_id "
            + "FROM "
            + "("
            + "	SELECT "
            + "		pp.patient_id, MAX(pp.return_date) AS return_date"
            + "	FROM"
            + "	("
            + "		SELECT "
            + "			p.patient_id, "
            + "			o.value_datetime AS return_date,"
            + "			e.encounter_id"
            + "		FROM patient p "
            + "		INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + "		INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "		WHERE "
            + "			p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "			AND e.encounter_type IN (${adultoSequimentoEncounterType},${pediatriaSeguimentoEncounterType}) "
            + "			AND o.concept_id = ${returnVisitDateConcept} "
            + "			AND e.encounter_datetime <=:endDate AND e.location_id=:location "
            + "		UNION"
            + "		SELECT "
            + "			p.patient_id, "
            + "			o.value_datetime AS return_date,"
            + "			e.encounter_id"
            + "		FROM patient p "
            + "		INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + "		INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "		WHERE "
            + "			p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "			AND e.encounter_type IN (${pharmacyEncounterType}) "
            + "			AND o.concept_id = ${returnVisitDateForDrugsConcept} "
            + "			AND e.encounter_datetime <=:endDate AND e.location_id=:location "
            + "		UNION"
            + "		SELECT"
            + "			p.patient_id, "
            + "			DATE_ADD(o.value_datetime, INTERVAL 30 DAY) AS return_date,"
            + "			e.encounter_id			"
            + "		FROM patient p"
            + "		INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + "		INNER JOIN obs o ON o.encounter_id = e.encounter_id"
            + "		WHERE"
            + "			p.voided = 0 AND e.voided = 0 AND o.voided = 0"
            + "			AND e.encounter_type = ${masterCardDrugPickupEncounterType} "
            + "			AND o.concept_id = ${artPickupDateMasterCardConcept}"
            + "			AND o.value_datetime <=:endDate AND e.location_id=:location "
            + "	) pp"
            + "	INNER JOIN ("
            + "		SELECT p.patient_id, "
            + "			("
            + "				SELECT "
            + "					e.encounter_id "
            + "				FROM encounter e"
            + "				INNER JOIN obs o ON e.encounter_id = o.encounter_id"
            + "				WHERE "
            + "					e.patient_id = p.patient_id"
            + "					AND e.voided = 0"
            + "					AND o.voided = 0"
            + "					AND e.encounter_datetime <= :endDate"
            + "					AND e.encounter_type IN (${adultoSequimentoEncounterType},${pediatriaSeguimentoEncounterType})"
            + "				ORDER BY e.encounter_datetime DESC"
            + "				LIMIT 1"
            + "			) last_cl_encounter,"
            + "			("
            + "				SELECT "
            + "					e.encounter_id "
            + "				FROM encounter e"
            + "				INNER JOIN obs o ON e.encounter_id = o.encounter_id"
            + "				WHERE "
            + "					e.patient_id = p.patient_id"
            + "					AND e.voided = 0"
            + "					AND o.voided = 0"
            + "					AND e.encounter_datetime <= :endDate"
            + "					AND e.encounter_type = ${pharmacyEncounterType}"
            + "				ORDER BY e.encounter_datetime DESC"
            + "				LIMIT 1"
            + "			) AS latest_pharm_encounter_id,"
            + "			("
            + "					SELECT "
            + "						e.encounter_id "
            + "					FROM encounter e"
            + "					INNER JOIN obs o ON e.encounter_id = o.encounter_id"
            + "					WHERE "
            + "						e.patient_id = p.patient_id"
            + "						AND e.voided = 0"
            + "						AND o.voided = 0"
            + "						AND o.value_datetime <= :endDate"
            + "						AND e.encounter_type = ${masterCardDrugPickupEncounterType}"
            + "						AND o.concept_id = ${artPickupDateMasterCardConcept}"
            + "					ORDER BY o.value_datetime DESC"
            + "					LIMIT 1"
            + "			) AS latest_dp_encounter_id				"
            + "		FROM patient p"
            + "	) last_encounters ON last_encounters.patient_id = pp.patient_id"
            + "	WHERE pp.encounter_id IN (last_cl_encounter, latest_pharm_encounter_id, latest_dp_encounter_id)"
            + "	GROUP BY pp.patient_id"
            + ")all_patients "
            + " WHERE "
            + "	DATE_ADD(return_date, INTERVAL 28 DAY)  >= DATE_ADD(:startDate, INTERVAL -1 DAY) "
            + "	AND DATE_ADD(return_date, INTERVAL 28 DAY) < :endDate";

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  public static String getNonConsentedPatients(
      int prevencaoPositivaInicial,
      int prevencaoPositivaSeguimento,
      int acceptContactConcept,
      int noConcept) {
    String query =
        "SELECT distinct(pp.patient_id) FROM patient pp "
            + "INNER JOIN encounter e ON e.patient_id=pp.patient_id "
            + "INNER JOIN obs o ON o.person_id = pp.patient_id "
            + "INNER JOIN person p ON o.person_id = p.person_id "
            + "WHERE pp.voided=0 AND e.voided=0 AND e.encounter_type IN(%d, %d) AND e.location_id=:location AND o.obs_datetime<=:endDate AND o.voided=0 AND o.concept_id=%d AND o.value_coded=%d AND o.location_id=:location "
            + "AND o.obs_id = (SELECT obs_id FROM obs WHERE concept_id = %d AND pp.patient_id = person_id GROUP BY obs_datetime DESC LIMIT 1)";

    return String.format(
        query,
        prevencaoPositivaInicial,
        prevencaoPositivaSeguimento,
        acceptContactConcept,
        noConcept,
        acceptContactConcept);
  }

  public static String getTransferredOutPatients(int program, int state) {
    String query =
        "SELECT pg.patient_id"
            + " FROM patient p"
            + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
            + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND"
            + " pg.program_id=%d"
            + " AND ps.state=%d"
            + " AND ps.start_date BETWEEN (:endDate - INTERVAL 183 DAY) AND  :endDate AND pg.location_id=:location AND ps.end_date is null";
    return String.format(query, program, state);
  }

  // All Patients marked as Dead in the patient home visit card
  public static String getPatientsMarkedDeadInHomeVisitCard(
      int homeVisitCardEncounterTypeId,
      int apoioReintegracaoParteAEncounterTypeId,
      int apoioReintegracaoParteBEncounterTypeId,
      int busca,
      int dead) {
    String query =
        " SELECT     pa.patient_id "
            + "FROM       patient pa "
            + "INNER JOIN encounter e "
            + "ON         pa.patient_id=e.patient_id "
            + "INNER JOIN obs o "
            + "ON         pa.patient_id=o.person_id "
            + "WHERE      e.encounter_type IN (%d, %d, %d) "
            + "AND        o.concept_id= %d "
            + "AND        o.value_coded = %d "
            + "AND        e.location_id=:location "
            + "AND        o.obs_datetime <=:endDate";

    return String.format(
        query,
        homeVisitCardEncounterTypeId,
        apoioReintegracaoParteAEncounterTypeId,
        apoioReintegracaoParteBEncounterTypeId,
        busca,
        dead);
  }

  public static String getPatientsWithMissedVisit(
      int homeVisitCardEncounterTypeId,
      int reasonPatientMissedVisitConceptId,
      int transferredOutToAnotherFacilityConceptId,
      int autoTransferConceptId) {
    String query =
        "SELECT e.patient_id "
            + "FROM encounter e "
            + "         JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) encounter_datetime "
            + "               FROM patient p "
            + "                        JOIN encounter e ON p.patient_id = e.patient_id "
            + "                        JOIN obs o ON e.encounter_id = o.encounter_id "
            + "               WHERE o.concept_id = %d "
            + "                 AND e.location_id = :location "
            + "                 AND e.encounter_type= %d "
            + "                 AND e.encounter_datetime BETWEEN :startDate AND :endDate AND p.voided=0 "
            + "               GROUP BY p.patient_id) last "
            + "              ON e.patient_id = last.patient_id AND last.encounter_datetime = e.encounter_datetime "
            + "WHERE o.value_coded IN (%d,%d) AND e.location_id = :location AND e.voided=0 AND o.voided=0 ";

    return String.format(
        query,
        reasonPatientMissedVisitConceptId,
        homeVisitCardEncounterTypeId,
        transferredOutToAnotherFacilityConceptId,
        autoTransferConceptId);
  }

  public static String getRefusedOrStoppedTreatment(
      int homeVisitCardEncounterTypeId,
      int reasonPatientMissedVisitConceptId,
      int patientForgotVisitDateConceptId,
      int patientIsBedriddenAtHomeConceptId,
      int distanceOrMoneyForTransportIsToMuchForPatientConceptId,
      int patientIsDissatifiedWithDayHospitalServicesConceptId,
      int fearOfTheProviderConceptId,
      int absenceOfHealthProviderInHealthUnitConceptId,
      int patientDoesNotLikeArvTreatmentSideEffectsConceptId,
      int patientIsTreatingHivWithTraditionalMedicineConceptId,
      int otherReasonWhyPatientMissedVisitConceptId,
      int pharmacyEncounterTypeId,
      int returnVisitDateForDrugsConcept,
      int adultoSequimentoEncounterTypeId,
      int arvPediatriaSeguimentoEncounterTypeId,
      int returnVisitDateConcept,
      int masterCardDrugPickupEncounterTypeId,
      int artDatePickupConceptId) {

    String query =
        "SELECT e.patient_id "
            + "FROM encounter e "
            + "         JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         JOIN (SELECT patient_id, DATE_ADD(MAX(return_date), INTERVAL 28 DAY) AS return_date "
            + "               FROM (SELECT p.patient_id, "
            + "                            (SELECT o.value_datetime AS return_date "
            + "                             FROM encounter e "
            + "                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                             WHERE p.patient_id = e.patient_id "
            + "                               AND e.voided = 0 "
            + "                               AND o.voided = 0 "
            + "                               AND e.encounter_type = %d "
            + "                               AND e.location_id = :location "
            + "                               AND o.concept_id = %d "
            + "                               AND e.encounter_datetime <= :endDate "
            + "                             ORDER BY e.encounter_datetime DESC "
            + "                             LIMIT 1) AS return_date "
            + "                     FROM patient p "
            + "                     UNION "
            + "                     SELECT p.patient_id, "
            + "                            (SELECT o.value_datetime AS return_date "
            + "                             FROM encounter e "
            + "                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                             WHERE p.patient_id = e.patient_id "
            + "                               AND e.voided = 0 "
            + "                               AND o.voided = 0 "
            + "                               AND e.encounter_type IN (%d, %d) "
            + "                               AND e.location_id = :location "
            + "                               AND o.concept_id = %d "
            + "                               AND e.encounter_datetime <= :endDate "
            + "                             ORDER BY e.encounter_datetime DESC "
            + "                             LIMIT 1) AS return_date "
            + "                     FROM patient p "
            + "                     UNION "
            + "                     SELECT p.patient_id, "
            + "                            (SELECT DATE_ADD(o.value_datetime, INTERVAL 30 DAY) AS return_date "
            + "                             FROM encounter e "
            + "                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                             WHERE p.patient_id = e.patient_id "
            + "                               AND e.voided = 0 "
            + "                               AND o.voided = 0 "
            + "                               AND e.encounter_type = %d "
            + "                               AND e.location_id = :location "
            + "                               AND o.concept_id = %d "
            + "                               AND o.value_datetime <= :endDate "
            + "                             ORDER BY e.encounter_datetime DESC "
            + "                             LIMIT 1) AS return_date "
            + "                     FROM patient p) e "
            + "               GROUP BY e.patient_id) lp ON e.patient_id = lp.patient_id "
            + "WHERE o.concept_id = %d AND o.value_coded IN (%d,%d,%d,%d,%d,%d,%d,%d,%d) "
            + "AND e.encounter_type = %d "
            + "AND e.encounter_datetime BETWEEN lp.return_date and :endDate "
            + "AND e.location_id = :location "
            + "AND e.voided=0 "
            + "AND o.voided=0 ";

    return String.format(
        query,
        pharmacyEncounterTypeId,
        returnVisitDateForDrugsConcept,
        adultoSequimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        returnVisitDateConcept,
        masterCardDrugPickupEncounterTypeId,
        artDatePickupConceptId,
        reasonPatientMissedVisitConceptId,
        patientForgotVisitDateConceptId,
        patientIsBedriddenAtHomeConceptId,
        distanceOrMoneyForTransportIsToMuchForPatientConceptId,
        patientIsDissatifiedWithDayHospitalServicesConceptId,
        fearOfTheProviderConceptId,
        absenceOfHealthProviderInHealthUnitConceptId,
        patientDoesNotLikeArvTreatmentSideEffectsConceptId,
        patientIsTreatingHivWithTraditionalMedicineConceptId,
        otherReasonWhyPatientMissedVisitConceptId,
        homeVisitCardEncounterTypeId);
  }

  /*
   Untraced Patients Criteria 2
   Patients without Patient Visit Card of type busca and with a set of observations
  */
  public static String getPatientsWithVisitCardAndWithObs(
      int pharmacyEncounterTypeId,
      int adultoSequimentoEncounterTypeId,
      int arvPediatriaSeguimentoEncounterTypeId,
      int returnVisitDateForDrugsConcept,
      int returnVisitDateConcept,
      int homeVisitCardEncounterTypeId,
      int apoioReintegracaoParteAEncounterTypeId,
      int apoioReintegracaoParteBEncounterTypeId,
      int typeOfVisitConcept,
      int buscaConcept,
      int secondAttemptConcept,
      int thirdAttemptConcept,
      int patientFoundConcept,
      int defaultingMotiveConcept,
      int reportVisitConcept1,
      int reportVisitConcept2,
      int patientFoundForwardedConcept,
      int reasonForNotFindingConcept,
      int whoGaveInformationConcept,
      int cardDeliveryDate,
      int masterCardDrugPickupEncounterTypeId,
      int artDatePickupConceptId) {
    String query =
        "SELECT pa.patient_id "
            + "FROM   patient pa "
            + "INNER JOIN ("
            + "SELECT patient_id, DATE_ADD(MAX(return_date), INTERVAL 28 DAY) AS return_date "
            + "FROM (SELECT p.patient_id, "
            + "             (SELECT o.value_datetime AS return_date "
            + "              FROM encounter e "
            + "                       INNER JOIN obs o "
            + "                                  ON e.encounter_id = o.encounter_id "
            + "              WHERE p.patient_id = e.patient_id "
            + "                AND e.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.encounter_type = ${pharmacy} "
            + "                AND e.location_id = :location "
            + "                AND o.concept_id = ${drugReturnVisitDate} "
            + "                AND e.encounter_datetime <= :endDate "
            + "              ORDER BY e.encounter_datetime DESC "
            + "              LIMIT 1) AS return_date "
            + "      FROM patient p "
            + "      UNION "
            + "      SELECT p.patient_id, "
            + "             (SELECT o.value_datetime AS return_date "
            + "              FROM encounter e "
            + "                       INNER JOIN obs o "
            + "                                  ON e.encounter_id = o.encounter_id "
            + "              WHERE p.patient_id = e.patient_id "
            + "                AND e.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.encounter_type IN (${adultSeg}, ${childSeg}) "
            + "                AND e.location_id = :location "
            + "                AND o.concept_id = ${returnVisitDate} "
            + "                AND e.encounter_datetime <= :endDate "
            + "              ORDER BY e.encounter_datetime DESC "
            + "              LIMIT 1) AS return_date "
            + "      FROM patient p "
            + "      UNION "
            + "      SELECT p.patient_id, "
            + "             (SELECT DATE_ADD(o.value_datetime, INTERVAL 30 DAY) AS return_date "
            + "              FROM encounter e "
            + "                       INNER JOIN obs o "
            + "                                  ON e.encounter_id = o.encounter_id "
            + "              WHERE p.patient_id = e.patient_id "
            + "                AND e.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.encounter_type = ${masterCardPickup} "
            + "                AND e.location_id = :location "
            + "                AND o.concept_id = ${artDatePickup} "
            + "                AND o.value_datetime <= :endDate "
            + "              ORDER BY e.encounter_datetime DESC "
            + "              LIMIT 1) AS return_date "
            + "      FROM patient p) e "
            + "GROUP BY e.patient_id) lp ON pa.patient_id=lp.patient_id "
            + "INNER JOIN encounter e ON "
            + "  pa.patient_id=e.patient_id AND "
            + "  e.encounter_datetime >= lp.return_date AND "
            + "  e.encounter_datetime <= :endDate AND "
            + "  e.encounter_type IN (${homeVisit}, ${apoioA}, ${apoioB}) AND "
            + "  e.location_id=:location "
            + "INNER JOIN obs visitType ON "
            + "  pa.patient_id=visitType.person_id AND "
            + "  visitType.encounter_id = e.encounter_id AND "
            + "  visitType.concept_id = ${typeVisit} AND "
            + "  visitType.value_coded = ${busca} AND "
            + "  visitType.obs_datetime <= :endDate "
            + "INNER JOIN obs o ON "
            + "  pa.patient_id=o.person_id AND "
            + "  o.encounter_id = e.encounter_id AND "
            + "  o.concept_id IN (${secondAttempt},"
            + "                   ${thirdAttempt},"
            + "                   ${patientFound},"
            + "                   ${defaultingMotive},"
            + "                   ${reportVisit1},"
            + "                   ${reportVisit2},"
            + "                   ${patientFoundForwarded},"
            + "                   ${reasonForNotFinding},"
            + "                   ${whoGaveInformation},"
            + "                   ${cardDeliveryDate} ) AND "
            + "  o.obs_datetime <= :endDate "
            + "GROUP BY pa.patient_id ";

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("pharmacy", pharmacyEncounterTypeId);
    valuesMap.put("drugReturnVisitDate", returnVisitDateForDrugsConcept);
    valuesMap.put("adultSeg", adultoSequimentoEncounterTypeId);
    valuesMap.put("childSeg", arvPediatriaSeguimentoEncounterTypeId);
    valuesMap.put("returnVisitDate", returnVisitDateConcept);
    valuesMap.put("masterCardPickup", masterCardDrugPickupEncounterTypeId);
    valuesMap.put("artDatePickup", artDatePickupConceptId);
    valuesMap.put("homeVisit", homeVisitCardEncounterTypeId);
    valuesMap.put("apoioA", apoioReintegracaoParteAEncounterTypeId);
    valuesMap.put("apoioB", apoioReintegracaoParteBEncounterTypeId);
    valuesMap.put("typeVisit", typeOfVisitConcept);
    valuesMap.put("busca", buscaConcept);
    valuesMap.put("secondAttempt", secondAttemptConcept);
    valuesMap.put("thirdAttempt", thirdAttemptConcept);
    valuesMap.put("patientFound", patientFoundConcept);
    valuesMap.put("defaultingMotive", defaultingMotiveConcept);
    valuesMap.put("reportVisit1", reportVisitConcept1);
    valuesMap.put("reportVisit2", reportVisitConcept2);
    valuesMap.put("patientFoundForwarded", patientFoundForwardedConcept);
    valuesMap.put("reasonForNotFinding", reasonForNotFindingConcept);
    valuesMap.put("whoGaveInformation", whoGaveInformationConcept);
    valuesMap.put("cardDeliveryDate", cardDeliveryDate);
    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    return sub.replace(query);
  }

  /*
       All Patients without “Patient Visit Card” (Encounter type 21 or 36 or 37) registered between
       ◦ the last scheduled appointment or drugs pick up (the most recent one) by reporting end date and
       ◦ the reporting end date
  */
  public static String
      getPatientsWithoutVisitCardRegisteredBtwnLastAppointmentOrDrugPickupAndEnddate(
          int pharmacyEncounterTypeId,
          int adultoSequimentoEncounterTypeId,
          int arvPediatriaSeguimentoEncounterTypeId,
          int returnVisitDateForDrugsConcept,
          int returnVisitDateConcept,
          int homeVisitCardEncounterTypeId,
          int apoioReintegracaoParteAEncounterTypeId,
          int apoioReintegracaoParteBEncounterTypeId,
          int masterCardDrugPickupEncounterTypeId,
          int artDatePickupConceptId) {

    String query =
        " SELECT pa.patient_id FROM patient pa "
            + " WHERE pa.patient_id NOT IN ("
            + "  SELECT pa.patient_id "
            + "  FROM patient pa"
            + "  INNER JOIN encounter e ON pa.patient_id=e.patient_id "
            + "  INNER JOIN obs o ON pa.patient_id=o.person_id "
            + "  INNER JOIN ("
            + "                SELECT patient_id, DATE_ADD(MAX(return_date), INTERVAL 28 DAY) AS return_date "
            + "                FROM (SELECT p.patient_id, "
            + "                             (SELECT o.value_datetime AS return_date "
            + "                              FROM encounter e "
            + "                                       INNER JOIN obs o "
            + "                                                  ON e.encounter_id = o.encounter_id "
            + "                              WHERE p.patient_id = e.patient_id "
            + "                                AND e.voided = 0 "
            + "                                AND o.voided = 0 "
            + "                                AND e.encounter_type = %d "
            + "                                AND e.location_id = :location "
            + "                                AND o.concept_id = %d "
            + "                                AND e.encounter_datetime <= :endDate "
            + "                              ORDER BY e.encounter_datetime DESC "
            + "                              LIMIT 1) AS return_date "
            + "                      FROM patient p "
            + "                      UNION "
            + "                      SELECT p.patient_id, "
            + "                             (SELECT o.value_datetime AS return_date "
            + "                              FROM encounter e "
            + "                                       INNER JOIN obs o "
            + "                                                  ON e.encounter_id = o.encounter_id "
            + "                              WHERE p.patient_id = e.patient_id "
            + "                                AND e.voided = 0 "
            + "                                AND o.voided = 0 "
            + "                                AND e.encounter_type IN (%d, %d) "
            + "                                AND e.location_id = :location "
            + "                                AND o.concept_id = %d "
            + "                                AND e.encounter_datetime <= :endDate "
            + "                              ORDER BY e.encounter_datetime DESC "
            + "                              LIMIT 1) AS return_date "
            + "                      FROM patient p "
            + "                      UNION "
            + "                      SELECT p.patient_id, "
            + "                             (SELECT DATE_ADD(o.value_datetime, INTERVAL 30 DAY) AS return_date "
            + "                              FROM encounter e "
            + "                                       INNER JOIN obs o "
            + "                                                  ON e.encounter_id = o.encounter_id "
            + "                              WHERE p.patient_id = e.patient_id "
            + "                                AND e.voided = 0 "
            + "                                AND o.voided = 0 "
            + "                                AND e.encounter_type = %d "
            + "                                AND e.location_id = :location "
            + "                                AND o.concept_id = %d "
            + "                                AND o.value_datetime <= :endDate "
            + "                              ORDER BY e.encounter_datetime DESC "
            + "                              LIMIT 1) AS return_date "
            + "                      FROM patient p) e "
            + "                GROUP BY e.patient_id)lp ON pa.patient_id=lp.patient_id "
            + "  WHERE e.encounter_datetime >= lp.return_date AND e.encounter_datetime<=:endDate"
            + "  AND e.encounter_type IN (%d, %d, %d) "
            + "  AND e.location_id=:location  "
            + "  GROUP BY pa.patient_id"
            + ") "
            + " GROUP BY pa.patient_id";

    return String.format(
        query,
        pharmacyEncounterTypeId,
        returnVisitDateForDrugsConcept,
        adultoSequimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        returnVisitDateConcept,
        masterCardDrugPickupEncounterTypeId,
        artDatePickupConceptId,
        homeVisitCardEncounterTypeId,
        apoioReintegracaoParteAEncounterTypeId,
        apoioReintegracaoParteBEncounterTypeId);
  }

  // Traced Patients (Unable to locate)
  public static String getPatientsTracedWithVisitCard(
      int pharmacyEncounterTypeId,
      int adultoSequimentoEncounterTypeId,
      int arvPediatriaSeguimentoEncounterTypeId,
      int returnVisitDateForDrugsConcept,
      int returnVisitDateConcept,
      int masterCardDrugPickupEncounterTypeId,
      int homeVisitCardEncounterTypeId,
      int apoioReintegracaoParteAEncounterTypeId,
      int apoioReintegracaoParteBEncounterTypeId,
      int typeOfVisitConcept,
      int buscaConcept,
      int patientFoundConcept,
      int patientFoundAnswerConcept,
      int artDatePickupConceptId) {
    String query =
        "SELECT DISTINCT pa.patient_id FROM patient pa "
            + "    INNER JOIN ( "
            + "    SELECT patient_id, DATE_ADD(MAX(return_date), INTERVAL 28 DAY) AS return_date "
            + "    FROM (SELECT p.patient_id, "
            + "                 (SELECT o.value_datetime AS return_date "
            + "                  FROM encounter e "
            + "                           INNER JOIN obs o "
            + "                                      ON e.encounter_id = o.encounter_id "
            + "                  WHERE p.patient_id = e.patient_id "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.encounter_type = %d "
            + "                    AND e.location_id = :location "
            + "                    AND o.concept_id = %d "
            + "                    AND e.encounter_datetime <= :endDate"
            + "                  ORDER BY e.encounter_datetime DESC "
            + "                  LIMIT 1) AS return_date "
            + "          FROM patient p "
            + "          UNION "
            + "          SELECT p.patient_id, "
            + "                 (SELECT o.value_datetime AS return_date "
            + "                  FROM encounter e "
            + "                           INNER JOIN obs o "
            + "                                      ON e.encounter_id = o.encounter_id "
            + "                  WHERE p.patient_id = e.patient_id "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.encounter_type IN (%d, %d) "
            + "                    AND e.location_id = :location "
            + "                    AND o.concept_id = %d "
            + "                    AND e.encounter_datetime <= :endDate"
            + "                  ORDER BY e.encounter_datetime DESC "
            + "                  LIMIT 1) AS return_date "
            + "          FROM patient p "
            + "          UNION "
            + "          SELECT p.patient_id, "
            + "                 (SELECT DATE_ADD(o.value_datetime, INTERVAL 30 DAY) AS return_date "
            + "                  FROM encounter e "
            + "                           INNER JOIN obs o "
            + "                                      ON e.encounter_id = o.encounter_id "
            + "                  WHERE p.patient_id = e.patient_id "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.encounter_type = %d "
            + "                    AND e.location_id = :location "
            + "                    AND o.concept_id = %d "
            + "                    AND o.value_datetime <= :endDate"
            + "                  ORDER BY e.encounter_datetime DESC "
            + "                  LIMIT 1) AS return_date "
            + "          FROM patient p) e "
            + "    GROUP BY e.patient_id)lp ON pa.patient_id=lp.patient_id "
            + "    INNER JOIN encounter e ON "
            + "        pa.patient_id=e.patient_id AND "
            + "        e.encounter_type IN (%d,%d,%d) AND "
            + "        e.location_id = :location AND "
            + "        e.encounter_datetime >= lp.return_date AND "
            + "        e.encounter_datetime <= :endDate "
            + "    INNER JOIN obs visitType ON "
            + "        pa.patient_id=visitType.person_id AND "
            + "        visitType.encounter_id=e.encounter_id AND "
            + "        (visitType.concept_id=%d AND visitType.value_coded=%d) "
            + "    LEFT JOIN obs patientNotFound ON "
            + "        pa.patient_id=patientNotFound.person_id AND "
            + "        patientNotFound.encounter_id=e.encounter_id AND "
            + "        (patientNotFound.concept_id=%d AND patientNotFound.value_coded=%d) "
            + " WHERE patientNotFound.obs_id IS NOT NULL "
            + " ORDER BY pa.patient_id ";

    return String.format(
        query,
        pharmacyEncounterTypeId,
        returnVisitDateForDrugsConcept,
        adultoSequimentoEncounterTypeId,
        arvPediatriaSeguimentoEncounterTypeId,
        returnVisitDateConcept,
        masterCardDrugPickupEncounterTypeId,
        artDatePickupConceptId,
        homeVisitCardEncounterTypeId,
        apoioReintegracaoParteAEncounterTypeId,
        apoioReintegracaoParteBEncounterTypeId,
        typeOfVisitConcept,
        buscaConcept,
        patientFoundConcept,
        patientFoundAnswerConcept);
  }

  public static String getPatientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup(
      int adultoSeguimentoEncounterType,
      int ARVPediatriaSeguimentoEncounterType,
      int aRVPharmaciaEncounterType,
      int masterCardDrugPickupEncounterType,
      int returnVisitDateConcept,
      int returnVisitDateForArvDrugConcept,
      int getArtDatePickupMasterCard) {
    Map<String, Integer> map = new HashMap<>();
    map.put("adultoSeguimentoEncounterType", adultoSeguimentoEncounterType);
    map.put("ARVPediatriaSeguimentoEncounterType", ARVPediatriaSeguimentoEncounterType);
    map.put("aRVPharmaciaEncounterType", aRVPharmaciaEncounterType);
    map.put("masterCardDrugPickupEncounterType", masterCardDrugPickupEncounterType);
    map.put("returnVisitDateConcept", returnVisitDateConcept);
    map.put("returnVisitDateForArvDrugConcept", returnVisitDateForArvDrugConcept);
    map.put("getArtDatePickupMasterCard", getArtDatePickupMasterCard);

    String query =
        " SELECT ps.patient_id "
            + "   FROM (   "
            + "         SELECT pm.patient_id "
            + "         FROM"
            + "          (SELECT p.patient_id AS patient_id"
            + "       FROM patient p "
            + "       WHERE  p.voided = 0 "
            + "           AND p.patient_id NOT IN "
            + "               ("
            + "               SELECT patient_id "
            + "                   FROM encounter e"
            + "						INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + "                   WHERE  e.encounter_type = ${masterCardDrugPickupEncounterType}  "
            + "                       AND e.location_id = :location "
            + "                       AND e.voided = 0"
            + "						  AND o.voided = 0"
            + "						  AND o.value_datetime >= :onOrAfter  "
            + "						  AND o.value_datetime <= :onOrBefore  "
            + "						  AND o.concept_id = ${getArtDatePickupMasterCard})) pm "
            + "       INNER JOIN ( "
            + "       Select ficha.patient_id "
            + "       from ( "
            + "           SELECT q1.patient_id "
            + "           from "
            + "               ( "
            + "               SELECT p.patient_id, "
            + "                   Max(e.encounter_datetime) as max_enc_datetime, Max(e.encounter_id) AS encounter_id "
            + "               FROM patient p "
            + "                   INNER JOIN encounter e "
            + "                   ON e.patient_id = p.patient_id "
            + "                   INNER JOIN obs o "
            + "                   ON o.encounter_id = e.encounter_id "
            + "               WHERE  p.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND o.voided = 0 "
            + "                   AND e.encounter_type IN (${adultoSeguimentoEncounterType},${ARVPediatriaSeguimentoEncounterType}) "
            + "                   AND e.encounter_datetime >= :onOrAfter "
            + "                   AND e.encounter_datetime <= :onOrBefore "
            + "                   AND e.location_id = :location "
            + "               GROUP  BY p.patient_id ) q1 "
            + "               left join obs o2 on o2.encounter_id=q1.encounter_id and "
            + "                   o2.concept_id = ${returnVisitDateConcept} and o2.voided=0 "
            + "               where  o2.obs_id  is null) ficha "
            + "           INNER JOIN ( "
            + "               SELECT q2.patient_id "
            + "               from ( "
            + "                   SELECT p.patient_id, "
            + "                       Max(e.encounter_datetime) as max_enc_datetime, Max(e.encounter_id) AS encounter_id "
            + "                   FROM patient p "
            + "                       INNER JOIN encounter e "
            + "                       ON e.patient_id = p.patient_id "
            + "                       INNER JOIN obs o "
            + "                       ON o.encounter_id = e.encounter_id "
            + "                   WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.encounter_type IN (${aRVPharmaciaEncounterType}) "
            + "                       AND e.encounter_datetime >= :onOrAfter "
            + "                       And e.encounter_datetime <= :onOrBefore "
            + "                       AND e.location_id = :location "
            + "                   GROUP  BY p.patient_id  "
            + "               )q2 "
            + "               left join obs o1 on o1.encounter_id=q2.encounter_id and "
            + "                       o1.concept_id = ${returnVisitDateForArvDrugConcept} and o1.voided=0 "
            + "               where  o1.obs_id is null "
            + "           ) fila ON ficha.patient_id=fila.patient_id ) filaficha on filaficha.patient_id=pm.patient_id "
            + "       )ps "
            + "       GROUP  BY ps.patient_id";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query);
  }

  public static String getPatientsDeadInProgramStateByReportingEndDate(
      int artProgram, int artDeadWorkflowState) {
    Map<String, Integer> map = new HashMap<>();
    map.put("artProgram", artProgram);
    map.put("artDeadWorkflowState", artDeadWorkflowState);
    String query =
        " select p.patient_id from patient p "
            + " inner join patient_program pg on p.patient_id=pg.patient_id "
            + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
            + " where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id=${artProgram} "
            + " and ps.state =${artDeadWorkflowState} and ps.end_date is null and ps.start_date<=:onOrBefore "
            + "and pg.location_id=:location group by p.patient_id  ";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query);
  }
}
