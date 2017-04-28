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
package tornado.drivers.opencl.graal.phases;

import com.oracle.graal.nodes.*;
import com.oracle.graal.phases.BasePhase;
import tornado.graal.phases.TornadoHighTierContext;

@Deprecated
public class TornadoInvokeCleanup extends BasePhase<TornadoHighTierContext> {

    @Override
    protected void run(StructuredGraph graph, TornadoHighTierContext context) {
        graph.getNodes().filter(InvokeWithExceptionNode.class).forEach(invoke -> {
            //System.out.printf("cleaning: %s\n",invoke);
//            final List<GuardingPiNode> guardingPis = invoke.usages().filter(GuardingPiNode.class).snapshot();
//
//            if (invoke.exceptionEdge() != null) {
//                invoke.killExceptionEdge();
//            }
//
//            AbstractBeginNode begin = invoke.next();
//            if (begin instanceof KillingBeginNode) {
//                AbstractBeginNode newBegin = new BeginNode();
//                graph.addAfterFixed(begin, graph.add(newBegin));
//                begin.replaceAtUsages(newBegin);
//                graph.removeFixed(begin);
//            }
        });
    }

}
