package com.rytec.rec.service.iec60870;

/**
 * 
 * 文件传输的阶段
 * @author danny
 *
 */
public enum FileStage {
	FILE_IDLE,
	FILE_READY,
	SECTION_READY,
	SECTION_TRANS,
	SAGEMENT_LAST,
	FILE_DOWN
}
