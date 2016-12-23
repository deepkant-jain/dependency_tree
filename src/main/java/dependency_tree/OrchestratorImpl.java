package dependency_tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.*;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class OrchestratorImpl implements OrchestratorInterface {
    private Map<String, Server> servers = new HashMap<String, Server>();
    private DirectedGraph<Server,Server> cluster = null;
    DirectedGraph graph = new SimpleDirectedGraph(DefaultEdge.class);
    
    private void pre_process() {
        createGraph();
        return;
    }

    public boolean addServer(Server server) {
        
        //Check for if all the dependencies exist
        Set<String> dependencies = server.getServerDependencySet();
        for (String dependentServer: dependencies){
            if(servers.containsKey(dependentServer)== false){
                throw new RuntimeException("Please add the dependencies first \n");
            }
        }
        
        if (servers.put(server.getServerName(), server) != null){
            throw new RuntimeException("Server with name: " + server.getServerName() + " already exists \n");
        }
        return true;
    }

    private boolean createGraph() {

        for (Server server: servers.values()) {
            //System.out.println("building Graph by adding"+server.getServerName());
            graph.addVertex(server);
        }
        
        for (Server server: servers.values()) { 
            if (server.getServerDependencySet() != null) 
            {
                for (String depend: server.getServerDependencySet()) 
                {
                    Server dependOnServer = servers.get(depend);
                    graph.addEdge(dependOnServer, server);
                }
            }
        }
        
        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(graph);
        
        if (cycleDetector.detectCycles()){
            throw new RuntimeException("Cycle Exist in Graph");
        }
        else{
            cluster = graph;
            return true;
        }
    }

    public void spawn_cluster() {
        // TODO Auto-generated method stub
        pre_process();
        if (cluster.vertexSet().size() == 0){
            System.out.println("Cluster empty, nothing to spawn\n");
        }
        Iterator iter = new TopologicalOrderIterator(graph);
        
        while(iter.hasNext()) {
            Server server = (Server) iter.next();
            System.out.println("Spawning server:"+ server.getServerName());
        }
        return;
    }

    public void instance_down(Server server) {
        if (cluster.vertexSet().size() == 0) {
            System.out.println("No cluster information available \n");
            return;
        }
        
        Set<String> connectedServers = new HashSet<String>(); 
        Stack<Server> relaunchOrderofServers = new Stack<Server>();
        Stack<Server> stopOrderofServers = new Stack<Server>();
        Iterator bfiteraor = new BreadthFirstIterator(cluster, server); 
        
        while (bfiteraor.hasNext()){
            Server ser = (Server) bfiteraor.next();
            connectedServers.add(ser.getServerName());
        }
        
        if (connectedServers.size() == 1){
            //only one server in cluster
            System.out.println("Relaunching server :  " + server.getServerName());
        }
        else{
            // Stop server dependent on other server in reverse topological sort order and re-launch in topological sort order. 
            Iterator iter = new TopologicalOrderIterator(cluster);
            while (iter.hasNext()){
                Server ser = (Server) iter.next();
                if (connectedServers.contains(ser.getServerName()))
                {
                    stopOrderofServers.push(ser);
                }
            }
            
            while(!stopOrderofServers.empty()){
                Server ser = stopOrderofServers.pop();
                relaunchOrderofServers.push(ser);
                if (ser != server){
                    System.out.println("stopping server: "+ ser.getServerName());
                }
            }
            while(!relaunchOrderofServers.empty()){
                System.out.println("Relaunching : "+ relaunchOrderofServers.pop().getServerName());
            }
        }
        return;
    }

}
