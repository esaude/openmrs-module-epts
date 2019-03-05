package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Calculates for patient eligibility to be pregnant
 *
 * @return CalculationResultMap
 */
@Component
public class PregnantCalculation extends AbstractPatientCalculation {

  @Autowired private HivMetadata hivMetadata;

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    Location location = (Location) context.getFromCache("location");
    Date oneYearBefore = EptsCalculationUtils.addMonths(context.getNow(), -12);

    EncounterType labEncounterType = this.hivMetadata.getMisauLaboratorioEncounterType();
    EncounterType adultFollowup = this.hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType pediatriaFollowup = this.hivMetadata.getARVPediatriaSeguimentoEncounterType();

    Concept viralLoadConcept = this.hivMetadata.getHivViralLoadConcept();
    Concept pregnant = this.hivMetadata.getPregnantConcept();
    Concept pregnantBasedOnWeeks = this.hivMetadata.getNumberOfWeeksPregnant();
    Concept pregnancyDueDate = this.hivMetadata.getPregnancyDueDate();
    Program ptv = this.hivMetadata.getPtvEtvProgram();
    Concept gestation = this.hivMetadata.getGestationConcept();

    // get female patients only
    Set<Integer> femaleCohort = EptsCalculationUtils.female(cohort, context);

    CalculationResultMap pregnantMap =
        EptsCalculations.getObs(
            pregnant,
            femaleCohort,
            Arrays.asList(location),
            Arrays.asList(gestation),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap markedPregnantByWeeks =
        EptsCalculations.getObs(
            pregnantBasedOnWeeks,
            femaleCohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap markedPregnantDueDate =
        EptsCalculations.getObs(
            pregnancyDueDate,
            femaleCohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap markedPregnantInProgram =
        EptsCalculations.allProgramEnrollment(ptv, femaleCohort, context);

    CalculationResultMap lastVl =
        EptsCalculations.lastObs(
            Arrays.asList(labEncounterType, adultFollowup, pediatriaFollowup),
            viralLoadConcept,
            location,
            oneYearBefore,
            context.getNow(),
            femaleCohort,
            context);

    for (Integer pId : femaleCohort) {
      Boolean isCandidate = false;
      Obs lastVlObs = EptsCalculationUtils.resultForPatient(lastVl, pId);

      if (lastVlObs != null && lastVlObs.getObsDatetime() != null) {
        Date lastVlDate = lastVlObs.getObsDatetime();

        ListResult pregnantResult = (ListResult) pregnantMap.get(pId);
        ListResult pregnantByWeeksResullt = (ListResult) markedPregnantByWeeks.get(pId);
        ListResult pregnantDueDateResult = (ListResult) markedPregnantDueDate.get(pId);
        ListResult pregnantsInProgramResults = (ListResult) markedPregnantInProgram.get(pId);

        List<Obs> pregnantObsList = EptsCalculationUtils.extractResultValues(pregnantResult);
        List<Obs> pregnantByWeeksObsList =
            EptsCalculationUtils.extractResultValues(pregnantByWeeksResullt);
        List<Obs> pregnantDueDateObsList =
            EptsCalculationUtils.extractResultValues(pregnantDueDateResult);
        List<PatientProgram> patientProgams =
            EptsCalculationUtils.extractResultValues(pregnantsInProgramResults);

        if (this.isPregnant(lastVlDate, pregnantObsList)
            || this.isPregnantByWeeks(lastVlDate, pregnantByWeeksObsList)
            || this.isPregnantDueDate(lastVlDate, pregnantDueDateObsList)
            || this.isPregnantInProgram(lastVlDate, patientProgams, location)) {
          isCandidate = true;
        }
      }
      resultMap.put(pId, new BooleanResult(isCandidate, this));
    }
    return resultMap;
  }

  private boolean isPregnant(Date lastVlDate, List<Obs> pregnantObsList) {

    for (Obs obs : pregnantObsList) {
      if (this.isInPregnantViralLoadRange(lastVlDate, obs.getEncounter().getEncounterDatetime())) {
        return true;
      }
    }
    return false;
  }

  private boolean isPregnantByWeeks(Date lastVlDate, List<Obs> pregnantByWeeksObsList) {

    for (Obs obs : pregnantByWeeksObsList) {
      if (obs.getValueNumeric() != null
          && this.isInPregnantViralLoadRange(
              lastVlDate, obs.getEncounter().getEncounterDatetime())) {
        return true;
      }
    }
    return false;
  }

  private boolean isPregnantDueDate(Date lastVlDate, List<Obs> pregnantDueDateObsList) {

    for (Obs obs : pregnantDueDateObsList) {
      if (this.isInPregnantViralLoadRange(lastVlDate, obs.getEncounter().getEncounterDatetime())) {
        return true;
      }
    }
    return false;
  }

  private boolean isPregnantInProgram(
      Date lastVlDate, List<PatientProgram> patientPrograms, Location location) {

    for (PatientProgram patientProgram : patientPrograms) {
      if (location.equals(patientProgram.getLocation())
          && patientProgram.getDateEnrolled() != null
          && this.isInPregnantViralLoadRange(lastVlDate, patientProgram.getDateEnrolled())) {
        return true;
      }
    }
    return false;
  }

  private boolean isInPregnantViralLoadRange(Date viralLoadDate, Date pregnancyDate) {

    Date startDate = EptsCalculationUtils.addMonths(viralLoadDate, -9);
    return pregnancyDate.compareTo(startDate) >= 0 && pregnancyDate.compareTo(viralLoadDate) <= 0;
  }
}
