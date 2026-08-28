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
package nl.knaw.dans.virusscan.core.service;

import nl.knaw.dans.virusscan.core.task.FileScanTask;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class FileScanTaskFactoryImpl implements FileScanTaskFactory {

    private final DataverseApiService dataverseApiService;
    private final VirusScanner virusScanner;
    private final ScanJobStore scanJobStore;
    private final ExecutorService executorService;

    public FileScanTaskFactoryImpl(DataverseApiService dataverseApiService, VirusScanner virusScanner, ScanJobStore scanJobStore, ExecutorService executorService) {
        this.dataverseApiService = dataverseApiService;
        this.virusScanner = virusScanner;
        this.scanJobStore = scanJobStore;
        this.executorService = executorService;
    }

    @Override
    public UUID startTask(List<Long> fileIds) {
        var job = scanJobStore.create(fileIds);
        executorService.submit(new FileScanTask(dataverseApiService, virusScanner, scanJobStore, job.getJobId()));
        return job.getJobId();
    }
}
