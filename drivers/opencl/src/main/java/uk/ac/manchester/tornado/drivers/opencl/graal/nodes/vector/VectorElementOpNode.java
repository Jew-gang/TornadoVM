/*
 * Copyright (c) 2020, APT Group, Department of Computer Science,
 * School of Engineering, The University of Manchester. All rights reserved.
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
package uk.ac.manchester.tornado.drivers.opencl.graal.nodes.vector;

import static uk.ac.manchester.tornado.api.exceptions.TornadoInternalError.guarantee;
import static uk.ac.manchester.tornado.api.exceptions.TornadoInternalError.shouldNotReachHere;

import org.graalvm.compiler.core.common.LIRKind;
import org.graalvm.compiler.core.common.type.ObjectStamp;
import org.graalvm.compiler.core.common.type.Stamp;
import org.graalvm.compiler.core.common.type.StampFactory;
import org.graalvm.compiler.graph.NodeClass;
import org.graalvm.compiler.lir.ConstantValue;
import org.graalvm.compiler.nodeinfo.InputType;
import org.graalvm.compiler.nodeinfo.NodeInfo;
import org.graalvm.compiler.nodes.ConstantNode;
import org.graalvm.compiler.nodes.NodeView;
import org.graalvm.compiler.nodes.ValueNode;
import org.graalvm.compiler.nodes.calc.FloatingNode;
import org.graalvm.compiler.nodes.spi.LIRLowerable;
import org.graalvm.compiler.nodes.spi.NodeLIRBuilderTool;

import jdk.vm.ci.meta.JavaConstant;
import jdk.vm.ci.meta.Value;
import uk.ac.manchester.tornado.drivers.opencl.graal.OCLStamp;
import uk.ac.manchester.tornado.drivers.opencl.graal.lir.OCLKind;
import uk.ac.manchester.tornado.drivers.opencl.graal.lir.OCLVectorElementSelect;

/**
 * The {@code LoadIndexedNode} represents a read from an element of an array.
 */
@NodeInfo(nameTemplate = "Op .s{p#lane}")
public abstract class VectorElementOpNode extends FloatingNode implements LIRLowerable, Comparable<VectorElementOpNode> {

    public static final NodeClass<VectorElementOpNode> TYPE = NodeClass.create(VectorElementOpNode.class);

    @Input(InputType.Extension) ValueNode vector;

    @Input ValueNode lane;

    protected final OCLKind oclKind;

    protected VectorElementOpNode(NodeClass<? extends VectorElementOpNode> c, OCLKind kind, ValueNode vector, ValueNode lane) {
        super(c, StampFactory.forKind(kind.asJavaKind()));
        this.oclKind = kind;
        this.vector = vector;
        this.lane = lane;

        Stamp vstamp = vector.stamp(NodeView.DEFAULT);
        OCLKind vectorKind = OCLKind.ILLEGAL;
        if (vstamp instanceof ObjectStamp) {
            ObjectStamp ostamp = (ObjectStamp) vector.stamp(NodeView.DEFAULT);
            // System.out.printf("ostamp: type=%s\n", ostamp.type());

            if (ostamp.type() != null) {
                vectorKind = OCLKind.fromResolvedJavaType(ostamp.type());
                guarantee(vectorKind.isVector(), "Cannot apply vector operation to non-vector type: %s", vectorKind);
                guarantee(vectorKind.getVectorLength() >= laneId(), "Invalid lane %d on type %s", laneId(), oclKind);
            }
        } else if (vstamp instanceof OCLStamp) {
            final OCLStamp vectorStamp = (OCLStamp) vector.stamp(NodeView.DEFAULT);
            vectorKind = vectorStamp.getOCLKind();
            guarantee(vectorKind.isVector(), "Cannot apply vector operation to non-vector type: %s", vectorKind);
            guarantee(vectorKind.getVectorLength() >= laneId(), "Invalid lane %d on type %s", laneId(), oclKind);
        } else {
            shouldNotReachHere("invalid type on vector operation: %s (stamp=%s (class=%s))", vector, vector.stamp(NodeView.DEFAULT), vector.stamp(NodeView.DEFAULT).getClass().getName());
        }

    }

    @Override
    public int compareTo(VectorElementOpNode o) {
        return Integer.compare(laneId(), o.laneId());
    }

    @Override
    public boolean inferStamp() {
        // return false;
        return updateStamp(StampFactory.forKind(oclKind.asJavaKind()));
    }

    final public int laneId() {
        guarantee(lane instanceof ConstantNode, "Invalid lane: %s", lane);
        return (lane instanceof ConstantNode) ? lane.asJavaConstant().asInt() : -1;
    }

    public ValueNode getVector() {
        return vector;
    }

    public OCLKind getOCLKind() {
        return oclKind;
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        guarantee(vector != null, "vector is null");
        // System.out.printf("vector = %s,
        // origin=%s\n",vector,vector.getOrigin());
        Value targetVector = gen.operand(getVector());
        // if (targetVector == null && vector.getOrigin() instanceof Invoke) {
        // targetVector = gen.operand(vector.getOrigin());
        // }

        guarantee(targetVector != null, "vector is null 2");
        final OCLVectorElementSelect element = new OCLVectorElementSelect(gen.getLIRGeneratorTool().getLIRKind(stamp), targetVector,
                new ConstantValue(LIRKind.value(OCLKind.INT), JavaConstant.forInt(laneId())));
        gen.setResult(this, element);

    }

}
