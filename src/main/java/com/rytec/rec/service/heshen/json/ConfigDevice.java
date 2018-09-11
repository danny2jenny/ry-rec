package com.rytec.rec.service.heshen.json;

import java.util.ArrayList;
import java.util.List;

public class ConfigDevice {
    public String devName;
    public int devType;
    public int area;
    public String latitude;
    public String longitude;
    public List<ConfigFunction> function = new ArrayList<ConfigFunction>();
}
