package com.thoth.server.model.domain;

import java.time.Instant;

public interface BackupListItem {

    String getIdentifier();
    String getResourceId();

    Instant getTimestamp();

}
