all: build

build:
	./bin/compile.sh jdk-8

graal-jdk-8: 
	./bin/compile.sh graal-jdk-8

graal-jdk-11: 
	./bin/compile.sh graal-jdk-11

offline:
	./bin/compile.sh jdk-8 OFFLINE

clean: 
	mvn clean

example:
	tornado --printKernel --debug uk.ac.manchester.tornado.examples.VectorAddInt 8192

tests:
	tornado-test.py --verbose
	tornado-test.py -V -J"-Dtornado.heap.allocation=1MB" uk.ac.manchester.tornado.unittests.fails.HeapFail#test03
	test-native.sh 

test-slam:
	tornado-test.py -V --fast uk.ac.manchester.tornado.unittests.slam.graphics.GraphicsTests 


test-hdgraphics:
	@[ "${HDGRAPHICS_ID}" ] || ( echo ">> HDGRAPHICS_ID is not set. Please do \`export HDGRAPHICS_ID=<dev-id>\`"; exit 1 )
	tornado-test.py -V -J"-Ds0.t0.device=0:${HDGRAPHICS_ID} -Ds0.t1.device=0:${HDGRAPHICS_ID}" uk.ac.manchester.tornado.unittests.reductions.TestReductionsFloats
	tornado-test.py -V -J"-Ds0.t0.device=0:${HDGRAPHICS_ID} -Ds0.t1.device=0:${HDGRAPHICS_ID}" uk.ac.manchester.tornado.unittests.reductions.TestReductionsDoubles
	tornado-test.py -V -J"-Ds0.t0.device=0:${HDGRAPHICS_ID} -Ds0.t1.device=0:${HDGRAPHICS_ID}" uk.ac.manchester.tornado.unittests.reductions.TestReductionsIntegers
	tornado-test.py -V -J"-Ds0.t0.device=0:${HDGRAPHICS_ID} -Ds0.t1.device=0:${HDGRAPHICS_ID}" uk.ac.manchester.tornado.unittests.reductions.TestReductionsLong


eclipse:
	mvn eclipse:eclipse

clean-graphs:
	rm *.cfg *.bgv *.log

