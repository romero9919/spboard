package net.musecom.spboard.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import net.musecom.spboard.dto.UploadFileDto;

@Mapper
public interface UploadMapper {
    
	public int insertFile(UploadFileDto udto);
    
    public int deleteFile(int uploadId);
    
    public int deleteAllFile(int boardId);
    
    public int deleteDumpFile();
    
    public List<UploadFileDto> selectFileByBoardId(int boardId);
    
    public UploadFileDto selectFileById(int uploadId);
    
    public void updateFile(Map<String, Object> params);

}
