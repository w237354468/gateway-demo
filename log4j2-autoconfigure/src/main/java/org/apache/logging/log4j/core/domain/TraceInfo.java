package org.apache.logging.log4j.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TraceInfo {

    private String traceId;

    private String spanId;

    private String segmentId;
}
