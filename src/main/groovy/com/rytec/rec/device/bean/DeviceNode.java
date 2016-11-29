package com.rytec.rec.device.bean;

/**
 * Created by danny on 16-11-22.
 * Device 和 node 的映射关系
 */
public class DeviceNode {
    public int id;
    public int dno;
    public String dname;
    public int dtype;
    public String lnodetype;
    public int lnodenum;
    public int nid;
    public int cid;
    public int nno;
    public int ntype;
    public String conf;
    public int nfun;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDno() {
        return dno;
    }

    public void setDno(int dno) {
        this.dno = dno;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public int getDtype() {
        return dtype;
    }

    public void setDtype(int dtype) {
        this.dtype = dtype;
    }

    public String getLnodetype() {
        return lnodetype;
    }

    public void setLnodetype(String lnodetype) {
        this.lnodetype = lnodetype;
    }

    public int getLnodenum() {
        return lnodenum;
    }

    public void setLnodenum(int lnodenum) {
        this.lnodenum = lnodenum;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getNno() {
        return nno;
    }

    public void setNno(int nno) {
        this.nno = nno;
    }

    public int getNtype() {
        return ntype;
    }

    public void setNtype(int ntype) {
        this.ntype = ntype;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public int getNfun() {
        return nfun;
    }

    public void setNfun(int nfun) {
        this.nfun = nfun;
    }
}
