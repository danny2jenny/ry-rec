<%--
  Created by IntelliJ IDEA.
  User: 12793
  Date: 2017/4/14
  Time: 1:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>RYTEC 智能辅助控制系统 </title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">


    <%--extjs 启动器#--%>
    <script type="text/javascript" src="extjs/bootstrap.js"></script>

    <%--中文化--%>
    <script type="text/javascript" src="extjs/locale/ext-lang-zh_CN.js"></script>

    <%--Extjs 的主题选择#--%>
    <link rel="stylesheet" type="text/css" href="/css/mark.css"/>
    <link rel="stylesheet" type="text/css" href="extjs/resources/css/ext-all-neptune-debug.css"/>
    <link rel="stylesheet" type="text/css" href="extjs/resources/ext-theme-neptune/ext-theme-neptune-all-debug.css"/>


    <%--以下的JS加载必须严格按照顺序--%>

    <%--jQuery--%>
    <script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="js/stomp.js"></script>


    <%--OpenLayers--%>
    <link rel="stylesheet" href="openlayers/ol.css" type="text/css">
    <script type="text/javascript" src="openlayers/ol-debug.js"></script>

    <!-- OL3 Ext Toolbar -->
    <link rel="stylesheet" href="openlayers/control/controlbar.css" type="text/css"/>
    <script type="text/javascript" src="openlayers/control/controlbar.js"></script>
    <script type="text/javascript" src="openlayers/control/buttoncontrol.js"></script>
    <script type="text/javascript" src="openlayers/control/togglecontrol.js"></script>

    <%--Layer Switch--%>
    <link rel="stylesheet" href="openlayers/control/layerswitcherimagecontrol.css"/>
    <script type="text/javascript" src="openlayers/control/layerswitchercontrol.js"></script>
    <script type="text/javascript" src="openlayers/control/layerswitcherimagecontrol.js"></script>
    <script type="text/javascript" src="openlayers/getpreview.js"></script>

    <%--Overlay弹出--%>
    <link rel="stylesheet" href="openlayers/overlay/popupoverlay.css"/>
    <script type="text/javascript" src="openlayers/overlay/popupoverlay.js"></script>

    <%--Extjs Direct 的配置文件--%>
    <script type="text/javascript" src="/srv/api.js"></script>

    <%--extjs 工具#--%>
    <script type="text/javascript" src="srv/config"></script>
    <script type="text/javascript" src="extjs_app/lib/util.js"></script>

    <%--主程序入口--%>
    <script type="text/javascript" src="extjs_app/Admin.js"></script>
</head>
<body id="body">

</body>
</html>
