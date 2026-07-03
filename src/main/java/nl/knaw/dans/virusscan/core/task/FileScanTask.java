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
package nl.knaw.dans.virusscan.core.task;

import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.virusscan.core.model.FileScanResult;
import nl.knaw.dans.virusscan.core.model.ScanJobStatus;
import nl.knaw.dans.virusscan.core.service.DataverseApiService;
import nl.knaw.dans.virusscan.core.service.ScanJobStore;
import nl.knaw.dans.virusscan.core.service.VirusScanner;

import java.util.List;
import java.util.UUID;

@Slf4j
public class FileScanTask implements Runnable {

    private final DataverseApiService dataverseApiService;
    private final VirusScanner virusScanner;
    private final ScanJobStore scanJobStore;
    private final UUID jobId;

    public FileScanTask(DataverseApiService dataverseApiService, VirusScanner virusScanner, ScanJobStore scanJobStore, UUID jobId) {
        this.dataverseApiService = dataverseApiService;
        this.virusScanner = virusScanner;
        this.scanJobStore = scanJobStore;
        this.jobId = jobId;
    }

    @Override
    public void run() {
        try {
            runTask();
        }
        catch (Exception e) {
            log.error("Unexpected error in FileScanTask for job {}", jobId, e);
        }
    }

    void runTask() {
        var job = scanJobStore.get(jobId).orElseThrow(() -> new IllegalStateException("Job not found: " + jobId));

        job.setStatus(ScanJobStatus.Status.RUNNING);
        scanJobStore.update(job);

        var errorCount = 0;

        for (var fileId : job.getFileIds()) {
            var fileName = "file-" + fileId;
            log.debug("Scanning file {} (job {})", fileId, jobId);

            FileScanResult result;
            try {
                var viruses = dataverseApiService.getFile(Math.toIntExact(fileId),
                    response -> virusScanner.scanForVirus(response.getEntity().getContent()));

                if (viruses.isEmpty()) {
                    result = new FileScanResult(fileId, fileName, FileScanResult.Status.CLEAN, List.of());
                }
                else {
                    log.warn("Viruses found in file {} (job {}): {}", fileId, jobId, viruses);
                    result = new FileScanResult(fileId, fileName, FileScanResult.Status.INFECTED, viruses);
                }
            }
            catch (Exception e) {
                log.error("Error scanning file {} (job {})", fileId, jobId, e);
                result = new FileScanResult(fileId, fileName, FileScanResult.Status.ERROR, List.of());
                errorCount++;
            }

            job.getResults().add(result);
            job.setProcessedFiles(job.getProcessedFiles() + 1);
            scanJobStore.update(job);
        }

        var finalStatus = (errorCount == job.getTotalFiles()) ? ScanJobStatus.Status.FAILED : ScanJobStatus.Status.COMPLETED;
        job.setStatus(finalStatus);
        scanJobStore.update(job);

        log.info("Job {} finished with status {}", jobId, finalStatus);
    }
}
