package org.apache.logging.log4j.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraceInfo {

    private String traceId;

    private int spanId;

    private String segmentId;
}
