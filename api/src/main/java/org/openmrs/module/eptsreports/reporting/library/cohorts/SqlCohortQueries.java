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
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;

import org.openmrs.Program;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines all of the SQL Cohort Definition instances we want to expose for EPTS
 */
@Component
public class SqlCohortQueries {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	@Autowired
	private TbMetadata tbMetadata;
	
	// Looks for patients enrolled in ART program (program 2=SERVICO TARV -
	// TRATAMENTO) before or on end date
	@DocumentedDefinition(value = "inARTProgramDuringTimePeriod")
	public InProgramCohortDefinition getPatientsinARTProgramDuringTimePeriod() {
		InProgramCohortDefinition inARTProgramDuringTimePeriod = new InProgramCohortDefinition();
		Program artProgram = hivMetadata.getARTProgram();
		inARTProgramDuringTimePeriod.setName("inARTProgramDuringTimePeriod");
		inARTProgramDuringTimePeriod.setPrograms(Arrays.asList(artProgram));
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		inARTProgramDuringTimePeriod.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return inARTProgramDuringTimePeriod;
	}
	
	// Looks for patients registered as START DRUGS (answer to question 1255 = ARV
	// PLAN is 1256 = START DRUGS) in the first drug pickup (encounter type
	// 18=S.TARV: FARMACIA) or follow up consultation for adults and children
	// (encounter types 6=S.TARV: ADULTO SEGUIMENTO and 9=S.TARV: PEDIATRIA
	// SEGUIMENTO) before or on end date
	@DocumentedDefinition(value = "patientWithSTARTDRUGSObs")
	public SqlCohortDefinition getPatientWithSTARTDRUGSObs() {
		SqlCohortDefinition patientWithSTARTDRUGSObs = new SqlCohortDefinition();
		patientWithSTARTDRUGSObs.setName("patientWithSTARTDRUGSObs");
		patientWithSTARTDRUGSObs.setQuery(
		    "Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in ("
		            + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id="
		            + hivMetadata.getARVPlanConcept().getConceptId() + " and o.value_coded="
		            + hivMetadata.getstartDrugsConcept().getConceptId()
		            + " and e.encounter_datetime<=:onOrBefore group by p.patient_id");
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithSTARTDRUGSObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return patientWithSTARTDRUGSObs;
	}
	
	// Looks for with START DATE (Concept 1190=HISTORICAL DRUG START DATE) filled in
	// drug pickup (encounter type 18=S.TARV: FARMACIA) or follow up consultation
	// for adults and children (encounter types 6=S.TARV: ADULTO SEGUIMENTO and
	// 9=S.TARV: PEDIATRIA SEGUIMENTO) where START DATE is before or equal end date
	@DocumentedDefinition(value = "patientWithHistoricalDrugStartDateObs")
	public SqlCohortDefinition getPatientWithHistoricalDrugStartDateObs() {
		SqlCohortDefinition patientWithHistoricalDrugStartDateObs = new SqlCohortDefinition();
		patientWithHistoricalDrugStartDateObs.setName("patientWithHistoricalDrugStartDateObs");
		patientWithHistoricalDrugStartDateObs.setQuery(
		    "Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in ("
		            + hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id="
		            + hivMetadata.gethistoricalDrugStartDateConcept().getConceptId()
		            + " and o.value_datetime is not null and o.value_datetime<=:onOrBefore group by p.patient_id");
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		patientWithHistoricalDrugStartDateObs.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return patientWithHistoricalDrugStartDateObs;
	}
	
	// Looks for patients enrolled on ART program (program 2=SERVICO TARV -
	// TRATAMENTO), transferred from other health facility (program workflow state
	// is 29=TRANSFER FROM OTHER FACILITY) between start date and end date
	@DocumentedDefinition(value = "transferredFromOtherHealthFacility")
	public SqlCohortDefinition getPatientsTransferredFromOtherHealthFacility() {
		SqlCohortDefinition transferredFromOtherHealthFacility = new SqlCohortDefinition();
		transferredFromOtherHealthFacility.setName("transferredFromOtherHealthFacility");
		transferredFromOtherHealthFacility.setQuery(
		    "select pg.patient_id from patient p inner join patient_program pg on p.patient_id=pg.patient_id inner join patient_state ps on pg.patient_program_id=ps.patient_program_id where pg.voided=0 and ps.voided=0 and p.voided=0 and pg.program_id="
		            + hivMetadata.getARTProgram().getProgramId() + " and ps.state="
		            + hivMetadata.gettransferredFromOtherHealthFacilityWorkflowState().getProgramWorkflowStateId()
		            + " and ps.start_date=pg.date_enrolled and ps.start_date<=:onOrBefore and  ps.start_date>=:onOrAfter");
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		transferredFromOtherHealthFacility.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return transferredFromOtherHealthFacility;
	}
	
	// Obtain patients notified to be on TB treatment
	@DocumentedDefinition(value = "transferredFromOtherHealthFacility")
	public SqlCohortDefinition getPatientsNotifiedToBeOnTbTreatment() {
		SqlCohortDefinition notifiedToBeOnTbTreatment = new SqlCohortDefinition();
		notifiedToBeOnTbTreatment.setName("notifiedToBeOnTbTreatment");
		notifiedToBeOnTbTreatment.setQuery(
		    "Select p.patient_id from patient p inner join encounter e on p.patient_id=e.patient_id inner join obs o on o.encounter_id=e.encounter_id where e.voided=0 and o.voided=0 and p.voided=0 and e.encounter_type in ("
		            + hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId() + ","
		            + hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId() + ") and o.concept_id="
		            + tbMetadata.getTUBERCULOSIS_TREATMENT_PLANConcept().getConceptId() + " and o.value_coded="
		            + hivMetadata.getYesConcept().getConceptId()
		            + " and e.encounter_datetime<=:onOrBefore group by p.patient_id");
		notifiedToBeOnTbTreatment.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
		notifiedToBeOnTbTreatment.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
		return notifiedToBeOnTbTreatment;
	}
	
}
