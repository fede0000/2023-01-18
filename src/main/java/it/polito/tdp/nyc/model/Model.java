package it.polito.tdp.nyc.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import it.polito.tdp.nyc.db.NYCDao;

public class Model {
	
	private NYCDao dao;
	private List<String> listaVertici;
	private Graph<String, DefaultWeightedEdge> grafo;
	private List<Arco> listArchi;
	double peso = 0.0;
	
	public Model() {
		
		dao = new NYCDao();
		
	}
	
	public List<String> loadProvider(){
		
		return dao.getAllProvider();
	}
	
	public void creaGrafo(double distanza, String provider) {
		
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.listaVertici = dao.getAllVerices(provider);
		Graphs.addAllVertices(this.grafo, listaVertici);
		
		this.listArchi = dao.getAllEdges(provider);
		
		for(Arco a1 : listArchi) {
			for(Arco a2 : listArchi) {
				peso = LatLngTool.distance(a1.getCoordinate(), a2.getCoordinate(), LengthUnit.KILOMETER);
				if(peso<= distanza && peso != 0.0) {
					Graphs.addEdge(this.grafo, a1.getLocation(), a2.getLocation(), peso);
				}
			}
		}
		
	}
	
	public int getNVertici(){
		return this.grafo.vertexSet().size();
	}
	
	public int getNArchi(){
		return this.grafo.edgeSet().size();
	}
	
	
	//SBALLATO!!
	public Map<String,Integer> AnalizzaComponente(){
		
		Map<String,Integer> mapConnessi1 = new HashMap<>();
		Map<String,Integer> mapConnessi2 = new HashMap<>();
		int max = 0;
		
		for(String v : this.grafo.vertexSet()) {
		
		ConnectivityInspector<String, DefaultWeightedEdge> inspector =
				new ConnectivityInspector<String, DefaultWeightedEdge>(this.grafo);
		
		Set<String> connessi = inspector.connectedSetOf(v);
		mapConnessi1.put(v, connessi.size()-1);
		
		if(connessi.size()-1>max) {
			max = connessi.size()-1;
		}
		
		}
		
		for (Map.Entry<String, Integer> entry : mapConnessi1.entrySet()) {
			
			if(entry.getValue()==max) {
				
				mapConnessi2.put(entry.getKey(), entry.getValue());
			}
		    
			
		}
		
		
		return mapConnessi2;
	}
	
	
	//GIUSTO
	public Map<String,Integer> locationWithHighestNumberOfNeig() {
		
        int max = -1;
        Map<String,Integer> mapConnessi = new HashMap<>();
        Map<String,Integer> result = new HashMap<>();
        
        //trovo il vertice con più vicini
        for (String v : grafo.vertexSet()) {
        	
            int current = Graphs.neighborSetOf(grafo, v).size();
            mapConnessi.put(v, current);
            
            if (max < current) 
            	max = current;
                
        }   
        
        //Set<String> result = new HashSet<>();
        //restituisco i vertici 
        for (String v : grafo.vertexSet()) {
        	
            if (Graphs.neighborSetOf(grafo, v).size() == max) {
                result.put(v, max);
            }
        }
        
        return result;
    }
}
