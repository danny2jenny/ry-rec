package com.rytec.rec.service.iec60870;

// 60870 会话的阶段
public enum SessionStage {
	IDLE, // 空闲状态
	INIT, // 初始化阶段
	TIME_SYNC, // 时间同步
	FILE_LIST, // 文件列表
	CALL_ALL, // 总召
	FILE // 文件传输
}
