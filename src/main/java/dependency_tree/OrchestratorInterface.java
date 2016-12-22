package dependency_tree;

public interface OrchestratorInterface {
    
    public void pre_process();
    
    public boolean addServer(Server server);
 
    public void spawn_cluster();
 
    public void instance_down(Server server);

}
