#!/usr/bin/env bash

set -euo pipefail

echo "Running Phase 4 security and operations regression suite..."

./mvnw --batch-mode --no-transfer-progress \
  -Dtest=DefaultProfileSecurityIntegrationTest,\
SecureProfileSecurityIntegrationTest,\
SecureProfileGovernanceAuthorizationIntegrationTest,\
SecureProfileGovernanceAdminIntegrationTest,\
SecureProfilePlatformAdminIntegrationTest,\
SecureProfileJdbcIdentityProviderIntegrationTest,\
SecureProfileCredentialRotationIntegrationTest,\
SecureProfileSecurityDiagnosticsIntegrationTest,\
ProductionProfileObservabilityIntegrationTest,\
ObservabilityContractIntegrationTest,\
SecureProfileDatasourceResilienceIntegrationTest,\
SecureProfileSecurityConfigurationTest,\
SecureProfileConfigurationGuardrailsTest,\
ProductionProfileConfigurationGuardrailsTest \
  test
