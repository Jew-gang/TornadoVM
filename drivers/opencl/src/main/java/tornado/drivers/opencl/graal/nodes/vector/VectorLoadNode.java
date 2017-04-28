/* 
 * Copyright 2012 James Clarkson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tornado.drivers.opencl.graal.nodes.vector;

import com.oracle.graal.graph.Node;
import com.oracle.graal.graph.NodeClass;
import com.oracle.graal.graph.spi.CanonicalizerTool;
import com.oracle.graal.nodeinfo.NodeInfo;
import com.oracle.graal.nodes.ValueNode;
import com.oracle.graal.nodes.java.AccessIndexedNode;
import jdk.vm.ci.meta.JavaKind;
import tornado.drivers.opencl.graal.OCLStamp;
import tornado.drivers.opencl.graal.OCLStampFactory;
import tornado.drivers.opencl.graal.lir.OCLKind;

/**
 * The {@code VectorLoadNode} represents a vector-read from a set of contiguous
 * elements of an array.
 */
@NodeInfo(nameTemplate = "VectorLoad")
public class VectorLoadNode extends AccessIndexedNode {

    public static final NodeClass<VectorLoadNode> TYPE = NodeClass
            .create(VectorLoadNode.class);

    private final OCLKind kind;

    /**
     * Creates a new LoadIndexedNode.
     *
     * @param kind  the element type
     * @param array the instruction producing the array
     * @param index the instruction producing the index
     */
    public VectorLoadNode(OCLKind kind, ValueNode array, ValueNode index) {
        super(TYPE, OCLStampFactory.getStampFor(kind), array, index, JavaKind.Illegal);
        this.kind = kind;
    }

    public Node canonical(CanonicalizerTool tool) {
        return this;
    }

    public int length() {
        return kind.getVectorLength();
    }

    public OCLKind elementType() {
        return kind.getElementKind();
    }

    public OCLKind vectorKind() {
        return kind;
    }

    @Override
    public JavaKind elementKind() {
        return ((OCLStamp) stamp()).getOCLKind().getElementKind().asJavaKind();
    }
}
