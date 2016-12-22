<%@ page language="java" contentType="text/javascript; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

//读取数据库常量

// 控制器通道对应的资源类型
    ry.constant.ctrChaResType = [
        <c:forEach items = "${ctlCharResType}" var = "res" varStatus = "status">
        <c:choose>
        <c:when test="${status.last}">
        [${res.id}, '${res.name}']
            </c:when>
            <c:otherwise>
            [${res.id}, '${res.name}'],
    </c:otherwise>
    </c:choose>
    </c:forEach>
    ];

// 资源类型
    ry.constant.resourceType = [
        <c:forEach items = "${resType}" var = "res" varStatus = "status">
        <c:choose>
        <c:when test="${status.last}">
        [${res.id}, '${res.name}']
            </c:when>
            <c:otherwise>
            [${res.id}, '${res.name}'],
    </c:otherwise>
    </c:choose>
    </c:forEach>
    ];

// 控制器通道类型
    ry.constant.ctrChaType = [
        <c:forEach items = "${resCat}" var = "res" varStatus = "status">
        <c:choose>
        <c:when test="${status.last}">
        [${res.id}, '${res.name}']
            </c:when>
            <c:otherwise>
            [${res.id}, '${res.name}'],
    </c:otherwise>
    </c:choose>
    </c:forEach>
    ];

// 告警类型
    ry.constant.almtype = [
        <c:forEach items = "${almType}" var = "res" varStatus = "status">
        <c:choose>
        <c:when test="${status.last}">
        [${res.id}, '${res.name}']
            </c:when>
            <c:otherwise>
            [${res.id}, '${res.name}'],
    </c:otherwise>
    </c:choose>
    </c:forEach>
    ];

// 联动的动作类型
    ry.constant.actiontype = [
        <c:forEach items = "${action}" var = "res" varStatus = "status">
        <c:choose>
        <c:when test="${status.last}">
        [${res.id}, '${res.name}']
            </c:when>
            <c:otherwise>
            [${res.id}, '${res.name}'],
    </c:otherwise>
    </c:choose>
    </c:forEach>
    ];

// 用户类型
    ry.constant.usertype = [
        <c:forEach items = "${userType}" var = "res" varStatus = "status">
        <c:choose>
        <c:when test="${status.last}">
        [${res.id}, '${res.name}']
            </c:when>
            <c:otherwise>
            [${res.id}, '${res.name}'],
    </c:otherwise>
    </c:choose>
    </c:forEach>
    ];

// 用户等级
    ry.constant.userRank = [
        <c:forEach items = "${userRank}" var = "res" varStatus = "status">
        <c:choose>
        <c:when test="${status.last}">
        [${res.id}, '${res.name}']
            </c:when>
            <c:otherwise>
            [${res.id}, '${res.name}'],
    </c:otherwise>
    </c:choose>
    </c:forEach>
    ];
    ry.constant.almtypeRank = [
        <c:forEach items = "${almType}" var = "res" varStatus = "status">
        <c:choose>
        <c:when test="${status.last}">
        [${res.id}, '${res.rank}']
            </c:when>
            <c:otherwise>
            [${res.id}, '${res.rank}'],
    </c:otherwise>
    </c:choose>
    </c:forEach>
    ];
// 告警等级
    ry.constant.alarmRank = [
        <c:forEach items = "${alarmRank}" var = "res" varStatus = "status">
        <c:choose>
        <c:when test="${status.last}">
        [${res.id}, '${res.name}']
            </c:when>
            <c:otherwise>
            [${res.id}, '${res.name}'],
    </c:otherwise>
    </c:choose>
    </c:forEach>
    ];

