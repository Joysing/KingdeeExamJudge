package com.kingdee.exam.judger.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * 用于比对用户输出结果和标准结果.
 */
@Component
public class Comparator {
	/**
	 * 获取用户输出和标准输出的比对结果.
	 * @param standardOutputFilePath - 标准输出文件路径
	 * @param outputFilePath - 用户输出文件路径
	 * @return 用户输出和标准输出是否相同
	 */
    boolean isOutputTheSame(String standardOutputFilePath,
                            String outputFilePath) throws IOException {
		File stdFile = new File(standardOutputFilePath);
		File file = new File(outputFilePath);

		LineIterator stdFileItr = FileUtils.lineIterator(stdFile, "UTF-8");
		LineIterator fileItr = FileUtils.lineIterator(file, "UTF-8");
		boolean isFileOutputTheSame = isFileOutputTheSame(stdFileItr, fileItr);
		
		LineIterator.closeQuietly(stdFileItr);
		LineIterator.closeQuietly(fileItr);
		return isFileOutputTheSame;
	}
	
	/**
	 * 比对标准输出和用户输出是否相同.
	 * @param stdFileItr - 标准输出文件的迭代器
	 * @param fileItr - 用户输出文件的迭代器
	 * @return 标准输出和用户输出是否相同
	 */
	private boolean isFileOutputTheSame(LineIterator stdFileItr, LineIterator fileItr) {
		try {
			while ( stdFileItr.hasNext() && fileItr.hasNext() ) {
				String stdLine = stdFileItr.nextLine();
				String line = fileItr.nextLine();
				
				if ( !isLineOutputTheSame(stdLine, line) ) {
					return false;
				}
			}
			while ( stdFileItr.hasNext() ) {
				String line = stdFileItr.nextLine();
				if ( !isLineEmpty(line) ) {
					return false;
				}
			}
			while ( fileItr.hasNext() ) {
				String line = fileItr.nextLine();
				if ( !isLineEmpty(line) ) {
					return false;
				}
			}
		} catch ( OutOfMemoryError ex ) {
			LOGGER.catching(ex);
			return false;
		}
		return true;
	}
	
	/**
	 * 比对某行的标准输出和用户输出(忽略行尾空格).
	 * @param stdLine - 标准输出中的某一行
	 * @param line - 用户输出中的某一行
	 * @return 某行的标准输出和用户输出是否相同
	 */
	private boolean isLineOutputTheSame(String stdLine, String line) {
		for ( int i = 0, j = 0; i < stdLine.length() && j < line.length(); ++ i, ++ j ) {
			if (  stdLine.charAt(i) != line.charAt(j) ) {
				if ( stdLine.charAt(i) == '\n' ) {
                    return isLineEmpty(line);
                } else if ( line.charAt(j) == '\n' ) {
                    return isLineEmpty(stdLine);
                }
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 忽略文件结尾的空行与空格.
	 * @param line - 某行文件内容
	 * @return 该行内容中是否只包含空格和换行符
	 */
	private boolean isLineEmpty(String line) {
		for ( int i = 0; i < line.length(); ++ i ) {
			if ( !(line.charAt(i) == ' ' || line.charAt(i) == '\n') ) {
				return false;
			}
		}
		return true;
	}
	
	private static final Logger LOGGER = LogManager.getLogger(Comparator.class);
}
