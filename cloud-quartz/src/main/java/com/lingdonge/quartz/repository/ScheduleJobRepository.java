package com.lingdonge.quartz.repository;

import com.lingdonge.db.repository.BaseRepository;
import com.lingdonge.quartz.domain.ScheduleJob;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleJobRepository extends BaseRepository<ScheduleJob, Integer> {

    List<ScheduleJob> findAllByJobStatus(String jobStatus);

}
