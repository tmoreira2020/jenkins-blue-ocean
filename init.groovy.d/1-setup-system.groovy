import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.model.*
import jenkins.security.s2m.AdminWhitelistRule
import jenkins.security.*

import org.jenkinsci.plugins.authorizeproject.*
import org.jenkinsci.plugins.authorizeproject.strategy.*

def env = System.getenv()


def jenkins = Jenkins.getInstance()

jenkins.setSystemMessage("My Jenkins + Blue Ocean Setup")

jenkins.save()

def jenkinsLocationConfiguration = JenkinsLocationConfiguration.get()

jenkinsLocationConfiguration.setAdminAddress("tmoreira2020@gmail.com")

jenkinsLocationConfiguration.setUrl("http://localhost:8080")

jenkinsLocationConfiguration.save()