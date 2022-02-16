package com.huanghongbe.zoom.utils.ServerInfo;

import lombok.Data;

/**
 * 內存相关信息
 *
 */
@Data
public class Mem {
    /**
     * 内存总量
     */
    private double total;

    /**
     * 已用内存
     */
    private double used;

    /**
     * 剩余内存
     */
    private double free;

    /**
     * 使用率
     */
    private double usage;
}
