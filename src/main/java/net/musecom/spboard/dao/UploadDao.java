package net.musecom.spboard.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import net.musecom.spboard.dto.UploadFileDto;

@Repository
public class UploadDao implements UploadMapper {

	@Autowired
	private SqlSession session;
	
	@Override
	public int insertFile(UploadFileDto udto) {

		return session.insert("insertFile", udto);
	}

	@Override
	public int deleteFile(int uploadId) {
		// TODO Auto-generated method stub
		return session.delete("deleteFile", uploadId);
	}
	
	@Override
	public int deleteAllFile(int boardId) {
		// TODO Auto-generated method stub
		return session.delete("deleteAllFile", boardId);
	}

	@Override
	public int deleteDumpFile() {
		// TODO Auto-generated method stub
		return session.delete("deleteDumpFile");
	}

	@Override
	public List<UploadFileDto> selectFileByBoardId(int boardId) {
		// TODO Auto-generated method stub
		return session.selectList("selectByBoardId", boardId);
	}

	@Override
	public UploadFileDto selectFileById(int uploadId) {
		// TODO Auto-generated method stub
		return session.selectOne("selectById", uploadId);
	}

	@Override
	public void updateFile(Map<String, Object> params) {
		session.update("updateFile", params);
	}


}
