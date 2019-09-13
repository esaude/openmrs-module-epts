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
      int tarvConcept) {

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs transf "
            + "         ON transf.encounter_id = e.encounter_id "
            + "       JOIN obs type "
            + "         ON type.encounter_id = e.encounter_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.encounter_type = %d "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "       AND transf.voided = 0 "
            + "       AND transf.concept_id = %d "
            + "       AND transf.value_coded = %d "
            + "       AND transf.obs_datetime BETWEEN :startDate AND :endDate "
            + "       AND type.voided = 0 "
            + "       AND type.concept_id = %d "
            + "       AND type.value_coded = %d";

    return String.format(
        query,
        masterCardEncounter,
        transferFromConcept,
        yesConcept,
        typeOfPantientConcept,
        tarvConcept);
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

  public static String getAllPatientsWithArtStartDateBeforeDate(
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
            + "       AND o.value_datetime < :onOrBefore "
            + "       AND o.concept_id =%d";
    return String.format(query, masterCardEncounterType, artStartDateConceptId);
  }

  public static String getPatientsWhoHadDrugPickUpBeforeDate(
      int masterCardEncounterType, int drugPickupDateConceptId) {
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
            + "       AND o.value_datetime < :onOrBefore "
            + "       AND o.concept_id =%d";
    return String.format(query, masterCardEncounterType, drugPickupDateConceptId);
  }

  public static String getPatientsTransferredFromAnotherHealthFacilityByEndOfPreviousMonth(
      int masterCardEncounter,
      int transferFromConcept,
      int yesConcept,
      int typeOfPantientConcept,
      int tarvConcept) {

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       JOIN encounter e "
            + "         ON p.patient_id = e.patient_id "
            + "       JOIN obs transf "
            + "         ON transf.encounter_id = e.encounter_id "
            + "       JOIN obs type "
            + "         ON type.encounter_id = e.encounter_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.encounter_type = %d "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime < :onOrBefore "
            + "       AND transf.voided = 0 "
            + "       AND transf.concept_id = %d "
            + "       AND transf.value_coded = %d "
            + "       AND transf.obs_datetime < :onOrBefore "
            + "       AND type.voided = 0 "
            + "       AND type.concept_id = %d "
            + "       AND type.value_coded = %d";

    return String.format(
        query,
        masterCardEncounter,
        transferFromConcept,
        yesConcept,
        typeOfPantientConcept,
        tarvConcept);
  }
}
