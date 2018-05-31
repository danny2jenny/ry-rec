package com.rytec.rec.web.util;


import com.rytec.rec.app.RecBase;
import com.rytec.rec.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Video extends RecBase {

    @Autowired
    VideoService videoService;

    @RequestMapping("/video")
    @ResponseBody
    public void vOnlion(@RequestParam String parm) {
        videoService.onVideoMessage(parm);
    }
}
