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
package nl.knaw.dans.virusscan.resource;

import lombok.extern.slf4j.Slf4j;
import nl.knaw.dans.virusscan.api.ScanJobStatusDto;
import nl.knaw.dans.virusscan.api.StartDatasetScanRequestDto;
import nl.knaw.dans.virusscan.api.StartFileScanRequestDto;
import nl.knaw.dans.virusscan.api.StartScanResponseDto;
import nl.knaw.dans.virusscan.resources.ScanApi;

import javax.ws.rs.core.Response;
import java.util.UUID;

@Slf4j
public class ScanResourceImpl implements ScanApi {

    @Override
    public Response getScanJobStatus(UUID jobId) {
        log.info("Received request for scan job status: {}", jobId);
        // TODO: implement
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Override
    public Response startDatasetScan(StartDatasetScanRequestDto startDatasetScanRequestDto) {
        log.info("Received request to start dataset scan: {}", startDatasetScanRequestDto);
        // TODO: implement
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Override
    public Response startFileScan(StartFileScanRequestDto startFileScanRequestDto) {
        log.info("Received request to start file scan: {}", startFileScanRequestDto);
        // TODO: implement
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
