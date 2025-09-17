package com.bjw.testtable.file.repository;

import java.util.Collection;
import java.util.List;
public interface FileRepositoryCustom {
    List<Long> findPostIdsHavingFiles(Collection<Long> postIds);
}
