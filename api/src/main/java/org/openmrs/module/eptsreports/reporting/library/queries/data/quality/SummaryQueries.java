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
package org.openmrs.module.eptsreports.reporting.library.queries.data.quality;

import java.util.List;

/** This class will contain the queries for the summary indicator page That aggregate them all */
public class SummaryQueries {

  /** GRAVIDAS INSCRITAS NO SERVIÇO TARV */
  public static String getPregnantPatients(
      int pregnantConcept,
      int gestationConcept,
      int weeksPregnantConcept,
      int eddConcept,
      int adultInitailEncounter,
      int adultSegEncounter,
      int etvProgram) {

    return "SELECT  p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + pregnantConcept
        + " AND value_coded="
        + gestationConcept
        + " AND e.encounter_type IN ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location)"
        + " AND pe.gender ='M'"
        + " UNION"
        + " SELECT p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + weeksPregnantConcept
        + " AND"
        + " e.encounter_type IN ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " AND pe.gender ='M'"
        + " UNION"
        + " SELECT p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + eddConcept
        + " AND"
        + " e.encounter_type in ("
        + adultInitailEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " AND pe.gender ='M'"
        + " UNION"
        + " SELECT pp.patient_id FROM patient_program pp"
        + " INNER JOIN person pe ON pp.patient_id=pe.person_id"
        + " WHERE pp.program_id="
        + etvProgram
        + " AND pp.voided=0 AND pp.location_id IN(:location) "
        + " AND pe.gender ='M'";
  }

  /**
   * Get the raw sql query for breastfeeding male patients
   *
   * @return String
   */
  public static String getBreastfeedingMalePatients(
      int deliveryDateConcept,
      int arvInitiationConcept,
      int lactationConcept,
      int registeredBreastfeedingConcept,
      int yesConcept,
      int ptvProgram,
      int gaveBirthState,
      int adultInitialEncounter,
      int adultSegEncounter) {

    return " SELECT p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + deliveryDateConcept
        + " AND"
        + " e.encounter_type in ("
        + adultInitialEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " AND pe.gender ='M'"
        + " UNION "
        + "SELECT  p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + arvInitiationConcept
        + " AND value_coded="
        + lactationConcept
        + " AND e.encounter_type IN ("
        + adultInitialEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location)"
        + " AND pe.gender ='M'"
        + " UNION "
        + " SELECT p.patient_id"
        + " FROM patient p"
        + " INNER JOIN person pe ON p.patient_id=pe.person_id"
        + " INNER JOIN encounter e ON p.patient_id=e.patient_id"
        + " INNER JOIN obs o ON e.encounter_id=o.encounter_id"
        + " WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id="
        + registeredBreastfeedingConcept
        + " AND value_coded="
        + yesConcept
        + " AND"
        + " e.encounter_type IN ("
        + adultInitialEncounter
        + ","
        + adultSegEncounter
        + ") AND e.location_id IN(:location) "
        + " AND pe.gender ='M'"
        + "UNION "
        + "SELECT 	pg.patient_id"
        + " FROM patient p"
        + " INNER JOIN patient_program pg ON p.patient_id=pg.patient_id"
        + " INNER JOIN person pe ON pg.patient_id=pe.person_id"
        + " INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id"
        + " WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND"
        + " pg.program_id="
        + ptvProgram
        + " AND ps.state="
        + gaveBirthState
        + " AND ps.end_date IS NULL AND"
        + " location_id IN(:location)"
        + " AND pe.gender ='M'";
  }

  /**
   * Get patients whose year of birth is before 1920
   *
   * @return String
   */
  public static String getPatientsWhoseYearOfBirthIsBeforeYear(int year) {
    ;
    return "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND YEAR(pe.birthdate) <"
        + year;
  }

  /**
   * Get patients whose date of birth, estimated date of birth or age is negative
   *
   * @return String
   */
  public static String getPatientsWithNegativeBirthDates() {
    return "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NULL"
        + " UNION "
        + "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND pe.birthdate > pe.date_created";
  }

  /**
   * The patients birth, estimated date of birth or age indicates they are > 100 years of age
   *
   * @return String
   */
  public static String getPatientsWithMoreThanXyears(int years) {
    return "SELECT pa.patient_id FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL AND TIMESTAMPDIFF(YEAR, pe.birthdate, :endDate) >"
        + years;
  }

  /**
   * The patient’s date of birth is after any drug pick up date
   *
   * @return String
   */
  public static String getPatientsWhoseBirthdateIsAfterDrugPickup(List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String str2 = str1.replaceAll("]", "");
    String query =
        "SELECT birth_date.patient_id FROM "
            + "((SELECT pa.patient_id, pe.birthdate AS birthdate FROM patient pa INNER JOIN person pe ON pa.patient_id=pe.person_id WHERE pe.birthdate IS NOT NULL) birth_date "
            + "INNER JOIN "
            + "(SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + "patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + "AND e.encounter_type IN (%s) AND e.location_id IN(:location) GROUP BY p.patient_id "
            + ") encounter ON birth_date.patient_id=encounter.patient_id) WHERE birth_date.birthdate > encounter.encounter_date";
    return String.format(query, str2);
  }

  /**
   * Get patients who have a given state before an encounter
   *
   * @return String
   */
  public static String getPatientsWithStateThatIsBeforeAnEncounter(
      int programId, int stateId, List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String str2 = str1.replaceAll("]", "");
    String query =
        "SELECT states.patient_id FROM "
            + "((SELECT pg.patient_id AS patient_id, ps.start_date AS start_date "
            + "FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 AND pg.program_id IN (%s) "
            + " AND ps.state IN (%s) "
            + " AND pg.location_id IN(:location) AND ps.end_date IS NULL GROUP BY pg.patient_id) states INNER JOIN "
            + "(SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + "patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + "AND e.encounter_type IN (%s) AND e.location_id IN(:location) GROUP BY p.patient_id"
            + ") encounter ON states.patient_id=encounter.patient_id) WHERE encounter.encounter_date > states.start_date";
    return String.format(query, programId, stateId, str2);
  }

  /**
   * Get patients who are marked as deceased and have a consultation after deceased date
   *
   * @return String
   */
  public static String getPatientsMarkedAsDeceasedAndHaveAnEncounter(List<Integer> encounterList) {
    String str1 = String.valueOf(encounterList).replaceAll("\\[", "");
    String encounters = str1.replaceAll("]", "");
    String query =
        "SELECT demo.patient_id FROM "
            + "((SELECT p.patient_id AS patient_id, pe.death_date AS death_date "
            + "FROM patient p "
            + "INNER JOIN person pe ON p.patient_id=pe.person_id "
            + "WHERE pe.death_date IS NOT NULL GROUP BY p.patient_id) demo INNER JOIN "
            + "(SELECT p.patient_id AS patient_id, MAX(e.encounter_datetime) AS encounter_date FROM "
            + "patient p INNER JOIN encounter e ON p.patient_id=e.patient_id WHERE p.voided = 0 and e.voided=0 "
            + "AND e.encounter_type IN (%s) AND e.location_id IN(:location) GROUP BY p.patient_id"
            + ") encounter ON demo.patient_id=encounter.patient_id) WHERE encounter.encounter_date > demo.death_date";
    return String.format(query, encounters);
  }
}
