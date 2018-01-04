package org.openmuc.j60870;

/**
 * by Danny
 * 生成文件名
 */
public class IeFileNameStr extends InformationElement {

    private final byte[] segment;

    public IeFileNameStr(byte[] segment){
        this.segment = segment;
    }

    @Override
    int encode(byte[] buffer, int i) {
        System.arraycopy(segment, 0, buffer, i, segment.length);
        return segment.length;
    }

    @Override
    public String toString() {
        return "Filename of length: " + segment.length;
    }
}
