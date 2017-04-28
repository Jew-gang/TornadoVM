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

import com.oracle.graal.compiler.common.type.StampFactory;
import com.oracle.graal.graph.NodeClass;
import com.oracle.graal.nodeinfo.NodeInfo;
import com.oracle.graal.nodes.calc.FloatingNode;
import com.oracle.graal.nodes.spi.LIRLowerable;
import com.oracle.graal.nodes.spi.NodeLIRBuilderTool;
import jdk.vm.ci.meta.Value;
import tornado.drivers.opencl.graal.meta.OCLMemorySpace;
import tornado.drivers.opencl.graal.meta.OCLStack;

import static tornado.common.exceptions.TornadoInternalError.guarantee;

@NodeInfo
public class OCLMemoryRegion extends FloatingNode implements LIRLowerable {

    public static final NodeClass<OCLMemoryRegion> TYPE = NodeClass.create(OCLMemoryRegion.class);

    public static enum Region {
        GLOBAL, LOCAL, PRIVATE, CONSTANT, STACK, HEAP;
    }

    protected Region region;

    public OCLMemoryRegion(Region region) {
        super(TYPE, StampFactory.objectNonNull());
        this.region = region;
    }

    @Override
    public void generate(NodeLIRBuilderTool gen) {
        Value value = null;
        switch (region) {
            case GLOBAL:
                value = OCLMemorySpace.GLOBAL;
                break;
            case LOCAL:
                value = OCLMemorySpace.LOCAL;
                break;
            case CONSTANT:
                value = OCLMemorySpace.CONSTANT;
                break;
            case PRIVATE:
                value = OCLMemorySpace.PRIVATE;
                break;
            case STACK:
                value = OCLStack.STACK;
                break;
            case HEAP:
                value = OCLMemorySpace.HEAP;
                break;

        }

        guarantee(value != null, "unimplemented region: %s", region);
        gen.setResult(this, value);
    }

}
