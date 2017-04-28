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

import com.oracle.graal.compiler.common.LIRKind;
import com.oracle.graal.graph.NodeClass;
import com.oracle.graal.lir.Variable;
import com.oracle.graal.nodeinfo.NodeInfo;
import com.oracle.graal.nodes.ValueNode;
import com.oracle.graal.nodes.calc.FloatingNode;
import com.oracle.graal.nodes.spi.LIRLowerable;
import com.oracle.graal.nodes.spi.NodeLIRBuilderTool;
import jdk.vm.ci.meta.Value;
import tornado.drivers.opencl.graal.OCLStampFactory;
import tornado.drivers.opencl.graal.asm.OCLAssembler.OCLBinaryOp;
import tornado.drivers.opencl.graal.lir.OCLBinary;
import tornado.drivers.opencl.graal.lir.OCLKind;
import tornado.drivers.opencl.graal.lir.OCLLIRStmt.AssignStmt;

import static tornado.graal.compiler.TornadoCodeGenerator.trace;

@NodeInfo(shortName = "-")
public class VectorSubNode extends FloatingNode implements LIRLowerable, VectorOp {

    public static final NodeClass<VectorSubNode> TYPE = NodeClass.create(VectorSubNode.class);

    @Input
    ValueNode x;
    @Input
    ValueNode y;

    public VectorSubNode(OCLKind kind, ValueNode x, ValueNode y) {
        this(TYPE, kind, x, y);
    }

    protected VectorSubNode(NodeClass<? extends VectorSubNode> c, OCLKind kind, ValueNode x, ValueNode y) {
        super(c, OCLStampFactory.getStampFor(kind));
        this.x = x;
        this.y = y;
    }

    public ValueNode getX() {
        return x;
    }

    public ValueNode getY() {
        return y;
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        LIRKind lirKind = gen.getLIRGeneratorTool().getLIRKind(stamp);
        final Variable result = gen.getLIRGeneratorTool().newVariable(lirKind);

        final Value input1 = gen.operand(x);
        final Value input2 = gen.operand(y);

        trace("emitVectorSub: %s + %s", input1, input2);
        gen.getLIRGeneratorTool().append(new AssignStmt(result, new OCLBinary.Expr(OCLBinaryOp.SUB, gen.getLIRGeneratorTool().getLIRKind(stamp), input1, input2)));
        gen.setResult(this, result);
    }

}
