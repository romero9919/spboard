package net.musecom.spboard;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.musecom.spboard.dao.SpBoardDao;
import net.musecom.spboard.dao.UploadDao;
import net.musecom.spboard.dto.UploadFileDto;

@RestController
public class DeleteController {
	
	@Autowired
	UploadDao uploadDao;
	
	@Autowired
	SpBoardDao dao;
	
	@Autowired
	ServletContext servletContext;
	
	@Autowired
	UploadFileDto fDto;
	
    @PostMapping("/del")
    public String delOk(@RequestParam("id") int id, @RequestParam("pass") String pass) {
        try {
            System.out.println("삭제 로직 수행");
            // 삭제 로직 코드
            //비번검증
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
    		params.put("pass", pass);
    		int rs = dao.validatePass(params);
            
    		if(rs > 0) {
    			
    		 //파일 삭제를 위한 경로 설정
    		 String path = servletContext.getRealPath("/resources/upload/");
    		 
    		 //파일정보 가져오기
    		 List<UploadFileDto> fDtos = uploadDao.selectFileByBoardId(id);
    		 for(UploadFileDto fDto : fDtos) {
    			 String fileName = fDto.getNfilename();
    	    	 File file = new File(path + fileName);
    	    	 if(file.exists()) {
    	    		file.delete();
    	    	 }
    	    	//db 레코드 삭제
    	    	 uploadDao.deleteFile(fDto.getId());
    		 }
    		  //본문 테이블 삭제
    		  dao.delete(id);
    		  return "1";
    		}else {	
              return "0";
    		}
    		
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}
