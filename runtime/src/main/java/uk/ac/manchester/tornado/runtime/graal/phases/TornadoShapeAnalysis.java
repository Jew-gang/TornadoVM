/*
 * Copyright (c) 2018, 2019, APT Group, School of Computer Science,
 * The University of Manchester. All rights reserved.
 * Copyright (c) 2009, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Authors: James Clarkson
 *
 */
package uk.ac.manchester.tornado.runtime.graal.phases;

import java.util.Collections;
import java.util.List;

import org.graalvm.compiler.graph.NodeBitMap;
import org.graalvm.compiler.loop.LoopEx;
import org.graalvm.compiler.loop.LoopFragmentInside;
import org.graalvm.compiler.loop.LoopsData;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.LoopBeginNode;
import org.graalvm.compiler.nodes.StructuredGraph;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.phases.BasePhase;

import uk.ac.manchester.tornado.runtime.common.Tornado;
import uk.ac.manchester.tornado.runtime.domain.DomainTree;
import uk.ac.manchester.tornado.runtime.domain.IntDomain;
import uk.ac.manchester.tornado.runtime.graal.nodes.ParallelRangeNode;

/**
 * It analyses the loop index space and determines the correct indices using
 * strides in loops.
 *
 */
public class TornadoShapeAnalysis extends BasePhase<TornadoHighTierContext> {

    private static int resolveInt(ValueNode value) {
        if (value instanceof ConstantNode) {
            return ((ConstantNode) value).asJavaConstant().asInt();
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int getMaxLevelNestedLoops(StructuredGraph graph) {
        int dimensions = 1;

        if (graph.hasLoops()) {
            final LoopsData data = new LoopsData(graph);
            data.detectedCountedLoops();

            final List<LoopEx> loops = data.outerFirst();

            for (int i = 0; i < loops.size(); i++) {
                LoopEx loopEx = loops.get(i);
                LoopFragmentInside inside = loopEx.inside();
                NodeBitMap nodes = inside.nodes();

                List<LoopBeginNode> snapshot = nodes.filter(LoopBeginNode.class).snapshot();
                if (snapshot.size() > 1) {
                    dimensions = Math.max(dimensions, snapshot.size());
                }
            }
        }
        return dimensions;
    }

    private void setDomainTree(int dimensions, List<ParallelRangeNode> ranges, TornadoHighTierContext context) {
        final DomainTree domainTree = new DomainTree(dimensions);

        int lastIndex = -1;
        boolean valid = true;
        for (int i = 0; i < dimensions; i++) {
            final ParallelRangeNode range = ranges.get(i);
            final int index = range.index();
            if (index != lastIndex && resolveInt(range.offset().value()) != Integer.MIN_VALUE && resolveInt(range.stride().value()) != Integer.MIN_VALUE
                    && resolveInt(range.value()) != Integer.MIN_VALUE) {
                domainTree.set(index, new IntDomain(resolveInt(range.offset().value()), resolveInt(range.stride().value()), resolveInt(range.value())));
            } else {
                valid = false;
                Tornado.info("unsupported multiple parallel loops");
                break;
            }
            lastIndex = index;
        }

        if (valid) {
            Tornado.trace("loop nest depth = %d", domainTree.getDepth());
            Tornado.debug("discovered parallel domain: %s", domainTree);
            context.getMeta().setDomain(domainTree);
        }
    }

    @Override
    protected void run(StructuredGraph graph, TornadoHighTierContext context) {

        if (!context.hasMeta()) {
            return;
        }

        int dimensions = getMaxLevelNestedLoops(graph);

        final List<ParallelRangeNode> ranges = graph.getNodes().filter(ParallelRangeNode.class).snapshot();
        if (ranges.size() < dimensions) {
            dimensions = ranges.size();
        }
        Collections.sort(ranges);

        setDomainTree(dimensions, ranges, context);

    }

}
