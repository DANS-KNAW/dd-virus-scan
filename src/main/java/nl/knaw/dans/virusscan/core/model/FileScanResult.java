/*
 * Copyright (C) 2022 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.virusscan.core.model;

import java.util.List;

public class FileScanResult {

    public enum Status {
        PENDING, CLEAN, INFECTED, ERROR
    }

    private final long fileId;
    private final String fileName;
    private final Status status;
    private final List<String> viruses;

    public FileScanResult(long fileId, String fileName, Status status, List<String> viruses) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.status = status;
        this.viruses = List.copyOf(viruses);
    }

    public long getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public Status getStatus() {
        return status;
    }

    public List<String> getViruses() {
        return viruses;
    }
}
