package com.kingdee.exam.judger.mapper;

import java.util.List;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.kingdee.exam.judger.model.Checkpoint;

@CacheNamespace(implementation = org.mybatis.caches.ehcache.EhcacheCache.class)
public interface CheckpointMapper {
	/**
	 * 获取某个试题的全部测试点.
	 * @param problemId - 试题的唯一标识符
	 * @return 某个试题的全部测试点
	 */
	@Select("SELECT * FROM voj_problem_checkpoints WHERE problem_id = #{problemId}")
	@Options()
	@Results({
		@Result(property = "problemId", column = "problem_id"),
		@Result(property = "checkpointId", column = "checkpoint_id"),
		@Result(property = "isExactlyMatch", column = "checkpoint_exactly_match"),
		@Result(property = "score", column = "checkpoint_score"),
		@Result(property = "input", column = "checkpoint_input"),
		@Result(property = "output", column = "checkpoint_output"),
	})
	List<Checkpoint> getCheckpointsUsingProblemId(@Param("problemId") long problemId);
}
