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
    
    public void pre_process() {
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
        return createGraph();
    }

    private boolean createGraph() {
        DirectedGraph graph = new SimpleDirectedGraph(DefaultEdge.class);

        for (Server server: servers.values()) 
        {
            graph.addVertex(server);
        }
        
        for (Server server: servers.values()) 
        { 
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
        
        if (cluster.vertexSet().size() == 0){
            System.out.println("Cluster empty, nothing to spawn\n");
        }
        Iterator iter = new TopologicalOrderIterator(cluster);
        
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
        Stack<Server> relaunchOrder = new Stack<Server>();
        Stack<Server> stopOrder = new Stack<Server>();
        Iterator bfi = new BreadthFirstIterator(cluster, server); 
        while (bfi.hasNext())
        {
            Server ser = (Server) bfi.next();
            connectedServers.add(ser.getServerName());
        }
        
        if (connectedServers.size() == 1)
        {
            //only one server in cluster
            System.out.println("Relaunching server :  " + server.getServerName());
        }
        else
        {
            // Stop server dependent on other server in reverse topological sort order and re-launch in topological sort order. 
            Iterator iter = new TopologicalOrderIterator(cluster);
            while (iter.hasNext())
            {
                Server ser = (Server) iter.next();
                if (connectedServers.contains(ser.getServerName()))
                {
                    stopOrder.push(ser);
                }
            }
            
            while(!stopOrder.empty())
            {
                Server ser = stopOrder.pop();
                relaunchOrder.push(ser);
                if (ser != server){
                    System.out.println("stopping server: "+ ser.getServerName());
                }
            }
            while(!relaunchOrder.empty())
            {
                System.out.println("Relaunching : "+ relaunchOrder.pop().getServerName());
            }
        }
        return;
    }

}
