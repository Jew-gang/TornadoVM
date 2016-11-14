package tornado.examples.arrays;


import java.util.Arrays;
import tornado.api.Parallel;
import tornado.api.Read;
import tornado.api.Write;
import tornado.common.enums.Access;
import tornado.drivers.opencl.OpenCL;
import tornado.runtime.api.PrebuiltTask;
import tornado.runtime.api.TaskGraph;
import tornado.runtime.api.TaskUtils;

public class ArrayAddIntPrebuilt {
	
	public static void add(@Read int[] a,@Read int[] b,@Write int[] c){
		for(@Parallel int i=0;i<c.length;i++)
			c[i] = a[i] + b[i];
	}

	public static void main(String[] args) {
		
		final int numElements = 8;
		int[] a = new int[numElements];
		int[] b = new int[numElements];
		int[] c = new int[numElements];
		
		Arrays.fill(a, 1);
		Arrays.fill(b, 2);
		Arrays.fill(c, 0);
		
                final PrebuiltTask add = TaskUtils.createTask(
                        "add",
                        "opencl/add.cl",
                        new Object[]{a,b,c}, 
                        new Access[]{Access.READ,Access.READ,Access.WRITE},
                        OpenCL.defaultDevice(),
                        new int[]{numElements});
		
		final TaskGraph graph = new TaskGraph();
		graph
			.add(add)
			.streamOut(c)
			.mapAllTo(OpenCL.defaultDevice())
			.schedule()
			.waitOn();
		
		System.out.println("a: " + Arrays.toString(a));
		System.out.println("b: " + Arrays.toString(b));
		System.out.println("c: " + Arrays.toString(c));
	}

}