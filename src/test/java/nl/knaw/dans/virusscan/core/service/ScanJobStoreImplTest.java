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

import nl.knaw.dans.virusscan.core.model.ScanJobStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ScanJobStoreImplTest {

    @Test
    void create_returns_job_with_pending_status_and_correct_file_count() {
        var store = new ScanJobStoreImpl();
        var job = store.create(List.of(1L, 2L, 3L));

        assertThat(job.getJobId()).isNotNull();
        assertThat(job.getStatus()).isEqualTo(ScanJobStatus.Status.PENDING);
        assertThat(job.getTotalFiles()).isEqualTo(3);
        assertThat(job.getProcessedFiles()).isEqualTo(0);
        assertThat(job.getResults()).isEmpty();
    }

    @Test
    void get_returns_empty_for_unknown_job() {
        var store = new ScanJobStoreImpl();
        assertThat(store.get(UUID.randomUUID())).isEmpty();
    }

    @Test
    void get_returns_job_after_create() {
        var store = new ScanJobStoreImpl();
        var job = store.create(List.of(10L));

        assertThat(store.get(job.getJobId())).isPresent();
    }

    @Test
    void update_persists_status_change() {
        var store = new ScanJobStoreImpl();
        var job = store.create(List.of(1L));

        job.setStatus(ScanJobStatus.Status.COMPLETED);
        store.update(job);

        assertThat(store.get(job.getJobId()).orElseThrow().getStatus())
            .isEqualTo(ScanJobStatus.Status.COMPLETED);
    }

    @Test
    void create_generates_unique_ids_for_each_job() {
        var store = new ScanJobStoreImpl();
        var job1 = store.create(List.of(1L));
        var job2 = store.create(List.of(2L));

        assertThat(job1.getJobId()).isNotEqualTo(job2.getJobId());
    }
}
