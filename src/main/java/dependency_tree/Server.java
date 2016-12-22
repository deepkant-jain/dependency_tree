package dependency_tree;

import java.util.Set;

public class Server{

    private String serverName;
    private Set<String> serverDependencySet;
    
    public Server(String serverName, Set<String> serverDependencySet) {
        this.serverName = serverName;
        this.serverDependencySet = serverDependencySet;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return the serverDependencySet
     */
    public Set<String> getServerDependencySet() {
        return serverDependencySet;
    }

    /**
     * @param serverDependencySet the serverDependencySet to set
     */
    public void setServerDependencySet(Set<String> serverDependencySet) {
        this.serverDependencySet = serverDependencySet;
    }

}
