/*
 * This file is part of Tornado: A heterogeneous programming framework: 
 * https://github.com/beehive-lab/tornadovm
 *
 * Copyright (c) 2020, APT Group, Department of Computer Science,
 * School of Engineering, The University of Manchester. All rights reserved.
 * Copyright (c) 2013-2019, APT Group, School of Computer Science,
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
package uk.ac.manchester.tornado.drivers.opencl.builtins;

import org.graalvm.compiler.api.replacements.Fold;

import uk.ac.manchester.tornado.api.annotations.ReductionOp;

public class OpenCLIntrinsics {

    public static native int get_global_id(int value);

    public static native int get_local_id(int value);

    public static native int get_global_size(int value);

    public static native int get_local_size(int value);

    public static native int get_group_id(int value);

    public static native int get_group_size(int value);

    /**
     * <p>
     * <code> 
     *  barrier(CLK_LOCAL_MEM_FENCE);
     * </code>
     * </p>
     */
    public static native void localBarrier();

    /**
     * <p>
     * <code> 
     *  barrier(CLK_GLOBAL_MEM_FENCE);
     * </code>
     * </p>
     */
    public static native void globalBarrier();

    public static native void printf();

    public static native void printEmpty();

    public static native void createLocalMemory(int[] array, int size);

    public static int fmax(float a, float b) {
        return 0;
    }

    @Fold
    public static int fmax() {
        return 0;
    }

    public static <T1, T2, R> R op(ReductionOp op, T1 x, T2 y) {
        return null;
    }

}
