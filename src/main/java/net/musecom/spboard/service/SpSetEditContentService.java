package net.musecom.spboard.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import net.musecom.spboard.dao.SpBoardDao;
import net.musecom.spboard.dao.UploadDao;
import net.musecom.spboard.dto.SpBoardDto;

@Service
public class SpSetEditContentService implements SpService {
   
	@Autowired
    SpBoardDao dao;	

	@Autowired
	UploadDao fdao;
	
    @Override
	public void excute(Model model) {
		
		Map<String, Object> map = model.asMap();
		HttpServletRequest req = (HttpServletRequest) map.get("request");
		
		SpBoardDto dto = new SpBoardDto();
		
		dto.setTitle(req.getParameter("title"));
		dto.setContent(req.getParameter("content"));
		dto.setWriter(req.getParameter("writer"));
		dto.setPass(req.getParameter("pass"));
		dto.setImnum(req.getParameter("imnum"));
		dto.setId(Integer.parseInt(req.getParameter("id")));
		
		//dto.setImnum(System.currentTimeMillis() / 1000L); //unix 타임 스탬프 값
			
		dao.update(dto);

		Map<String, Object> params = new HashMap();
	    params.put("jboard_id", dto.getId());
		params.put("imnum", dto.getImnum());
		
		fdao.updateFile(params);
		
	}

}
