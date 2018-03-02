/*
 * This file is part of Tornado: A heterogeneous programming framework: 
 * https://github.com/beehive-lab/tornado
 *
 * Copyright (c) 2013-2018, APT Group, School of Computer Science,
 * The University of Manchester. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Authors: James Clarkson
 *
 */
package uk.ac.manchester.tornado.benchmarks.sgemm;

import uk.ac.manchester.tornado.benchmarks.BenchmarkDriver;
import uk.ac.manchester.tornado.benchmarks.BenchmarkRunner;

public class Benchmark extends BenchmarkRunner {

    private int width;
    private int height;

    @Override
    public void parseArgs(String[] args) {
        if (args.length == 3) {
            iterations = Integer.parseInt(args[0]);
            width = Integer.parseInt(args[1]);
            height = Integer.parseInt(args[2]);

        } else {
            iterations = 20;
            width = 512;
            height = 512;
        }
    }

    @Override
    protected String getName() {
        return "sgemm";
    }

    @Override
    protected String getIdString() {
        return String.format("%s-%d-%d-%d", getName(), iterations, width, height);
    }

    @Override
    protected String getConfigString() {
        return String.format("width=%d, height=%d", width, height);
    }

    @Override
    protected BenchmarkDriver getJavaDriver() {
        return new SgemmJava(iterations, width, height);
    }

    @Override
    protected BenchmarkDriver getTornadoDriver() {
        return new SgemmTornado(iterations, width, height);
    }

}