package com.cloudstorage.Cloudo.Repo;

import com.cloudstorage.Cloudo.models.CloudFile;
import com.cloudstorage.Cloudo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface CloudFileRepo extends JpaRepository<CloudFile, UUID> {
    List<CloudFile> findByOwner(User owner);//files belonging to a specific user

}
