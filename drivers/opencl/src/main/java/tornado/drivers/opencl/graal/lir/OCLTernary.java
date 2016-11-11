package tornado.drivers.opencl.graal.lir;

import com.oracle.graal.compiler.common.LIRKind;
import com.oracle.graal.lir.LIRInstruction.Use;
import com.oracle.graal.lir.Opcode;
import jdk.vm.ci.meta.Value;
import tornado.drivers.opencl.graal.asm.OCLAssembler;
import tornado.drivers.opencl.graal.asm.OCLAssembler.OCLTernaryOp;
import tornado.drivers.opencl.graal.asm.OCLAssembler.OCLTernaryTemplate;
import tornado.drivers.opencl.graal.compiler.OCLCompilationResultBuilder;

import static tornado.drivers.opencl.graal.asm.OCLAssemblerConstants.BRACKET_CLOSE;
import static tornado.drivers.opencl.graal.asm.OCLAssemblerConstants.BRACKET_OPEN;

public class OCLTernary {

    /**
     * Abstract operation which consumes two inputs
     */
    protected static class TernaryConsumer extends OCLLIROp {

        @Opcode
        protected final OCLTernaryOp opcode;

        @Use
        protected Value x;
        @Use
        protected Value y;
        @Use
        protected Value z;

        protected TernaryConsumer(OCLTernaryOp opcode, LIRKind lirKind, Value x, Value y, Value z) {
            super(lirKind);
            this.opcode = opcode;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void emit(OCLCompilationResultBuilder crb, OCLAssembler asm) {
            opcode.emit(crb, x, y, z);
        }

        @Override
        public String toString() {
            return String.format("%s %s %s %s", opcode.toString(), x, y, z);
        }

    }

    public static class Expr extends TernaryConsumer {

        public Expr(OCLTernaryOp opcode, LIRKind lirKind, Value x, Value y, Value z) {
            super(opcode, lirKind, x, y, z);
        }
    }

    public static class Select extends TernaryConsumer {

        protected OCLLIROp condition;

        public Select(LIRKind lirKind, OCLLIROp condition, Value y, Value z) {
            super(OCLTernaryTemplate.SELECT, lirKind, null, y, z);
            this.condition = condition;
        }

        @Override
        public void emit(OCLCompilationResultBuilder crb, OCLAssembler asm) {
            asm.emit(BRACKET_OPEN);
            condition.emit(crb, asm);
            asm.emit(BRACKET_CLOSE);
            asm.emit(" ? ");
            asm.emitValue(crb, y);
            asm.emit(" : ");
            asm.emitValue(crb, z);
        }
    }

    /**
     * OpenCL intrinsic call which consumes three inputs
     */
    public static class Intrinsic extends TernaryConsumer {

        public Intrinsic(OCLTernaryOp opcode, LIRKind lirKind, Value x, Value y, Value z) {
            super(opcode, lirKind, x, y, z);
        }

        @Override
        public String toString() {
            return String.format("%s(%s, %s, %s)", opcode.toString(), x, y, z);
        }
    }

}
