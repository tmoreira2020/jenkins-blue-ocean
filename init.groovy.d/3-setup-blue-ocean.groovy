import com.cloudbees.hudson.plugins.folder.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.google.common.collect.*
import hudson.model.*
import io.jenkins.blueocean.credential.*
import io.jenkins.blueocean.rest.impl.pipeline.credential.*
import jenkins.branch.*
import jenkins.model.*
import jenkins.plugins.git.traits.*
import jenkins.scm.api.mixin.*
import jenkins.scm.api.*
import org.acegisecurity.adapters.*
import org.acegisecurity.context.*
import org.acegisecurity.userdetails.*
import org.jenkinsci.plugins.workflow.multibranch.*
import org.jenkinsci.plugins.github_branch_source.*
import org.jenkinsci.plugins.github_branch_source.BranchDiscoveryTrait

class Setup {

    def StandardUsernamePasswordCredentials createCredential(user, githubToken) {
        String credentialId = "github"
        StandardUsernamePasswordCredentials credential = new UsernamePasswordCredentialsImpl(CredentialsScope.USER, credentialId, "GitHub Access Token", user.getId(), githubToken)

        return credential
    }

    def Domain createDomain() {
        Domain domain = new Domain(
            "blueocean-github-domain",
            "blueocean-github-domain to store credentials by BlueOcean",
            ImmutableList.<DomainSpecification>of(new BlueOceanDomainSpecification()))

        return domain
    }

    def WorkflowMultiBranchProject createProject(user, credential, orgName, projectName) {
        def jenkins = Jenkins.getInstance()
        WorkflowMultiBranchProject project = jenkins.createProject(WorkflowMultiBranchProject.class, projectName)
        AbstractFolderProperty prop = new BlueOceanCredentialsProvider.FolderPropertyImpl(user.getId(), credential.getId(), BlueOceanCredentialsProvider.createDomain("https://api.github.com"))

        project.addProperty(prop);

        def scmSource = createSource(orgName, projectName)

        project.getSourcesList().add(new BranchSource(scmSource))

        project.scheduleBuild(new Cause.UserIdCause())

        return project
    }

    def SCMSource createSource(orgName, projectName) {
        Set<ChangeRequestCheckoutStrategy> strategies = new HashSet<>()
        strategies.add(ChangeRequestCheckoutStrategy.MERGE)

        return new GitHubSCMSourceBuilder("blueocean", "https://api.github.com", "github",
                orgName,
                projectName)
                .withTrait(new BranchDiscoveryTrait(true, true))
                .withTrait(new ForkPullRequestDiscoveryTrait(strategies, new ForkPullRequestDiscoveryTrait.TrustPermission()))
                .withTrait(new OriginPullRequestDiscoveryTrait(strategies))
                .withTrait(new CleanBeforeCheckoutTrait())
                .withTrait(new CleanAfterCheckoutTrait())
                .withTrait(new LocalBranchTrait())
                .build()
    }

    def CredentialsStore getCredentialsStore(user) {
        CredentialsStore store = null
        for(CredentialsStore s: CredentialsProvider.lookupStores(user)) {
            if(s.hasPermission(CredentialsProvider.CREATE) && s.hasPermission(CredentialsProvider.UPDATE)){
                store = s
                break
            }
        }

        return store
    }

    def User login(username) {
        def jenkins = Jenkins.getInstance()
        def user = jenkins.getUser(username)

        UserDetails d = jenkins.getSecurityRealm().loadUserByUsername(user.getId())

        SecurityContextHolder.getContext().setAuthentication(new PrincipalAcegiUserToken(user.getId(), user.getId(), user.getId(), d.getAuthorities(), user.getId()))

        return user
    }

}

def orgName = "tmoreira2020"
def projectName = "jenkins-2-pipeline-test"
def env = System.getenv()
def setup = new Setup()

def admin = setup.login("admin")
def credentialsStore = setup.getCredentialsStore(admin)
def domain = setup.createDomain()
def credential = setup.createCredential(admin, env.GITHUB_TOKEN)

credentialsStore.addDomain(domain)
credentialsStore.addCredentials(domain, credential)

def project = setup.createProject(admin, credential, orgName, projectName)