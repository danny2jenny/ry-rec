package com.rytec.rec.util;

import java.util.Arrays;
import java.util.Collections;

public class Tools {

    /**
     * @param inArray  原数组
     * @param subArray 子数组
     * @return -1 表示未找到，否则表示找到的index
     */
    public static int findSubArray(byte[] inArray, byte[] subArray)
    {

        if (inArray == null || subArray == null || inArray.length == 0 || subArray.length == 0)
            return -1;
        int i, j;

        for (i = 0; i < inArray.length; i++)
        {
            if (inArray[i] == subArray[0])
            {
                for (j = 1; j < subArray.length; j++)
                {
                    if (inArray[i + j] != subArray[j])
                        break;
                }

                if (j == subArray.length)
                    return i;
            }
        }
        return -1;
    }

}
