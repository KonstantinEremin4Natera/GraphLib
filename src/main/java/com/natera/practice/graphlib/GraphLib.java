package com.natera.practice.graphlib;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GraphLib {
    public static <V,E> List<Edge<E>> computePath(Graph<V,E> graph, Vertex<V> u, Vertex<V> v) {
        Set<Vertex<V>> knownVertices = new HashSet<>();
        ConcurrentHashMap<Vertex<V>, Edge<E>> edgeForest = new ConcurrentHashMap<>();
        for (Vertex<V> innerVertex : graph.vertices()) {
            if (!knownVertices.contains(innerVertex)) {
                DFS(graph, innerVertex, knownVertices, edgeForest);
            }
        }
        List<Edge<E>> path = new CopyOnWriteArrayList<>();
        if (edgeForest.get(v) != null) {               // v discovered while searching
            Vertex<V> walk = v;                        // path is constructed backwards
            while (walk != u) {
                Edge<E> edge = edgeForest.get(walk);
                path.add(edge);                        // prepend edge to the path
                walk = graph.opposite(walk, edge);     // repeat with opposite endpoint
            }
        }
        return path;
    }

    private static <V,E> void DFS(Graph<V,E> graph, Vertex<V> u, Set<Vertex<V>> knownVertices, Map<Vertex<V>, Edge<E>> edgeForest) {
        knownVertices.add(u);
        for (Edge<E> e : graph.outgoingEdges(u)) {         // for every outgoing edge from u
            Vertex<V> v = graph.opposite(u, e);
            if (!knownVertices.contains(v)) {
                edgeForest.put(v, e);                      // e is the edge that discovered v
                DFS(graph, v, knownVertices, edgeForest);  // explored recursively from v
            }
        }
    }
}
