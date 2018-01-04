package com.rytec.rec.service.iec60870;

import com.rytec.rec.app.RecBase;
import org.openmuc.j60870.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 文件目录列表
 */
@Service
public class RecAsduFile extends RecBase {

    @Value("${iec60870.xml}")
    String xmlFileName;

    /**
     * 返回目录列表
     *
     * @param revAsdu
     * @return
     */
    public ASdu getAsdu(ASdu revAsdu) {
        ASdu asduBack;

        File xml = new File(xmlFileName);
        if (xml.exists() && xml.isFile()) {

        } else {
            return null;
        }
        asduBack = new ASdu(TypeId.F_DR_TA_1, 1, CauseOfTransmission.REQUEST, false, false, 0,
                revAsdu.getCommonAddress(),
                new InformationObject[]{
                        new InformationObject(0,
                                new InformationElement[][]{
                                        {new IeNameOfFile(0)},      // 文件名称Index
                                        {new IeLengthOfFileOrSection((int) xml.length())},    // 文件长度
                                        {new IeStatusOfFile(0, false, false, false)}, // 文件状态
                                        {new IeTime56(xml.lastModified())}, // 修改时间
                                        {new IeFileNameStr(xml.getName().getBytes())},  // 文件名称
                                        {new IeChecksum(0)}
                                }

                        )});
        return asduBack;
    }
}
