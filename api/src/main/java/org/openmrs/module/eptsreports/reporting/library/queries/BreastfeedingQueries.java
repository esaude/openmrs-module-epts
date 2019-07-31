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

public class BreastfeedingQueries {

  public static String getPatientsWhoGaveBirthWithinReportingPeriod(
      int etvProgram, int patientState) {
    return "select 	pg.patient_id"
        + " from patient p"
        + " inner join patient_program pg on p.patient_id=pg.patient_id"
        + " inner join patient_state ps on pg.patient_program_id=ps.patient_program_id"
        + " where pg.voided=0 and ps.voided=0 and p.voided=0 and"
        + " pg.program_id="
        + etvProgram
        + " and ps.state="
        + patientState
        + " and ps.end_date is null and"
        + " ps.start_date between :startDate and :endDate and location_id=:location";
  }

  public static String getLactatingPatients(
      int breastFeedingConcept, int yesValueConcept, int adultFollowupEncounterType) {
    String qry =
        "select max_visit.person_id "
            + "from ("
            + "select person_id, MAX(encounter_datetime) as edt "
            + "from obs "
            + "inner join encounter on obs.encounter_id = encounter.encounter_id "
            + "where obs.voided = false and obs.concept_id = %s  and obs.value_coded = %s "
            + "and encounter.encounter_type = %s "
            + "and obs.location_id in (:location) "
            + "and encounter.encounter_datetime >= :startDate "
            + "and encounter.encounter_datetime <= :endDate "
            + "group by person_id "
            + ") max_visit";

    return String.format(qry, breastFeedingConcept, yesValueConcept, adultFollowupEncounterType);
  }
}
