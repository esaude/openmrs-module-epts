/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.calculation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiObsDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiPatientStateDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiProgramEnrollmentForPatientDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.util.OpenmrsUtil;

/**
 * Utility class of common base calculations TODO: refactor needs to be merged with
 * EptsCalculationUtils
 */
public class EptsCalculations {

  /**
   * Evaluates alive-ness of each patient
   *
   * @param cohort the patient ids
   * @param context the calculation context
   * @return the alive-nesses in a calculation result map
   */
  public static CalculationResultMap alive(
      Collection<Integer> cohort, PatientCalculationContext context) {
    VitalStatusDataDefinition def = new VitalStatusDataDefinition("alive");
    CalculationResultMap vitals =
        EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);

    CalculationResultMap ret = new CalculationResultMap();
    for (int ptId : cohort) {
      boolean alive = false;
      if (vitals.get(ptId) != null) {
        VitalStatus vs = (VitalStatus) vitals.get(ptId).getValue();
        alive =
            !vs.getDead()
                || OpenmrsUtil.compareWithNullAsEarliest(vs.getDeathDate(), context.getNow()) > 0;
      }
      ret.put(ptId, new BooleanResult(alive, null, context));
    }
    return ret;
  }

  /**
   * Evaluates all obs of a given type of each patient TODO: refactor this to filter on patient id
   * while fetching obs
   *
   * @param concept the obs' concept
   * @param cohort the patient ids
   * @param context the calculation context
   * @return the obss in a calculation result map
   */
  public static CalculationResultMap allObs(
      Concept concept, Collection<Integer> cohort, PatientCalculationContext context) {
    ObsForPersonDataDefinition def =
        new ObsForPersonDataDefinition(
            "all obs", TimeQualifier.ANY, concept, context.getNow(), null);
    return EptsCalculationUtils.ensureEmptyListResults(
        EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context), cohort);
  }

  /**
   * Evaluates the first obs of a given type of each patient
   *
   * @param concept the obs' concept
   * @param cohort the patient ids
   * @param context the calculation context
   * @return the obss in a calculation result map
   */
  public static CalculationResultMap firstObs(
      Concept concept, Collection<Integer> cohort, PatientCalculationContext context) {
    ObsForPersonDataDefinition def =
        new ObsForPersonDataDefinition(
            "first obs", TimeQualifier.FIRST, concept, context.getNow(), null);
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  /**
   * Evaluate for obs based on the time modifier
   *
   * @param concept
   * @param cohort
   * @param locationList
   * @param timeQualifier
   * @param context
   * @return
   */
  public static CalculationResultMap getObs(
      Concept concept,
      Collection<Integer> cohort,
      List<Location> locationList,
      List<Concept> valueCodedList,
      TimeQualifier timeQualifier,
      Date startDate,
      PatientCalculationContext context) {
    ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
    def.setName(timeQualifier.name() + "obs");
    def.setWhich(timeQualifier);
    def.setQuestion(concept);
    if (startDate != null) {
      def.setOnOrAfter(startDate);
    }
    if (valueCodedList != null && valueCodedList.size() > 0) {
      def.setValueCodedList(valueCodedList);
    }
    def.setOnOrBefore(context.getNow());
    if (locationList.size() > 0) {
      def.setLocationList(locationList);
    }

    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  /**
   * Evaluates the last obs of a given type of each patient
   *
   * @param concept the obs' concept
   * @param cohort the patient ids
   * @param context the calculation context
   * @return the obss in a calculation result map
   */
  public static CalculationResultMap lastObs(
      Concept concept, Collection<Integer> cohort, PatientCalculationContext context) {
    ObsForPersonDataDefinition def =
        new ObsForPersonDataDefinition(
            "last obs", TimeQualifier.LAST, concept, context.getNow(), null);
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  /**
   * Evaluates the last patient state for the specified programWorkflowState
   *
   * @param cohort
   * @param location
   * @param startDate
   * @param endDate
   * @param programWorkflowState
   * @param context
   * @return
   */
  public static CalculationResultMap allPatientStates(
      Collection<Integer> cohort,
      Location location,
      ProgramWorkflowState programWorkflowState,
      PatientCalculationContext context) {

    JembiPatientStateDefinition def = new JembiPatientStateDefinition();
    def.setLocation(location);
    def.setStartedOnOrBefore(context.getNow());
    def.setStates(Arrays.asList(programWorkflowState));
    def.setWhich(TimeQualifier.ANY);

    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  public static CalculationResultMap allProgramEnrollment(
      Program program, Collection<Integer> cohort, PatientCalculationContext context) {
    ProgramEnrollmentsForPatientDataDefinition def =
        new ProgramEnrollmentsForPatientDataDefinition();

    def.setName("All in " + program.getName());
    def.setWhichEnrollment(TimeQualifier.ANY);
    def.setProgram(program);
    def.setEnrolledOnOrBefore(context.getNow());

    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  /**
   * Evaluates the first encounter of a given type of each patient
   *
   * @param encounterType the encounter type
   * @param cohort the patient ids
   * @param context the calculation context
   * @return the encounters in a calculation result map
   */
  public static CalculationResultMap firstEncounter(
      EncounterType encounterType,
      Collection<Integer> cohort,
      Location location,
      PatientCalculationContext context) {
    EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
    if (encounterType != null) {
      def.setName("first encounter of type " + encounterType.getName());
      def.addType(encounterType);
    } else {
      def.setName("first encounter of any type");
    }
    def.setWhich(TimeQualifier.FIRST);
    def.setLocationList(Arrays.asList(location));
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  public static CalculationResultMap firstObs(
      Concept question,
      Concept answer,
      Location location,
      boolean sortByDatetime,
      Collection<Integer> cohort,
      PatientCalculationContext context) {
    JembiObsDefinition definition = new JembiObsDefinition("JembiObsDefinition");
    definition.setQuestion(question);
    definition.setAnswer(answer);
    definition.setLocation(location);
    definition.setSortByDatetime(sortByDatetime);
    return EptsCalculationUtils.evaluateWithReporting(definition, cohort, null, null, context);
  }

  public static CalculationResultMap firstPatientProgram(
      Program program,
      Location location,
      Collection<Integer> cohort,
      PatientCalculationContext context) {
    JembiProgramEnrollmentForPatientDefinition definition =
        new JembiProgramEnrollmentForPatientDefinition("First Patient Program");
    definition.setProgram(program);
    definition.setLocation(location);
    return EptsCalculationUtils.evaluateWithReporting(definition, cohort, null, null, context);
  }

  public static CalculationResultMap lastObs(
      List<EncounterType> encounterTypes,
      Concept concept,
      Location location,
      Date startDate,
      Date endDate,
      Collection<Integer> cohort,
      PatientCalculationContext context) {
    ObsForPersonDataDefinition definition = new ObsForPersonDataDefinition();
    definition.setName("last obs");
    definition.setEncounterTypeList(encounterTypes);
    definition.setQuestion(concept);
    definition.setLocationList(Arrays.asList(location));
    definition.setOnOrAfter(startDate);
    definition.setOnOrBefore(endDate);
    definition.setWhich(TimeQualifier.LAST);
    return EptsCalculationUtils.evaluateWithReporting(definition, cohort, null, null, context);
  }
}
