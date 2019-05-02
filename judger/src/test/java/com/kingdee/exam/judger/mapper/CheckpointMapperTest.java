package com.kingdee.exam.judger.mapper;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.kingdee.exam.judger.model.Checkpoint;

/**
 * CheckpointMapper测试类.
 */
@ExtendWith(SpringExtension.class)
@Transactional
@ContextConfiguration({"classpath:test-spring-context.xml"})
public class CheckpointMapperTest {
	/**
	 * 测试用例: 测试getCheckpointsUsingProblemId(long)方法
	 * 测试数据: 使用存在的试题唯一标识符(1000)
	 * 预期结果: 返回对应的测试点列表(10个项目)
	 */
	@Test
	public void testGetCheckpointsUsingProblemIdExists() {
		List<Checkpoint> checkpoints = checkpointMapper.getCheckpointsUsingProblemId(1000);
		Assertions.assertEquals(10, checkpoints.size());
		
		Checkpoint firstCheckpoint = checkpoints.get(0);
		String output = firstCheckpoint.getOutput();
		Assertions.assertEquals("45652\r\n", output);
	}
	
	/**
	 * 测试用例: 测试getCheckpointsUsingProblemId(long)方法
	 * 测试数据: 使用不存在的试题唯一标识符(0)
	 * 预期结果: 返回对应的测试点列表(0个项目)
	 */
	@Test
	public void testGetCheckpointsUsingProblemIdNotExists() {
		List<Checkpoint> checkpoints = checkpointMapper.getCheckpointsUsingProblemId(0);
		Assertions.assertEquals(0, checkpoints.size());
	}
	
	/**
	 * 待测试的CheckpointMapper对象.
	 */
	@Autowired
	private CheckpointMapper checkpointMapper;
}
