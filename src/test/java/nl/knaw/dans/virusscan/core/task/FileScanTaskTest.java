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

import nl.knaw.dans.virusscan.core.model.FileScanResult;
import nl.knaw.dans.virusscan.core.model.ScanJobStatus;
import nl.knaw.dans.virusscan.core.service.DataverseApiService;
import nl.knaw.dans.virusscan.core.service.ScanJobStore;
import nl.knaw.dans.virusscan.core.service.ScanJobStoreImpl;
import nl.knaw.dans.virusscan.core.service.VirusScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;

class FileScanTaskTest {

    private DataverseApiService dataverseApiService;
    private VirusScanner virusScanner;
    private ScanJobStore scanJobStore;

    @BeforeEach
    void setUp() {
        dataverseApiService = Mockito.mock(DataverseApiService.class);
        virusScanner = Mockito.mock(VirusScanner.class);
        scanJobStore = new ScanJobStoreImpl();
    }

    @Test
    void run_all_files_clean_sets_status_completed() throws Exception {
        Mockito.when(dataverseApiService.getFile(anyInt(), any())).thenReturn(List.of());

        var job = scanJobStore.create(List.of(1L, 2L, 3L));
        new FileScanTask(dataverseApiService, virusScanner, scanJobStore, job.getJobId()).runTask();

        var result = scanJobStore.get(job.getJobId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(ScanJobStatus.Status.COMPLETED);
        assertThat(result.getProcessedFiles()).isEqualTo(3);
        assertThat(result.getResults()).hasSize(3);
        assertThat(result.getResults()).allMatch(r -> r.getStatus() == FileScanResult.Status.CLEAN);
        assertThat(result.getResults()).allMatch(r -> r.getViruses().isEmpty());
    }

    @Test
    void run_infected_file_records_virus_name() throws Exception {
        Mockito.when(dataverseApiService.getFile(anyInt(), any())).thenReturn(List.of("EICAR-Test-File"));

        var job = scanJobStore.create(List.of(42L));
        new FileScanTask(dataverseApiService, virusScanner, scanJobStore, job.getJobId()).runTask();

        var result = scanJobStore.get(job.getJobId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(ScanJobStatus.Status.COMPLETED);
        assertThat(result.getResults()).hasSize(1);

        var fileResult = result.getResults().get(0);
        assertThat(fileResult.getStatus()).isEqualTo(FileScanResult.Status.INFECTED);
        assertThat(fileResult.getViruses()).containsExactly("EICAR-Test-File");
    }

    @Test
    void run_file_error_records_error_status_and_continues() throws Exception {
        Mockito.when(dataverseApiService.getFile(Mockito.eq(10), any())).thenThrow(new IOException("connection refused"));
        Mockito.when(dataverseApiService.getFile(Mockito.eq(20), any())).thenReturn(List.of());

        var job = scanJobStore.create(List.of(10L, 20L));
        new FileScanTask(dataverseApiService, virusScanner, scanJobStore, job.getJobId()).runTask();

        var result = scanJobStore.get(job.getJobId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(ScanJobStatus.Status.COMPLETED);
        assertThat(result.getProcessedFiles()).isEqualTo(2);

        var results = result.getResults();
        assertThat(results.get(0).getStatus()).isEqualTo(FileScanResult.Status.ERROR);
        assertThat(results.get(1).getStatus()).isEqualTo(FileScanResult.Status.CLEAN);
    }

    @Test
    void run_all_files_error_sets_status_failed() throws Exception {
        Mockito.when(dataverseApiService.getFile(anyInt(), any())).thenThrow(new IOException("network error"));

        var job = scanJobStore.create(List.of(1L, 2L));
        new FileScanTask(dataverseApiService, virusScanner, scanJobStore, job.getJobId()).runTask();

        var result = scanJobStore.get(job.getJobId()).orElseThrow();
        assertThat(result.getStatus()).isEqualTo(ScanJobStatus.Status.FAILED);
        assertThat(result.getResults()).allMatch(r -> r.getStatus() == FileScanResult.Status.ERROR);
    }

    @Test
    void run_sets_status_running_then_completed() throws Exception {
        var statusDuringRun = new ScanJobStatus.Status[1];
        var job = scanJobStore.create(List.of(1L));

        var capturingStore = Mockito.spy(scanJobStore);
        Mockito.doAnswer(inv -> {
            ScanJobStatus s = inv.getArgument(0);
            if (s.getStatus() == ScanJobStatus.Status.RUNNING) {
                statusDuringRun[0] = ScanJobStatus.Status.RUNNING;
            }
            return inv.callRealMethod();
        }).when(capturingStore).update(any());

        Mockito.when(dataverseApiService.getFile(anyInt(), any())).thenReturn(List.of());

        new FileScanTask(dataverseApiService, virusScanner, capturingStore, job.getJobId()).runTask();

        assertThat(statusDuringRun[0]).isEqualTo(ScanJobStatus.Status.RUNNING);
        assertThat(capturingStore.get(job.getJobId()).orElseThrow().getStatus()).isEqualTo(ScanJobStatus.Status.COMPLETED);
    }
}
