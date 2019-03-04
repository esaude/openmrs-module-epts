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
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.BreastfeedingCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.OnArtForMoreThanXmonthsCalcultion;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.PregnantCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.RoutineCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PatientsOnRoutineEnum;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TxNew Cohort Definition instances we want to expose for EPTS */
@Component
public class TxPvlsCohortQueries {

  @Autowired private HivCohortQueries hivCohortQueries;

  /**
   * Patients who have NOT been on ART for 3 months based on the ART initiation date and date of
   * last viral load registered
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreMoreThan3MonthsOnArt() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "On ART for at least 3 months",
            Context.getRegisteredComponents(OnArtForMoreThanXmonthsCalcultion.class).get(0));
    cd.addParameter(new Parameter("onDate", "On Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }

  /**
   * Breast feeding women with viral load suppression
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "breastfeedingWomenWithViralSuppression")
  public CohortDefinition getBreastfeedingWomenWhoHaveViralSuppression() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding with viral suppression");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getPatientsWhoAreBreastfeedingAndNotPregnant(),
            "endDate=${endDate},location=${location}"));

    cd.addSearch(
        "suppression",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppression(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("breastfeeding AND suppression");

    return cd;
  }

  /**
   * Breast feeding women with viral load suppression
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "breastfeedingWomenWithViralLoadResults")
  public CohortDefinition getBreastfeedingWomenWhoHaveViralLoadResults() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding with viral results");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getPatientsWhoAreBreastfeedingAndNotPregnant(),
            "endDate=${endDate},location=${location}"));

    cd.addSearch(
        "results",
        EptsReportUtils.map(
            getPatientsWithViralLoadResults(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("breastfeeding AND results");

    return cd;
  }

  /**
   * Patients with viral suppression of <1000 in the last 12 months excluding dead, LTFU,
   * transferred out, stopped ART
   */
  public CohortDefinition getPatientsWithViralLoadSuppression() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "supp",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            getPatientsWhoAreMoreThan3MonthsOnArt(), "onDate=${endDate},location=${location}"));
    cd.setCompositionString("supp AND onArtLongEnough");
    return cd;
  }

  /**
   * Patients with viral results recorded in the last 12 months excluding dead, LTFU, transferred
   * out, stopped ARTtxNewCohortQueries
   */
  public CohortDefinition getPatientsWithViralLoadResults() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(hivCohortQueries.getPatientsViralLoadWithin12Months(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            getPatientsWhoAreMoreThan3MonthsOnArt(), "onDate=${endDate},location=${location}"));
    cd.setCompositionString("results AND onArtLongEnough");
    return cd;
  }

  /**
   * Get adults and children patients who are on routine
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum criteria) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "criteria", Context.getRegisteredComponents(RoutineCalculation.class).get(0));
    cd.setName("Routine for all patients controlled by parameter");
    cd.addParameter(new Parameter("onDate", "On Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addCalculationParameter("criteria", criteria);
    return cd;
  }

  /**
   * Get patients having viral load suppression and routine for adults and children - Numerator
   *
   * @retrun CohortDefinition
   */
  public CohortDefinition getPatientWithViralSuppressionAndOnRoutineAdultsAndChildren() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Suppression and on routine adult and children");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch("supp", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.ADULTCHILDREN),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("supp AND routine");
    return cd;
  }

  /**
   * Get patients having viral load suppression and not documented for adults and children -
   * Numerator
   *
   * @retrun CohortDefinition
   */
  public CohortDefinition getPatientWithViralSuppressionAndNotDocumentedForAdultsAndChildren() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("suppression and not documented adults and children");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch("supp", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.ADULTCHILDREN),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("supp AND NOT routine");
    return cd;
  }

  /**
   * Get patients with viral load results and on routine - Denominator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithViralLoadREsultsAndOnRoutineForChildrenAndAdults() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Viral load results with routine for children and adults denominator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(), mappings));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.ADULTCHILDREN),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("results AND routine");
    return cd;
  }

  /**
   * Get patients with viral load results and NOT documented
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithViralLoadREsultsAndNotDocumenetdForChildrenAndAdults() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Viral load results with not documentation for children and adults denominator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(), mappings));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.ADULTCHILDREN),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("results AND NOT routine");
    return cd;
  }

  // breastfeeding and pregnant women
  // breast feeding Numerator.
  /**
   * Get breastfeding women on routine Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getBreastFeedingWomenOnRoutineNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding with viral results and on routine");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "breastfeedingVl",
        EptsReportUtils.map(
            getBreastfeedingWomenWhoHaveViralSuppression(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("breastfeedingVl AND routine");

    return cd;
  }

  /**
   * Get breastfeeding women NOT documented Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getBreastFeedingWomenNotDocumentedNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding with viral results and NOT documented");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "breastfeedingVl",
        EptsReportUtils.map(
            getBreastfeedingWomenWhoHaveViralSuppression(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("breastfeedingVl AND NOT routine");

    return cd;
  }

  /**
   * Get pregnant women Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantWomenWithViralLoadSuppressionNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get pregnant women with viral load suppression");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "suppression", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            this.getPatientsWhoArePregnantCohort(), "onDate=${endDate},location=${location}"));
    cd.setCompositionString("suppression AND pregnant");
    return cd;
  }

  /**
   * Get pregnant women Denominator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantWomenWithViralLoadResultsDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get pregnant women with viral load results denominator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(), mappings));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            this.getPatientsWhoArePregnantCohort(), "onDate=${endDate},location=${location}"));
    cd.setCompositionString("results AND pregnant");
    return cd;
  }

  /**
   * Get pregnant, has viral load suppression, and on routine Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantAndOnRoutineNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Pregnant and on Routine Numerator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            getPregnantWomenWithViralLoadSuppressionNumerator(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("pregnant AND routine");
    return cd;
  }

  /**
   * Get pregnant, has viral load, and NOT documented Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantAndNotDocumentedNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Pregnant and NOT documented Numerator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            getPregnantWomenWithViralLoadSuppressionNumerator(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("pregnant AND NOT routine");
    return cd;
  }

  // Pregnant and breastfeeding denominator
  // Breastfeeding
  /**
   * Patients who have viral load results, breastfeeding and on routine denominator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getBreastfeedingWomenOnRoutineWithViralLoadResultsDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding women on routine and have Viral load results");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "vlandBreastfeeding",
        EptsReportUtils.map(
            getBreastfeedingWomenWhoHaveViralLoadResults(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("vlandBreastfeeding AND routine");
    return cd;
  }

  /**
   * Patients who have virial load results and NOT documented and are breastfeeding denominator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getBreastfeedingWomenAndNotDocumentedWithViralLoadResultsDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding women NOT documented and have Viral load results");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "vlandBreastfeeding",
        EptsReportUtils.map(
            getBreastfeedingWomenWhoHaveViralLoadResults(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("vlandBreastfeeding AND NOT routine");
    return cd;
  }

  // Pregnant
  /**
   * Patients who are pregnant, have viral load results and on routine denominator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantWomenAndOnRoutineWithViralLoadResultsDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get pregnant women with viral load results Not documented");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(getPregnantWomenWithViralLoadResultsDenominator(), mappings));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("pregnant AND routine");
    return cd;
  }

  /**
   * Patients who are pregnant, have viral load and NOT Documented
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantWomenAndNotDocumentedWithViralLoadResultsDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get pregnant women with viral load results Not documented");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(getPregnantWomenWithViralLoadResultsDenominator(), mappings));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(PatientsOnRoutineEnum.BREASTFEEDINGPREGNANT),
            "onDate=${endDate},location=${location}"));
    cd.setCompositionString("pregnant AND NOT routine");
    return cd;
  }

  /**
   * Get patients who are breastfeeding
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreBreastfeeding() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "breastfeeding",
            Context.getRegisteredComponents(BreastfeedingCalculation.class).get(0));
    cd.addParameter(new Parameter("onDate", "On Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }

  public CohortDefinition getPatientsWhoAreBreastfeedingAndNotPregnant() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get breastfeeding women who are NOT pregnant");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "onDate=${endDate},location=${location}";
    cd.addSearch("pregnant", EptsReportUtils.map(this.getPatientsWhoArePregnantCohort(), mappings));
    cd.addSearch(
        "breastfeeding", EptsReportUtils.map(this.getPatientsWhoAreBreastfeeding(), mappings));

    cd.setCompositionString("breastfeeding AND NOT pregnant");

    return cd;
  }

  /**
   * Get patients who are pregnant
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsWhoArePregnantCohort() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "pregnant", Context.getRegisteredComponents(PregnantCalculation.class).get(0));
    cd.addParameter(new Parameter("onDate", "On Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }
}
