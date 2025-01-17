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
 * Authors: Michalis Papadimitriou
 *
 *
 * */
package uk.ac.manchester.tornado.drivers.opencl.graal.nodes;

import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.FixedWithNextNode;
import org.graalvm.compiler.nodes.LoopBeginNode;
import org.graalvm.compiler.nodes.spi.LIRLowerable;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;

@NodeInfo
public class ThreadConfigurationNode extends FixedWithNextNode implements LIRLowerable {

    @Successor
    LoopBeginNode loopBegin;
    @Input
    LocalWorkGroupDimensionsNode localWork;
    public static final NodeClass<ThreadConfigurationNode> TYPE = NodeClass.create(ThreadConfigurationNode.class);

    public ThreadConfigurationNode(LocalWorkGroupDimensionsNode localWork) {
        super(TYPE, StampFactory.forVoid());
        this.localWork = localWork;
    }

    /**
     * This an empty implementation for generating LIR for this node. This is due to
     * a fix template used during code generation for this node.
     */
    @Override
    public void generate(NodeLIRBuilderTool nodeLIRBuilderTool) {
    }
}