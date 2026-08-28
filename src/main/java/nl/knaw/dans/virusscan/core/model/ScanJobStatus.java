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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScanJobStatus {

    public enum Status {
        PENDING, RUNNING, COMPLETED, FAILED
    }

    private final UUID jobId;
    private final List<Long> fileIds;
    private Status status;
    private int processedFiles;
    private final List<FileScanResult> results;

    public ScanJobStatus(UUID jobId, List<Long> fileIds) {
        this.jobId = jobId;
        this.fileIds = List.copyOf(fileIds);
        this.status = Status.PENDING;
        this.processedFiles = 0;
        this.results = new ArrayList<>();
    }

    public UUID getJobId() {
        return jobId;
    }

    public List<Long> getFileIds() {
        return fileIds;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getTotalFiles() {
        return fileIds.size();
    }

    public int getProcessedFiles() {
        return processedFiles;
    }

    public void setProcessedFiles(int processedFiles) {
        this.processedFiles = processedFiles;
    }

    public List<FileScanResult> getResults() {
        return results;
    }
}
