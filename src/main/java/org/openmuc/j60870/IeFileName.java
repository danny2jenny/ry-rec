package org.openmuc.j60870;

/**
 * 生成文件名
 */
public class IeFileName extends InformationElement {

    private final byte[] segment;

    public IeFileName(byte[] segment){
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
