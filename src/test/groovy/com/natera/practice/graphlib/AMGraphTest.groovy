package com.natera.practice.graphlib

import spock.lang.Specification

class AMGraphTest extends Specification {

    def "Should support directed graph"() {
        expect: "Directed graph creation"
        new AMGraph(true)
    }

    def "Should support undirected graph"() {
        expect: "Undirected graph creation"
        new AMGraph(false)
    }

    def "Should be able to add a vertex to the graph"() {
        given: "A graph"
        def graph = new AMGraph<String,Integer>(false)
        when:  "we try to add a vertex"
        graph.insertVertex("Test string")
        then:  "it is successfully added to the graph"
        graph.vertices().find(v->v.getWeight()=="Test string")
    }

    def "Should be able to add an edge to the graph"() {
        given: "A graph with at least two vertices to connect"
        def graph = new AMGraph<String,Integer>(false)
        def firstVertex = graph.insertVertex("First")
        def secondVertex = graph.insertVertex("Second")
        when:  "we try to add an edge between these vertices"
        graph.insertEdge(firstVertex, secondVertex, 1)
        then:  "it is successfully added to the graph"
        graph.edges().find(e->e.getWeight()==1)
    }

    def "Should compute a path between vertices"() {
        given: "A graph with connected vertices"
        def graph = new AMGraph<String,Integer>(false)
        def firstVertex = graph.insertVertex("First")
        def secondVertex = graph.insertVertex("Second")
        def thirdVertex = graph.insertVertex("Second")
        def firstEdge = graph.insertEdge(firstVertex, thirdVertex, 1)
        def secondEdge = graph.insertEdge(thirdVertex,secondVertex,1)
        when: "we request a path between two connected vertices"
        def path = GraphLib.computePath(graph, firstVertex, secondVertex)
        then:
        path.size() == 2
        path.get(0) == secondEdge
        path.get(1) == firstEdge
    }
}
