package com.gyreq.ai.example.lab07ragmilvus.dto;

/**
 * 上传响应 DTO
 *
 * @param chunkCount 入库成功的文本片段数量
 * @param fileName   文件名
 * @author gyreq
 * @since 1.0.0
 */
public record UploadResponseDTO(

        /**
         * 入库成功的文本片段数量
         */
        int chunkCount,

        /**
         * 文件名
         */
        String fileName

) {

    /**
     * 创建响应 DTO
     *
     * @param chunkCount 文本片段数量
     * @param fileName   文件名
     * @return 响应 DTO
     */
    public static UploadResponseDTO of(int chunkCount, String fileName) {
        return new UploadResponseDTO(chunkCount, fileName);
    }

}
