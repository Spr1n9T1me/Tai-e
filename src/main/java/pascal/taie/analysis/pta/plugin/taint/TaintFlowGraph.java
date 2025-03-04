/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.pta.plugin.taint;

import pascal.taie.analysis.graph.flowgraph.FlowEdge;
import pascal.taie.analysis.graph.flowgraph.Node;
import pascal.taie.util.collection.Maps;
import pascal.taie.util.collection.MultiMap;
import pascal.taie.util.collection.Sets;
import pascal.taie.util.collection.Views;
import pascal.taie.util.graph.Graph;

import java.util.Map;
import java.util.Set;

public class TaintFlowGraph implements Graph<Node> {

    private final Map<Node, SourcePoint> sourceNode2SourcePoint;

    private final Map<Node, SinkPoint> sinkNode2SinkPoint;

    private final Set<Node> nodes = Sets.newHybridSet();

    private final MultiMap<Node, FlowEdge> inEdges = Maps.newMultiMap();

    private final MultiMap<Node, FlowEdge> outEdges = Maps.newMultiMap();

    TaintFlowGraph(Map<Node, SourcePoint> sourceNode2SourcePoint,
                   Map<Node, SinkPoint> sinkNode2SinkPoint) {
        this.sourceNode2SourcePoint = sourceNode2SourcePoint;
        this.sinkNode2SinkPoint = sinkNode2SinkPoint;
        nodes.addAll(getSourceNodes());
        nodes.addAll(getSinkNodes());
    }

    Map<Node, SourcePoint> getSourceNode2SourcePoint() {
        return sourceNode2SourcePoint;
    }

    Map<Node, SinkPoint> getSinkNode2SinkPoint() {
        return sinkNode2SinkPoint;
    }

    Set<Node> getSourceNodes() {
        return this.sourceNode2SourcePoint.keySet();
    }

    Set<Node> getSinkNodes() {
        return this.sinkNode2SinkPoint.keySet();
    }

    void addEdge(FlowEdge edge) {
        nodes.add(edge.source());
        nodes.add(edge.target());
        inEdges.put(edge.target(), edge);
        outEdges.put(edge.source(), edge);
    }

    @Override
    public Set<Node> getPredsOf(Node node) {
        return Views.toMappedSet(getInEdgesOf(node), FlowEdge::source);
    }

    @Override
    public Set<FlowEdge> getInEdgesOf(Node node) {
        return inEdges.get(node);
    }

    @Override
    public Set<Node> getSuccsOf(Node node) {
        return Views.toMappedSet(getOutEdgesOf(node), FlowEdge::target);
    }

    @Override
    public Set<FlowEdge> getOutEdgesOf(Node node) {
        return outEdges.get(node);
    }

    @Override
    public Set<Node> getNodes() {
        return nodes;
    }
}
