package dependency_tree;

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        OrchestratorInterface orchestrator = new OrchestratorImpl();
        
        //creating dependencies of servers
        Set<String> dependenciesOfA = new HashSet<String>();
        dependenciesOfA.add("B");
        dependenciesOfA.add("C");
        Set<String> dependenciesOfB = new HashSet<String>();
        dependenciesOfB.add("C");
        //dependenciesOfB.add("A")//Just to check the case for cyclic dependency it should throw exception saying cyclic dependency exist.
        Set<String> dependenciesOfC = new HashSet<String>();
        Set<String> dependenciesOfE = new HashSet<String>();
        Set<String> dependenciesOfD = new HashSet<String>();
        dependenciesOfD.add("E");
        Set<String> dependenciesOfF = new HashSet<String>();
        
        //creating servers
        Server A = new Server("A", dependenciesOfA);
        Server B = new Server("B", dependenciesOfB);
        Server C = new Server("C", dependenciesOfC);
        Server D = new Server("D", dependenciesOfD);
        Server E = new Server("E", dependenciesOfE);
        Server F = new Server("F", dependenciesOfF);
        
        //Add server only when all of its dependencies are added
        orchestrator.addServer(C);
        orchestrator.addServer(B);
        orchestrator.addServer(A);
        orchestrator.addServer(E);
        orchestrator.addServer(D);
        orchestrator.addServer(F);
        
        //creating DAGgraph
        orchestrator.pre_process();
        
        //Calling API1
        orchestrator.spawn_cluster();
        
        //Calling API2
        orchestrator.instance_down(E);   
    }

}
