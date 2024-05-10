package net.musecom.spboard.service;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.musecom.spboard.dao.UploadDao;
import net.musecom.spboard.dto.UploadFileDto;

@Repository
public class TrashFIleDelete {

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private UploadDao fDao;	
	
	@Transactional
	public void excute() {

	    List<UploadFileDto> fdaos = fDao.selectFileByBoardId(0);    
	   
	    for(UploadFileDto fdao : fdaos) {
	    	String delFilename = fdao.getNfilename();
	    	try {
	    		//파일삭제
	    		String path = servletContext.getRealPath("/resources/upload/");
	    		File file = new File(path + delFilename);
	    		if(file.exists()) {
	    			file.delete();
	    		}
	    		//db 레코드 삭제
	    		fDao.deleteFile(fdao.getId());
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	}
	
}
