import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.install.*
import jenkins.model.*
import jenkins.security.s2m.AdminWhitelistRule
import jenkins.security.*

import org.jenkinsci.plugins.authorizeproject.*
import org.jenkinsci.plugins.authorizeproject.strategy.*

def env = System.getenv()

def jenkins = Jenkins.getInstance()

def allowsSignup = false
def hudsonRealm = new HudsonPrivateSecurityRealm(allowsSignup)

def adminUser = hudsonRealm.createAccount("admin", "test")
def customerUser = hudsonRealm.createAccount("thiago", "test")
jenkins.setSecurityRealm(hudsonRealm)

def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.ADMINISTER, "admin")

strategy.add(Jenkins.READ, "thiago")
strategy.add(hudson.model.View.READ, "thiago")

strategy.add(hudson.model.Item.BUILD, "thiago")
strategy.add(hudson.model.Item.READ, "thiago")
strategy.add(hudson.model.Item.WIPEOUT, "thiago")
strategy.add(hudson.model.Item.WORKSPACE, "thiago")

strategy.add(hudson.model.Run.UPDATE, "thiago")

jenkins.setAuthorizationStrategy(strategy)

jenkins.save()