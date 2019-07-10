package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.EriDSDCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EriDSDDataset extends BaseDataSet {

  @Autowired private EriDSDCohortQueries eriDSDCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private TxCurrCohortQueries txCurrCohortQueries;
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructEriDSDDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.setName("DSD Data Set");
    dsd.addParameters(getParameters());
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dsd.setName("total");
    dsd.addColumn(
        "D1T",
        "DSD D1 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D1 Total",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getAllPatientsWhoAreActiveAndStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "D1NPNB",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D1NPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getAllPatientsWhoAreActiveAndStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "D1NPNBC",
        "Non-pregnant and Non-Breastfeeding Children By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D1NPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getAllPatientsWhoAreActiveAndStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "D2T",
        "DSD D2 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D2 Total",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "D2NPNB",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2NPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "D2NPNBC",
        "Non-pregnant and Non-Breastfeeding Children  By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2NPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "BNP",
        "Breastfeeding (exclude pregnant)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "BNP",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastFeedingAndNotPregnant(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "PNB",
        "Pregnant (exclude breastfeeding)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "PNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoArePregnantAndNotBreastFeeding(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N1T",
        "DSD N1 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1T",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModel(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N1SST",
        "DSD N1 Stable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1SST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N1SNPNB",
        "Stable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1SNPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N1SNPNBC",
        "Stable Non-pregnant and Non-Breastfeeding Children  By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1SNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N1UST",
        "DSD N1 Unstable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1UST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N1UNPNB",
        "Unstable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1UNPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N1UNPNBC",
        "Unstable Non-pregnant and Non-Breastfeeding Children  By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1UNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N1UBNP",
        "N1 Unstable Breastfeeding (exclude pregnant)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1UBNP",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoAreBreastFeedingAndNotPregnantAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N1UPNB",
        "N1 Unstable Pregnant (include breastfeeding)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1UPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWhoArePregnantAndNotBreastFeedingAndParticipateInDsdModelUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N2T",
        "DSD N2 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2T",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveWithNextPickupAs3Months(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N2SST",
        "DSD N2 Stable Subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2SST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveWithNextPickupAs3MonthsAndStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N2SNPNBA",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2SNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveWithNextPickupAs3MonthsAndStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N2SNPNBC",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2SNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveWithNextPickupAs3MonthsAndStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N2UST",
        "DSD N2 Unstable Subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2UST",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveWithNextPickupAs3MonthsAndUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N2UNPNBA",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2UNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveWithNextPickupAs3MonthsAndUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N2UNPNBC",
        "Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2UNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreActiveWithNextPickupAs3MonthsAndUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N2UBNP",
        "N2 Patients who are breastfeeding excluding pregnant patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2UBNP",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeedingAndNotPregnantN2Unstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N2UPNB",
        "N2: Pregnant: includes all pregnant patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N2UPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoArePregnantAndNotBreastfeedingN2Unstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N3T",
        "DSD N3 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3T",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWithNextConsultationScheduled175To190Days(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N3SST",
        "DSD N3 Stable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3SST",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWithNextConsultationScheduled175To190DaysStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N3SNPNBA",
        "DSD N3 Stable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3SNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWithNextConsultationScheduled175To190DaysStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N3SNPNBC",
        " DSD N3 Stable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3SNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWithNextConsultationScheduled175To190DaysStable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N3UST",
        "DSD N3 Unstable subtotal",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3UST",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWithNextConsultationScheduled175To190DaysUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N3UNPNBA",
        "DSD N3 Unstable Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3UNPNBA",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWithNextConsultationScheduled175To190DaysUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "N3UNPNBC",
        " DSD N3 Unstable Non-pregnant and Non-Breastfeeding Children (2-4, 5-9, 10-14)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3UNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries
                        .getPatientsWithNextConsultationScheduled175To190DaysUnstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "N3UBNP",
        "N3 Patients who are breastfeeding excluding pregnant patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3UBNP",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeedingAndNotPregnantN3Unstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");
    dsd.addColumn(
        "N3UPNB",
        "N3: Pregnant: includes all pregnant patients",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3UPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoArePregnantAndNotBreastfeedingN3Unstable(),
                    "endDate=${endDate},location=${location}")),
            mappings),
        "");

    return dsd;
  }

  private List<ColumnParameters> getChildrenColumn() {
    ColumnParameters twoTo4 = new ColumnParameters("twoTo4", "2-4", "age=2-4", "01");
    ColumnParameters fiveTo9 = new ColumnParameters("fiveTo9", "5-9", "age=5-9", "02");
    ColumnParameters tenTo14 = new ColumnParameters("tenTo14", "10-14", "age=10-14", "03");

    return Arrays.asList(twoTo4, fiveTo9, tenTo14);
  }
}
