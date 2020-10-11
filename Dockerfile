FROM jboss/keycloak

COPY target/email-otp-spi-1.0-SNAPSHOT.war /opt/jboss/keycloak/standalone/deployments/
