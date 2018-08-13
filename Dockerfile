FROM jenkins/jenkins:2.121.2

## Setup Jenkins

RUN /usr/local/bin/install-plugins.sh \
      blueocean:1.7.2 \
      matrix-auth:2.3

# The directory with reference content for Jenkins home. Will be copied into real
# home on startup, possibly with overwriting (append .override to the file's name).
ARG JENKINS_REF_DIR="/usr/share/jenkins/ref"

# Run our init script on container startup; the scripts will still run on EVERY startup by Jenkins,
# unless each one guards itself against it.
# If files already exist, they will be overwritten (.override)
COPY init.groovy.d/1-setup-system.groovy \
        ${JENKINS_REF_DIR}/init.groovy.d/1-setup-system.groovy.override
COPY init.groovy.d/2-setup-admin-user.groovy \
        ${JENKINS_REF_DIR}/init.groovy.d/2-setup-admin-user.groovy.override
COPY init.groovy.d/3-setup-blue-ocean.groovy \
        ${JENKINS_REF_DIR}/init.groovy.d/3-setup-blue-ocean.groovy.override

RUN echo "2.121.2" > ${JENKINS_REF_DIR}/jenkins.install.InstallUtil.lastExecVersion
RUN echo "2.121.2" > ${JENKINS_REF_DIR}/jenkins.install.UpgradeWizard.state