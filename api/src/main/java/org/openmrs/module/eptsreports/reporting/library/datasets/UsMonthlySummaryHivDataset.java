package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.noMappings;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.UsMonthlySummaryHivCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsMonthlySummaryHivDataset extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private UsMonthlySummaryHivCohortQueries usMonthlySummaryHivCohortQueries;

  public DataSetDefinition constructUsMonthlySummaryHivDataset() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("US Monthly Summary HIV Data Set");
    dataSetDefinition.addParameters(getParameters());

    dataSetDefinition.addDimension("gender", noMappings(eptsCommonDimension.gender()));
    dataSetDefinition.addDimension(
        "age", map(eptsCommonDimension.getUsMonthlySummaryHivAges(), "effectiveDate=${endDate}"));

    addRow(
        dataSetDefinition,
        "A1",
        "Nº cumulativo de pacientes registados até o fim do mês anterior",
        getRegisteredInPreArtBooks1and2ByEndOfPreviousMonth(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "A2",
        "Nº de pacientes registados durante o mês",
        getRegisteredInPreArtBooks1and2DuringReportingPeriod(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "B1",
        "Nº mensal de novos inscritos",
        getNewlyEnrolled(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "B2",
        "Nº mensal de transferidos de outras US",
        getEnrolledByTransfer(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "C1",
        "Nº cumulativo de transferidos para outras US",
        getTransferredOut(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "C2",
        "Nº cumulativo de abandonos pre-tarv",
        getAbandoned(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "C3",
        "Nº cumulativo de óbitos pre-tarv",
        getDeceasedDuringPreArt(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "C4",
        "Nº cumulativo que iniciaram TARV",
        getInitiatedArt(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "E1",
        "Nº dos novos inscritos mensais no Livro de Registo Nº 1 de Pré-TARV rastreados para TB",
        getScreenedForTb(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "E2",
        "Nº dos novos inscritos mensais no Livro de Registo Nº 1 de Pré-TARV rastreados para ITS",
        getScreenedForSti(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "F1",
        "Nº dos novos inscritos mensais no Livro de Registo Nº 1 de Pré-TARV que iniciaram TPC durante o mês",
        getStartedCotrimoxazoleProphylaxis(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "F2",
        "Nº dos novos inscritos mensais no Livro de Registo Nº 1 de Pré-TARV que iniciaram TPC durante o mês",
        getStartedIsoniazidProphylaxis(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "G1",
        "Nº cumulativo de pacientes registados até o fim do mês anterior TARV",
        getRegisteredInArtBooks1and2ByEndOfPreviousMonth(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "G2",
        "Nº de pacientes registados durante o mês TARV",
        getRegisteredInPArtBooks1and2DuringReportingPeriod(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "H1",
        "Nº mensal de novos inícios tarv",
        getNewlyEnrolledInArt(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "H2",
        "Nº mensal de transferidos de outras US tarv",
        getEnrolledInArtByTransfer(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "I1",
        "Nº cumulativo de suspensos tarv",
        getInArtWhoSuspendedTreatment(),
        getColumnParameters());

    addRow(
        dataSetDefinition,
        "I2",
        "Nº cumulativo de transferidos para outras US tarv",
        getInArtTransferredOut(),
        getColumnParameters());

    return dataSetDefinition;
  }

  private Mapped<CohortIndicator> getInArtTransferredOut() {
    String name = "NUMERO CUMULATIVO DE PACIENTES TARV TRANSFERIDOS PARA";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtTransferredOut();
    String mappings = "onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getInArtWhoSuspendedTreatment() {
    String name = "NUMERO CUMULATIVO DE PACIENTES TARV QUE SUSPENDERAM TARV";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtWhoSuspendedTreatment();
    String mappings = "onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getEnrolledInArtByTransfer() {
    String name =
        "NUMERO DE PACIENTES TARV REGISTADOS NO LIVRO 1 E 2 TARV TRANSFERIDOS DE NUM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInArtEnrolledByTransfer();
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getNewlyEnrolledInArt() {
    String name =
        "NUMERO DE NOVOS PACIENTES QUE INICIARAM TARV REGISTADOS NO LIVRO 1 TARV NUM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getNewlyEnrolledInArt();
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getRegisteredInPArtBooks1and2DuringReportingPeriod() {
    String name = "NUMERO DE PACIENTES REGISTADOS NOS LIVROS 1 E 2 TARV NUM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getRegisteredInArtBooks1and2();
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getRegisteredInArtBooks1and2ByEndOfPreviousMonth() {
    String name =
        "NUMERO CUMULATIVO DE PACIENTES TARV REGISTADOS NOS LIVROS 1 E 2 ATE O FIM DE UM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getRegisteredInArtBooks1and2();
    String mappings = "onOrBefore=${startDate-1d},locationList=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getStartedIsoniazidProphylaxis() {
    String name =
        "NUMERO DE NOVOS PACIENTES REGISTADOS NO LIVRO 1 PRE-TARV NUM PERIODO E QUE INICIARAM PROFILAXIA COM INH NO MESMO PERIODO";
    CohortDefinition cohort =
        usMonthlySummaryHivCohortQueries.getInPreArtWhoStartedIsoniazidProphylaxis();
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getStartedCotrimoxazoleProphylaxis() {
    String name =
        "NUMERO DE NOVOS PACIENTES REGISTADOS NO LIVRO 1 PRE-TARV NUM PERIODO E QUE INICIARAM PROFILAXIA COM CTZ NO MESMO PERIODO";
    CohortDefinition cohort =
        usMonthlySummaryHivCohortQueries.getInPreArtWhoStartedCotrimoxazoleProphylaxis();
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getScreenedForSti() {
    String name =
        "NUMERO DE NOVOS PACIENTES REGISTADOS NO LIVRO 1 PRE-TARV NUM PERIODO E QUE FORAM RASTREADOS PARA ITS NO MESMO PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInPreArtWhoScreenedForSti();
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getScreenedForTb() {
    String name =
        "NUMERO DE NOVOS PACIENTES REGISTADOS NO LIVRO 1 PRE-TARV E RASTREADOS PARA TB NUM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInPreArtWhoScreenedForTb();
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getInitiatedArt() {
    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV QUE INICIARAM TARV";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInPreArtWhoInitiatedArt();
    String mappings = "onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getDeceasedDuringPreArt() {
    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV QUE OBITARAM";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getDeadDuringPreArt();
    String mappings = "onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getAbandoned() {
    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV QUE ABANDONARAM";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getAbandonedPreArt();
    String mappings = "onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getTransferredOut() {
    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV TRANSFERIDOS PARA";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getTransferredOut();
    String mappings = "onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getEnrolledByTransfer() {
    String name =
        "NUMERO DE PACIENTES PRE-TARV REGISTADOS NO LIVRO 1 E 2 TRANSFERIDOS DE NUM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getInPreArtEnrolledByTransfer();
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getNewlyEnrolled() {
    String name = "NUMERO DE NOVOS PACIENTES PRE-TARV REGISTADOS NO LIVRO 1 NUM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getNewlyEnrolledInArtBooks1and2();
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getRegisteredInPreArtBooks1and2ByEndOfPreviousMonth() {
    String name = "NUMERO CUMULATIVO DE PACIENTES PRE-TARV REGISTADOS ATE O FIM DE UM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getRegisteredInPreArtBooks1and2();
    String mappings = "onOrBefore=${startDate-1d},locationList=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private Mapped<CohortIndicator> getRegisteredInPreArtBooks1and2DuringReportingPeriod() {
    String name = "NUMERO DE PACIENTES PRE-TARV REGISTADOS NO LIVRO 1 E 2 NUM PERIODO";
    CohortDefinition cohort = usMonthlySummaryHivCohortQueries.getRegisteredInPreArtBooks1and2();
    String mappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    CohortIndicator indicator = eptsGeneralIndicator.getIndicator(name, map(cohort, mappings));
    return mapStraightThrough(indicator);
  }

  private List<ColumnParameters> getColumnParameters() {
    return Arrays.asList(
        new ColumnParameters("Female under 15", "Female under 15", "gender=F|age=0-14", "F014"),
        new ColumnParameters("Female above 15", "Female above 15", "gender=F|age=15+", "F15"),
        new ColumnParameters("Male under 15", "Male under 15", "gender=M|age=0-14", "M014"),
        new ColumnParameters("Male above 15", "Male above 15", "gender=M|age=15+", "M15"));
  }
}
