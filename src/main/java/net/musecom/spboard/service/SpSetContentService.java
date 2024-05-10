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
public class SpSetContentService implements SpService {
   
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
		
		//dto.setImnum(System.currentTimeMillis() / 1000L); //unix 타임 스탬프 값
		dto.setUserid("GUEST");
		if("ok".equals(req.getParameter("rewrite"))) {
		    dto.setRefid(Integer.parseInt(req.getParameter("refid")));
		    dto.setDepth(Integer.parseInt(req.getParameter("depth")));
		    dto.setRenum(Integer.parseInt(req.getParameter("renum")));
		    
			Map<String, Object> params2 = new HashMap();
			params2.put("refid", dto.getRefid());
			params2.put("renum", dto.getRenum());
			dao.updateRenum(params2);
		}
						
		dao.insert(dto);
		if(!"ok".equals(req.getParameter("rewrite"))) {
		   dao.updateRefId(dto.getId());
		}
		Map<String, Object> params = new HashMap();
	    params.put("jboard_id", dto.getId());
		params.put("imnum", dto.getImnum());
		
		fdao.updateFile(params);
		
	}

}
