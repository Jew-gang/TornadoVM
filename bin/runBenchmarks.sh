#!/bin/bash

PACKAGE="tornado.benchmarks"
BENCHMARKS="sadd saxpy sgemm spmv addvector dotvector rotatevector rotateimage convolvearray convolveimage"
MAIN_CLASS="Benchmark"

TORNADO_CMD="tornado"

if [ -z "${TORNADO_FLAGS}" ]; then
	TORNADO_FLAGS="-Xms8G -Dtornado.kernels.coarsener=False -Dtornado.profiles.dump=False -Dtornado.profiling.enable=True -Dtornado.opencl.schedule=True"
fi

DATE=$(date '+%Y-%m-%d-%H:%M')

RESULTS_ROOT="${TORNADO_ROOT}/var/results"
BM_ROOT="${RESULTS_ROOT}/${DATE}"

if [ -z "${DEVICES}" ]; then
	echo "Please set env variable DEVICES."
	echo "	e.g. DEVICES=0:0,0:1"
	exit
fi

if [ ! -d ${BM_ROOT} ]; then
  mkdir -p ${BM_ROOT}
fi

LOGFILE="${BM_ROOT}/bm"

ITERATIONS=10

if [ ! -z "${TORNADO_FLAGS}" ];then
  echo ${TORNADO_FLAGS} > "${BM_ROOT}/tornado.flags"
fi

if [ -e ${TORNADO_ROOT}/.git ]; then
	echo $(git rev-parse HEAD) > "${BM_ROOT}/git.sha"
fi

${TORNADO_CMD} -Xms8G -Ddevices=${DEVICES} -DstartSize=2 -DendSize=16777216 tornado.benchmarks.DataMovement > "${BM_ROOT}/data-movement.csv"

for bm in ${BENCHMARKS}; do
	for (( i=0; i<${ITERATIONS}; i++ )); do
		echo "running ${i} ${bm} ..."
		OUTFILE="${LOGFILE}-${bm}-${i}.log"
		${TORNADO_CMD} ${TORNADO_FLAGS} tornado.benchmarks.BenchmarkRunner ${PACKAGE}.${bm}.${MAIN_CLASS} >> "${OUTFILE}"
		${TORNADO_ROOT}/bin/convert2csv.sh ${OUTFILE}
	done
done

