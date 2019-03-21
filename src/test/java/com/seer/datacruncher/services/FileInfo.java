/*
 * Copyright (c) 2019  Altimari Mario
 * All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.seer.datacruncher.services;

public class FileInfo {
    private String fileName;
    private long fileSize;
    private String md5;

    public FileInfo() {
    }

    public FileInfo(String fileName, long fileSize, String md5) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.md5 = md5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (fileSize != fileInfo.fileSize) return false;
        if (fileName != null ? !fileName.equals(fileInfo.fileName) : fileInfo.fileName != null) return false;
        return md5 != null ? md5.equals(fileInfo.md5) : fileInfo.md5 == null;
    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + (int) (fileSize ^ (fileSize >>> 32));
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        return result;
    }
}
