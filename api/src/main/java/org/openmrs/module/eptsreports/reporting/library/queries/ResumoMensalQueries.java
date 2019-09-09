/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.queries;

public class ResumoMensalQueries {

  /**
   * Get the header and footer information for the report
   *
   * @param location_attribute_id
   * @return String
   */
  public static String getDefaultSettings(int location_attribute_id) {

    return "SELECT la.value_reference AS code, l.name AS name, DATE_FORMAT(now(), '%d-%m-%Y %H:%i:%s') AS date_time, l.state_province AS province, l.county_district AS district FROM location l INNER JOIN location_attribute la ON l.location_id=la.location_id INNER JOIN location_attribute_type lat ON la.attribute_type_id=lat.location_attribute_type_id WHERE l.location_id=:location AND lat.location_attribute_type_id="
        + location_attribute_id;
  }

  /**
   * All patients with encounter type 53, and Pre-ART Start Date that is less than startDate
   *
   * @return String
   */
  public static String getAllPatientsWithPreArtStartDateLessThanReportingStartDate(
      int encounterType, int conceptId) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location AND o.value_datetime IS NOT NULL AND o.concept_id=%d AND o.value_datetime <:startDate";
    return String.format(query, encounterType, conceptId);
  }
  /**
   * All patients with encounter type 53, and Pre-ART Start Date with Transfer from other HF Concept
   * ID 1369 and answer Concept ID 1065
   *
   * @return String
   */
  public static String getPatientsTransferredFromOtherHealthFacility(
      int qtnConceptId, int ansConceptId, int encounterType) {

    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location AND o.concept_id=%d AND o.value_coded=%d AND e.encounter_type=%d";
    return String.format(query, qtnConceptId, ansConceptId, encounterType);
  }

  /**
   * All patients with encounter type 53, and Pre-ART Start Date that falls between startDate and
   * enddate
   *
   * @return String
   */
  public static String getAllPatientsWithPreArtStartDateWithBoundaries(
      int encounterType, int conceptId) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location AND o.value_datetime IS NOT NULL  AND o.value_datetime BETWEEN :startDate AND :endDate AND o.concept_id=%d";
    return String.format(query, encounterType, conceptId);
  }

  /**
   * Patients who had a drug pick, i.e. at least one encounter “Levantamento de ARV Master Card
   *
   * @return String
   */
  public static String getPatientsWhoHadDrugPickUpInMasterCard(
      int qtnConceptId, int encounterType) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location AND o.concept_id=%d AND e.encounter_type=%d AND o.value_datetime IS NOT NULL  AND o.value_datetime BETWEEN :startDate AND :endDate";
    return String.format(query, qtnConceptId, encounterType);
  }

  /**
   * Type of Patient Transferred From
   *
   * @return String
   */
  public static String getTypeOfPatientTransferredFrom(
      int qtnConceptId, int ansConceptId, int encounterType) {
    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.location_id=:location AND o.concept_id=%d AND o.value_coded=%d AND e.encounter_type=%d";
    return String.format(query, qtnConceptId, ansConceptId, encounterType);
  }

  /**
   * Number of patients transferred-in from another HFs during the current month
   *
   * @return String
   */
  public static String getPatientsTransferredFromAnotherHealthFacilityDuringTheCurrentMonth(
      int masterCardEncounter,
      int transferFromConcept,
      int yesConcept,
      int typeOfPantientConcept,
      int tarvConcept,
      int dateTransferredConcept) {

    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND o.concept_id=%d AND o.value_coded=%d "
            + " UNION "
            + " SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND o.concept_id=%d AND o.value_coded=%d "
            + " UNION "
            + " SELECT p.patient_id FROM patient p INNER JOIN encounter e ON p.patient_id=e.patient_id INNER JOIN obs o ON o.encounter_id=e.encounter_id "
            + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND e.encounter_type=%d AND e.location_id=:location "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate AND o.concept_id=%d "
            + " AND o.obs_datetime BETWEEN :startDate AND :endDate";

    return String.format(
        query,
        masterCardEncounter,
        transferFromConcept,
        yesConcept,
        masterCardEncounter,
        typeOfPantientConcept,
        tarvConcept,
        masterCardEncounter,
        dateTransferredConcept);
  }

  /**
   * B.3: Number of patients who restarted the treatment during the current month
   *
   * @return String
   */
  public static String getPatientsWhoRestartedTreatmentDuringCurrentMonth(
      int adultSegEncounter, int patientStateConcept, int startDrugsConcept) {
    String query =
        "SELECT pa.patient_id "
            + "FROM   patient pa "
            + "       INNER JOIN encounter e "
            + "               ON pa.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "               ON e.encounter_id = o.encounter_id "
            + "WHERE  e.encounter_type =%d "
            + "       AND o.concept_id =%d "
            + "       AND o.value_coded =%d "
            + "       AND pa.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime BETWEEN :startDate and :endDate";

    return String.format(query, adultSegEncounter, patientStateConcept, startDrugsConcept);
  }

  public static String getPatientsTransferredOutDuringCurrentMonth(
      int adultSegEncounter, int stateOfStayConcept, int transferredOutConcept) {
    String query =
        "SELECT pa.patient_id "
            + "FROM   patient pa "
            + "       INNER JOIN encounter e "
            + "               ON pa.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "               ON e.encounter_id = o.encounter_id "
            + "WHERE  e.encounter_type = %d "
            + "       AND o.concept_id = %d "
            + "       AND o.value_coded = %d "
            + "       AND pa.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime BETWEEN :startDate and :endDate";
    return String.format(query, adultSegEncounter, stateOfStayConcept, transferredOutConcept);
  }

  public static String getPatientsWithArtSuspensionDuringCurrentMonth(
      int adultSegEncounter, int stateOfStayConcept, int suspendTreatmentConcept) {
    String query =
        "SELECT pa.patient_id "
            + "FROM   patient pa "
            + "       INNER JOIN encounter e "
            + "               ON pa.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "               ON e.encounter_id = o.encounter_id "
            + "WHERE  e.encounter_type = %d "
            + "       AND o.concept_id = %d "
            + "       AND o.value_coded = %d "
            + "       AND pa.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime BETWEEN :startDate and :endDate";
    return String.format(query, adultSegEncounter, stateOfStayConcept, suspendTreatmentConcept);
  }

  public static String getPatientsWhoAbandonedArtDuringCurrentMonth(
      int drugPickupEncounterType, int artPickupDate) {
    String query =
        "SELECT patient_id "
            + "FROM   (SELECT patient_id, "
            + "               Max(encounter_datetime) encounter_datetime "
            + "        FROM   encounter "
            + "        WHERE  encounter_type = %d "
            + "               AND encounter_datetime <= :endDate "
            + "               AND location_id = :location"
            + "        GROUP  BY patient_id) last_pickup "
            + "       JOIN obs o "
            + "         ON o.person_id = last_pickup.patient_id "
            + "WHERE  o.obs_datetime = last_pickup.encounter_datetime "
            + "       AND o.voided = 0 "
            + "       AND o.concept_id =%d "
            + "       AND location_id = :location"
            + "       AND TIMESTAMPDIFF(day, o.value_datetime, :endDate) >= 90 ";
    return String.format(query, drugPickupEncounterType, artPickupDate);
  }

  public static String getPatientsWhoDiedDuringCurrentMonth(
      int adultSegEncounter, int stateOfStayConcept, int patientHasDiedConcept) {
    String query =
        "SELECT pa.patient_id "
            + "FROM   patient pa "
            + "       INNER JOIN encounter e "
            + "               ON pa.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "               ON e.encounter_id = o.encounter_id "
            + "WHERE  e.encounter_type = %d "
            + "       AND o.concept_id = %d "
            + "       AND o.value_coded = %d "
            + "       AND pa.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime BETWEEN :startDate and :endDate";
    return String.format(query, adultSegEncounter, stateOfStayConcept, patientHasDiedConcept);
  }

  public static String getAllPatientsWithArtStartDateWithBoundaries(
      int masterCardEncounterType, int artStartDateConceptId) {
    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.encounter_type =%d "
            + "       AND e.location_id = :location "
            + "       AND o.value_datetime IS NOT NULL "
            + "       AND o.value_datetime BETWEEN :startDate AND :endDate "
            + "       AND o.concept_id =%d";
    return String.format(query, masterCardEncounterType, artStartDateConceptId);
  }
}
