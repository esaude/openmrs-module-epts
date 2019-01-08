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

import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.txnew.StartedArtAtXtoYAgeCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txnew.StartedArtBellowXAgeCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txnew.StartedArtOverXAgeCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AgeCohortQueries {
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	public CohortDefinition createBelowYAgeCohort(String name, int maxAge) {
		AgeCohortDefinition patientsWithAgeBelow = new AgeCohortDefinition();
		patientsWithAgeBelow.setName(name);
		patientsWithAgeBelow.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		patientsWithAgeBelow.setMaxAge(maxAge - 1);
		return patientsWithAgeBelow;
	}
	
	public CohortDefinition createXtoYAgeCohort(String name, int minAge, int maxAge) {
		AgeCohortDefinition xToYCohort = new AgeCohortDefinition();
		xToYCohort.setName(name);
		xToYCohort.setMaxAge(maxAge);
		xToYCohort.setMinAge(minAge);
		xToYCohort.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		return xToYCohort;
	}
	
	public CohortDefinition createOverXAgeCohort(String name, int minAge) {
		AgeCohortDefinition overXCohort = new AgeCohortDefinition();
		overXCohort.setName(name);
		overXCohort.setMinAge(minAge);
		overXCohort.addParameter(new Parameter("effectiveDate", "endDate", Date.class));
		return overXCohort;
	}
	
	/**
	 * @return Person with Unknown age, the birthdate column is null
	 */
	public CohortDefinition getPatientsWithUnknownAge() {
		return genericCohortQueries.generalSql("unknownAge",
		    "SELECT p.patient_id FROM patient p JOIN person pr ON p.patient_id = pr.person_id WHERE pr.birthdate IS NULL");
	}
	
	/**
	 * @param minAge Minimum age
	 * @param maxAge Maximum age
	 * @return Patients with age between {@code minAge} and {@code maxAge} on ART start date.
	 */
	public CohortDefinition createXtoYAgeOnArtStartDateCohort(int minAge, int maxAge) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition("patients with age between " + minAge + " and "
		        + maxAge + " on ART start date", Context.getRegisteredComponents(StartedArtAtXtoYAgeCalculation.class)
		        .get(0));
		cd.addCalculationParameter("minAge", minAge);
		cd.addCalculationParameter("maxAge", maxAge);
		return cd;
	}
	
	/**
	 * @param maxAge Maximum age
	 * @return Patients with age bellow {@code maxAge} on ART start date.
	 */
	public CohortDefinition createBelowXAgeOnArtStartDateCohort(int maxAge) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition("patients with age bellow " + maxAge
		        + " on ART start date", Context.getRegisteredComponents(StartedArtBellowXAgeCalculation.class).get(0));
		cd.addCalculationParameter("maxAge", maxAge);
		return cd;
	}
	
	/**
	 * @param minAge Minimum age
	 * @return Patients with age equal or above {@code minAge} on ART start date.
	 */
	public CohortDefinition createOverXAgeOnArtStartDateCohort(int minAge) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition("patients with age over " + minAge
		        + " on ART start date", Context.getRegisteredComponents(StartedArtOverXAgeCalculation.class).get(0));
		cd.addCalculationParameter("minAge", minAge);
		return cd;
	}
	
}
