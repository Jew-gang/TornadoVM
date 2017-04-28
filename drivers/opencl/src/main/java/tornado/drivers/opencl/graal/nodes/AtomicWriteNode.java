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
package tornado.drivers.opencl.graal.nodes;

import com.oracle.graal.compiler.common.LocationIdentity;
import com.oracle.graal.graph.NodeClass;
import com.oracle.graal.lir.gen.LIRGeneratorTool;
import com.oracle.graal.nodeinfo.NodeInfo;
import com.oracle.graal.nodes.ValueNode;
import com.oracle.graal.nodes.memory.AbstractWriteNode;
import com.oracle.graal.nodes.memory.address.AddressNode;
import com.oracle.graal.nodes.spi.LIRLowerable;
import com.oracle.graal.nodes.spi.NodeLIRBuilderTool;
import tornado.drivers.opencl.graal.asm.OCLAssembler.OCLBinaryIntrinsic;

import static tornado.common.exceptions.TornadoInternalError.unimplemented;

@NodeInfo(shortName = "Atomic Write")
public class AtomicWriteNode extends AbstractWriteNode implements LIRLowerable {

    public static final NodeClass<AtomicWriteNode> TYPE = NodeClass
            .create(AtomicWriteNode.class);

    OCLBinaryIntrinsic op;

    public AtomicWriteNode(
            OCLBinaryIntrinsic op,
            AddressNode address, LocationIdentity location, ValueNode value) {
        super(TYPE, address, location, value, BarrierType.NONE);
        this.op = op;
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        final LIRGeneratorTool tool = gen.getLIRGeneratorTool();
        unimplemented();
//        final LocationNode location = location();
//
//        final Value object = gen.operand(object());
//
//        final MemoryAccess addressOfObject = (MemoryAccess) location.generateAddress(gen, tool,
//                object);
////		addressOfObject.setKind(value().getKind());
//
//        final Value valueToStore = gen.operand(value());
//
//        tool.append(new OCLLIRInstruction.ExprStmt(new OCLBinary.Intrinsic(op, JavaKind.Illegal,
//                addressOfObject, valueToStore)));
//        trace("emitAtomicWrite: %s(%s, %s)", op.toString(),
//                addressOfObject, valueToStore);

    }

    @Override
    public boolean canNullCheck() {
        return false;
    }

}
