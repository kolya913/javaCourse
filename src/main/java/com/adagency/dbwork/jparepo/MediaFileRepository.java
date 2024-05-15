package com.adagency.dbwork.jparepo;

import com.adagency.model.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
}
