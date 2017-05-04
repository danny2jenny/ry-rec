<%@ page language="java" contentType="text/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


// 所有GIS的FEATURES
ry.gis.allFeatures = {};

// *************************** Hash 常量 **********************************
<c:forEach items = "${features}" var = "set">
ry.gis.allFeatures[${set.key}] = JSON.parse('${set.value}');
</c:forEach>