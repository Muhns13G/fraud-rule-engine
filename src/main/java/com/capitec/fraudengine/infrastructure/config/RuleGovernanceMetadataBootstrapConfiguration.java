package com.capitec.fraudengine.infrastructure.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.capitec.fraudengine.application.service.RuleGovernanceMetadataBootstrapService;

/**
 * Bootstraps persisted governance metadata for code-defined rules.
 */
@Configuration
public class RuleGovernanceMetadataBootstrapConfiguration {

	@Bean
	ApplicationRunner ruleGovernanceMetadataBootstrapRunner(
		RuleGovernanceMetadataBootstrapService ruleGovernanceMetadataBootstrapService
	) {
		return ignored -> ruleGovernanceMetadataBootstrapService.ensureMetadataForCodeDefinedRules();
	}
}
