package com.rytec.rec.service.heshen.json;

import java.util.ArrayList;
import java.util.List;

public class ConfigMaster {
    public int id;
    public String name;
    public String vender;
    public String createtime;
    public String ip;
    public String port;
    public List<ConfigDevice> devices = new ArrayList<ConfigDevice>();
}
