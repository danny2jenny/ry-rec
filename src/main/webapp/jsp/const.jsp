<%@ page language="java" contentType="text/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

/**
 * Created by danny on 17-1-31.
 * 生成JS所需要的常量
 */

ry = {};
ry.CONST = {};

// *************************** Hash 常量 **********************************
<c:forEach items = "${constHashMap}" var = "set">
ry.${set.key} = [
    <c:forEach items = "${set.value}" var = "val" varStatus = "status">
    <c:choose>
    <c:when test="${status.last}">
    [${val.key},'${val.value}']
    </c:when>
    <c:otherwise>
    [${val.key},'${val.value}'],
    </c:otherwise>
    </c:choose>
    </c:forEach>
];
</c:forEach>

// *************************** Value 常量 **********************************
<c:forEach items = "${constValues}" var = "set">
ry.CONST.${set.key} = {
    <c:forEach items = "${set.value}" var = "val" varStatus = "status">
    <c:choose>
    <c:when test="${status.last}">
    ${val.key}:${val.value}
    </c:when>
    <c:otherwise>
    ${val.key}:${val.value},
    </c:otherwise>
    </c:choose>
    </c:forEach>
};
</c:forEach>