/*******************************************************************************
 * Copyright 2011 Beintoo
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.beintoo.utils;

import android.util.Log;

public class DebugUtility {
	static public boolean isDebugEnable = false;
	public static void showLog (String output){
		if(isDebugEnable){
            int chunkSize = 4000;
            int chunkCount = (int)Math.ceil(((double)output.length()) / 4000.0);     // integer division
            for (int i = 0; i < chunkCount; i++) {
                int max = chunkSize * (i + 1);
                if(output.length() < chunkSize){
                    Log.d("DEBUG: ", output);
                } else if (max >= output.length()) {
                    Log.d("DEBUG: ", output.substring(4000 * i));
                } else {
                    Log.d("DEBUG: ", output.substring(4000 * i, max));
                }
            }
        }
	}
}
