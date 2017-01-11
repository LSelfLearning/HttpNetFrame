package com.start.lewish.filedownload_lib.sqlite;

/**
 * author: sundong
 * created at 2017/1/11 14:44
 */
public class DownloadEntity {

    private int  id = -1;
    private long startPos;
    private long endPos;
    private long progressPos;
    private String downloadUrl;

    public DownloadEntity() {

    }

    public DownloadEntity(long startPos, long endPos, long progressPos, String downloadUrl) {
        this(-1,startPos,endPos,progressPos,downloadUrl);
    }

    public DownloadEntity(int id, long startPos, long endPos, long progressPos, String downloadUrl) {
        this.id = id;
        this.startPos = startPos;
        this.endPos = endPos;
        this.progressPos = progressPos;
        this.downloadUrl = downloadUrl;
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        this.endPos = endPos;
    }

    public long getProgressPos() {
        return progressPos;
    }

    public void setProgressPos(long progressPos) {
        this.progressPos = progressPos;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DownloadEntity{" +
                "id=" + id +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                ", progressPos=" + progressPos +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadEntity that = (DownloadEntity) o;

        if (id != that.id) return false;
        if (startPos != that.startPos) return false;
        if (endPos != that.endPos) return false;
        if (progressPos != that.progressPos) return false;
        return downloadUrl != null ? downloadUrl.equals(that.downloadUrl) : that.downloadUrl == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (startPos ^ (startPos >>> 32));
        result = 31 * result + (int) (endPos ^ (endPos >>> 32));
        result = 31 * result + (int) (progressPos ^ (progressPos >>> 32));
        result = 31 * result + (downloadUrl != null ? downloadUrl.hashCode() : 0);
        return result;
    }
}
