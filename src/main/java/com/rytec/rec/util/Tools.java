package com.rytec.rec.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {

    /**
     * @param inArray  原数组
     * @param subArray 子数组
     * @return -1 表示未找到，否则表示找到的index
     */
    public static int findSubArray(byte[] inArray, byte[] subArray, int start) {

        if (inArray == null || subArray == null || inArray.length == 0 || subArray.length == 0)
            return -1;

        int i, j;

        for (i = start; i < inArray.length; i++) {
            if (inArray[i] == subArray[0]) {
                for (j = 1; j < subArray.length; j++) {
                    // 指针可能越界
                    if (i + j > inArray.length - 1) {
                        return -1;
                    }
                    if (inArray[i + j] != subArray[j])
                        break;
                }

                if (j == subArray.length)
                    return i;
            }
        }
        return -1;
    }


    /**
     * 当前时间的ISO字符串
     *
     * @return
     */
    public static String currentTimeIsoStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

}
