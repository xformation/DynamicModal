package com.synectiks.dynModel.utils;

import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.hibernate.jpa.boot.spi.ProviderChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibDynEntityPersister extends HibernatePersistenceProvider
		implements PersistenceProvider {

	private static final Logger log = LoggerFactory
			.getLogger(HibDynEntityPersister.class);

	public static final String CUSTOM_CLASSES = "CUSTOM_CLASSES";

	@Override
	protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(
			PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration,
			ClassLoader providedClassLoader) {

		if (persistenceUnitDescriptor instanceof ParsedPersistenceXmlDescriptor) {
			ParsedPersistenceXmlDescriptor tmp = (ParsedPersistenceXmlDescriptor) persistenceUnitDescriptor;
			Object object = integration.get("CUSTOM_CLASSES");
		}

		return super.getEntityManagerFactoryBuilder(persistenceUnitDescriptor,
				integration, providedClassLoader);
	}

	protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(
			String persistenceUnitName, Map properties, ClassLoader providedClassLoader) {
		log.debug(String.format(
				"Attempting to obtain correct EntityManagerFactoryBuilder for persistenceUnitName : %s",
				persistenceUnitName));

		final Map integration = wrap(properties);
		final List<ParsedPersistenceXmlDescriptor> units;
		try {
			units = PersistenceXmlParser.locatePersistenceUnits(integration);
		} catch (Exception e) {
			log.debug("Unable to locate persistence units", e);
			throw new PersistenceException("Unable to locate persistence units", e);
		}

		log.debug(String.format("Located and parsed %s persistence units; checking each",
				units.size()));

		if (persistenceUnitName == null && units.size() > 1) {
			// no persistence-unit name to look for was given and we found
			// multiple persistence-units
			throw new PersistenceException(
					"No name provided and multiple persistence units found");
		}

		for (ParsedPersistenceXmlDescriptor persistenceUnit : units) {
			log.debug(String.format(
					"Checking persistence-unit [name=%s, explicit-provider=%s] against incoming persistence unit name [%s]",
					persistenceUnit.getName(), persistenceUnit.getProviderClassName(),
					persistenceUnitName));

			final boolean matches = persistenceUnitName == null
					|| persistenceUnit.getName().equals(persistenceUnitName);
			if (!matches) {
				log.debug("Excluding from consideration due to name mis-match");
				continue;
			}

			// See if we (Hibernate) are the persistence provider

			String extractRequestedProviderName = ProviderChecker
					.extractRequestedProviderName(persistenceUnit, integration);

			if (!ProviderChecker.isProvider(persistenceUnit, properties) && !(this
					.getClass().getName().equals(extractRequestedProviderName))) {
				log.debug("Excluding from consideration due to provider mis-match");
				continue;
			}

			return getEntityManagerFactoryBuilder(persistenceUnit, integration,
					providedClassLoader);
		}

		log.debug("Found no matching persistence units");
		return null;
	}
}
