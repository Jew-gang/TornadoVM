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
package tornado.drivers.opencl.runtime;

public class OCLMemoryRegions {

    private int globalSize;
    private int constantSize;
    private int localSize;
    private int privateSize;
    private byte[] constantData;

    public OCLMemoryRegions() {
        this.globalSize = 0;
        this.constantSize = 0;
        this.localSize = 0;
        this.privateSize = 0;
        this.constantData = null;
    }

    public void allocGlobal(int size) {
        this.globalSize = size;
    }

    public void allocLocal(int size) {
        this.localSize = size;
    }

    public void allocPrivate(int size) {
        this.privateSize = size;
    }

    public void allocConstant(int size) {
        this.constantSize = size;
        constantData = new byte[size];
    }

    public int getGlobalSize() {
        return globalSize;
    }

    public int getConstantSize() {
        return constantSize;
    }

    public int getLocalSize() {
        return localSize;
    }

    public int getPrivateSize() {
        return privateSize;
    }

    public byte[] getConstantData() {
        return constantData;
    }

}
