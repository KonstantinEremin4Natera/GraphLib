package com.natera.practice.graphlib;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AMGraph<V,E> implements Graph<V,E> {
    private boolean isDirected;
    private List<Vertex<V>> vertices = new CopyOnWriteArrayList<>( );
    private List<Edge<E>> edges = new CopyOnWriteArrayList<>( );

    /** Constructs an empty graph (undirected or directed). */
    public AMGraph(boolean directed) {
        isDirected = directed;
    }

    /** Returns the number of graph vertices */
    public int numVertices() {
        return vertices.size();
    }

    /** Returns the graph vertices as an iterable collection */
    public Iterable<Vertex<V>> vertices() {
        return vertices;
    }

    /** Returns the number of graph edges */
    public int numEdges() {
        return edges.size();
    }

    /** Returns the graph edges as an iterable collection */
    public Iterable<Edge<E>> edges() {
        return edges;
    }

    /** Returns the number of edges for which vertex v is the start. */
    public int outDegree(Vertex<V> v) {
        InnerVertex<V> vert = validate(v);
        return vert.getOutgoing().size();
    }

    /** Returns an iterable collection of edges for which vertex v is the start. */
    public Iterable<Edge<E>> outgoingEdges(Vertex<V> v) {
        InnerVertex<V> vert = validate(v);
        return vert.getOutgoing().values();
    }

    /** Returns the number of edges for which vertex v is the end. */
    public int inDegree(Vertex<V> v) {
        InnerVertex<V> vert = validate(v);
        return vert.getIncoming( ).size( );
    }

    /** Returns an iterable collection of edges for which vertex v is the end. */
    public Iterable<Edge<E>> incomingEdges(Vertex<V> v) {
        InnerVertex<V> vert = validate(v);
        return vert.getIncoming( ).values( );
    }

    /** Returns the edge from u to v, or null if they are not adjacent. */
    public Edge<E> getEdge(Vertex<V> u, Vertex<V> v) {
        InnerVertex<V> origin = validate(u);
        return origin.getOutgoing().get(v);
    }

    /** Returns the vertices of edge e as an array of size two. */
    public Vertex<V>[] endVertices(Edge<E> e) {
        InnerEdge<E> edge = validate(e);
        return edge.getEndpoints();
    }

    /** Returns the vertex that is opposite vertex v on edge e. */
    public Vertex<V> opposite(Vertex<V> v, Edge<E> e) throws IllegalArgumentException {
        InnerEdge<E> edge = validate(e);
        Vertex<V>[] endpoints = edge.getEndpoints();
        if (endpoints[0] == v)
            return endpoints[1];
        else if (endpoints[1] == v)
            return endpoints[0];
        else
            throw new IllegalArgumentException("v is not connected to this edge");
    }

    /** Inserts and returns a new vertex with the given element. */
    public Vertex<V> insertVertex(V element) {
        InnerVertex<V> v = new InnerVertex<>(element, isDirected);
        vertices.add(v);
        v.setIndex(vertices.size());
        return v;
    }
    /** Inserts and returns a new edge between u and v, storing given weight. */
    public Edge<E> insertEdge(Vertex<V> u, Vertex<V> v, E element) throws IllegalArgumentException {
        if (getEdge(u,v) == null) {
            InnerEdge<E> e = new InnerEdge<>(u, v, element);
            edges.add(e);
            e.setIndex(edges.size());
            //e.setPosition(edges.add(e));
            InnerVertex<V> origin = validate(u);
            InnerVertex<V> dest = validate(v);
            origin.getOutgoing( ).put(v, e);
            dest.getIncoming( ).put(u, e);
            return e;
        } else
            throw new IllegalArgumentException("Edge from u to v exists");
    }

    @SuppressWarnings({"unchecked"})
    private InnerVertex<V> validate(Vertex<V> v) {
        if (!(v instanceof InnerVertex)) throw new IllegalArgumentException("Invalid vertex");
        InnerVertex<V> vert = (InnerVertex<V>) v;
        if (!vert.validate(this)) throw new IllegalArgumentException("Invalid vertex");
        return vert;
    }

    @SuppressWarnings({"unchecked"})
    private InnerEdge<E> validate(Edge<E> e) {
        if (!(e instanceof InnerEdge)) throw new IllegalArgumentException("Invalid edge");
        InnerEdge<E> edge = (InnerEdge<E>) e;
        if (!edge.validate(this)) throw new IllegalArgumentException("Invalid edge");
        return edge;
    }

    /** An edge between two vertices. */
    class InnerEdge<E> implements Edge<E> {
        private E weight;
        private int index;
        private Vertex<V>[] endpoints;

        @SuppressWarnings({"unchecked"})
        /** Constructs InnerEdge instance from u to v, storing the given element. */
        public InnerEdge(Vertex<V> u, Vertex<V> v, E elem) {
            weight = elem;
            endpoints = new Vertex[]{u,v};  // array of length 2
        }

        /** Returns the element associated with the edge. */
        public E getWeight() { return weight; }

        /** Returns reference to the endpoint array. */
        public Vertex<V>[] getEndpoints() { return endpoints; }

        /** Validates that this edge instance belongs to the given graph. */
        public boolean validate(AMGraph<V,E> graph) {
            return AMGraph.this == graph && index != 0;
        }

        public void setIndex(int index) {this.index = index;}

        public int getIndex() {return index;}
    }

    class InnerVertex<V> implements Vertex<V>{
        private V weight;
        private int index;
        private Map<Vertex<V>, Edge<E>> outgoing, incoming;

        /** Constructs a new InnerVertex instance storing the given element. */
        public InnerVertex(V weight, boolean graphIsDirected) {
            this.weight = weight;
            outgoing = new ConcurrentHashMap<>();
            if (graphIsDirected)
                incoming = new ConcurrentHashMap<>();
            else
                incoming = outgoing;    // if undirected, alias outgoing map
        }

        /** Validates that this vertex instance belongs to the given graph. */
        public boolean validate(Graph<V,E> graph) {
            return (AMGraph.this == graph && index != 0);
        }

        /** Returns the element associated with the vertex. */
        public V getWeight() { return weight; }

        public void setIndex(int index) {this.index = index;}

        public int getIndex() {return index;}

        /** Returns reference to the underlying map of outgoing edges. */
        protected Map<Vertex<V>, Edge<E>> getOutgoing() { return outgoing; }

        /** Returns reference to the underlying map of incoming edges. */
        public Map<Vertex<V>, Edge<E>> getIncoming() { return incoming; }
    }
}