package tornado.drivers.opencl.graal.nodes;

import com.oracle.graal.compiler.common.LIRKind;
import com.oracle.graal.compiler.common.type.StampFactory;
import com.oracle.graal.graph.Node;
import com.oracle.graal.graph.NodeClass;
import com.oracle.graal.graph.spi.CanonicalizerTool;
import com.oracle.graal.lir.Variable;
import com.oracle.graal.lir.gen.ArithmeticLIRGeneratorTool;
import com.oracle.graal.nodeinfo.NodeInfo;
import com.oracle.graal.nodes.ConstantNode;
import com.oracle.graal.nodes.ValueNode;
import com.oracle.graal.nodes.calc.TernaryNode;
import com.oracle.graal.nodes.spi.ArithmeticLIRLowerable;
import com.oracle.graal.nodes.spi.NodeLIRBuilderTool;
import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.Value;
import tornado.collections.math.TornadoMath;
import tornado.common.exceptions.TornadoInternalError;
import tornado.drivers.opencl.graal.lir.OCLArithmeticTool;
import tornado.drivers.opencl.graal.lir.OCLBuiltinTool;
import tornado.drivers.opencl.graal.lir.OCLLIRStmt.AssignStmt;

import static tornado.common.exceptions.TornadoInternalError.shouldNotReachHere;

@NodeInfo(nameTemplate = "{p#operation}")
public class OCLIntTernaryIntrinsicNode extends TernaryNode implements ArithmeticLIRLowerable {

    protected OCLIntTernaryIntrinsicNode(ValueNode x, ValueNode y, ValueNode z, Operation op, JavaKind kind) {
        super(TYPE, StampFactory.forKind(kind), x, y, z);
        this.operation = op;
    }

    public static final NodeClass<OCLIntTernaryIntrinsicNode> TYPE = NodeClass
            .create(OCLIntTernaryIntrinsicNode.class);
    protected final Operation operation;

    public enum Operation {
        CLAMP,
        MAD_HI,
        MAD_SAT,
        MAD24
    }

    public Operation operation() {
        return operation;
    }

    public static ValueNode create(ValueNode x, ValueNode y, ValueNode z, Operation op, JavaKind kind) {
        ValueNode c = tryConstantFold(x, y, z, op, kind);
        if (c != null) {
            return c;
        }
        return new OCLIntTernaryIntrinsicNode(x, y, z, op, kind);
    }

    protected static ValueNode tryConstantFold(ValueNode x, ValueNode y, ValueNode z, Operation op, JavaKind kind) {
        ConstantNode result = null;

        if (x.isConstant() && y.isConstant() && z.isConstant()) {
            if (kind == JavaKind.Int) {
                int ret = doCompute(x.asJavaConstant().asInt(), y.asJavaConstant().asInt(), z.asJavaConstant().asInt(), op);
                result = ConstantNode.forInt(ret);
            } else if (kind == JavaKind.Long) {
                long ret = doCompute(x.asJavaConstant().asLong(), y.asJavaConstant().asLong(), z.asJavaConstant().asInt(), op);
                result = ConstantNode.forLong(ret);
            }
        }
        return result;
    }

    @Override
    public void generate(NodeLIRBuilderTool builder, ArithmeticLIRGeneratorTool lirGen) {
        OCLBuiltinTool gen = ((OCLArithmeticTool) lirGen).getGen().getOCLBuiltinTool();

        Value x = getX().isConstant() ? builder.operand(getX()) : builder.operand(getX());
        Value y = getY().isConstant() ? builder.operand(getY()) : builder.operand(getY());
        Value z = getZ().isConstant() ? builder.operand(getZ()) : builder.operand(getZ());
        LIRKind lirKind = builder.getLIRGeneratorTool().getLIRKind(stamp);
        Variable result = builder.getLIRGeneratorTool().newVariable(lirKind);
        Value expr;
        switch (operation()) {
            case CLAMP:
                expr = gen.genIntClamp(x, y, z);
                break;
            case MAD24:
                expr = gen.genIntMad24(x, y, z);
                break;
            case MAD_HI:
                expr = gen.genIntMadHi(x, y, z);
                break;
            case MAD_SAT:
                expr = gen.genIntMadSat(x, y, z);
                break;
            default:
                throw shouldNotReachHere();
        }
        builder.getLIRGeneratorTool().append(new AssignStmt(result, expr));
        builder.setResult(this, result);

    }

    private static long doCompute(long x, long y, long z, Operation op) {
        switch (op) {
            default:
                throw new TornadoInternalError("unknown op %s", op);
        }
    }

    private static int doCompute(int x, int y, int z, Operation op) {
        switch (op) {
            case CLAMP:
                return TornadoMath.clamp(x, y, z);

            default:
                throw new TornadoInternalError("unknown op %s", op);
        }
    }

    @Override
    public Node canonical(CanonicalizerTool tool, ValueNode x, ValueNode y, ValueNode z) {
        ValueNode c = tryConstantFold(x, y, z, operation(), getStackKind());
        if (c != null) {
            return c;
        }
        return this;
    }

}
