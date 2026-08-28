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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScanJobStoreImpl implements ScanJobStore {

    private final ConcurrentHashMap<UUID, ScanJobStatus> store = new ConcurrentHashMap<>();

    @Override
    public ScanJobStatus create(List<Long> fileIds) {
        var job = new ScanJobStatus(UUID.randomUUID(), fileIds);
        store.put(job.getJobId(), job);
        return job;
    }

    @Override
    public Optional<ScanJobStatus> get(UUID jobId) {
        return Optional.ofNullable(store.get(jobId));
    }

    @Override
    public void update(ScanJobStatus job) {
        store.put(job.getJobId(), job);
    }
}
