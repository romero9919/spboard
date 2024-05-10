package net.musecom.spboard;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.musecom.spboard.dao.SpBoardDao;
import net.musecom.spboard.dao.UploadDao;
import net.musecom.spboard.dto.UploadFileDto;
import net.musecom.spboard.service.SpGetContentService;
import net.musecom.spboard.service.SpGetListService;
import net.musecom.spboard.service.SpSetContentService;
import net.musecom.spboard.service.SpSetEditContentService;
import net.musecom.spboard.service.TrashFIleDelete;

@Controller
public class BbsController {
   
	@Autowired
	SpGetListService getList;
	
	@Autowired
	SpGetContentService getContent;
	
	//insert
	@Autowired
	SpSetContentService setContent;
	
	@Autowired
	UploadFileDto uploadFileDto;
	
	@Autowired
	UploadDao uploadDao;
	
	@Autowired
	SpBoardDao dao;
	
	@Autowired
	ServletContext servletContext;
	
	@Autowired
	TrashFIleDelete trashFileDelete;
	
	@Autowired
	SpSetEditContentService setEditContent;
	
	@RequestMapping("/list")
	public String list(
			@RequestParam(value="cpg", defaultValue="1") int cpg, 
			@RequestParam(value="searchname", defaultValue="") String searchname,
			@RequestParam(value="searchvalue", defaultValue="") String searchvalue,
			Model model) {
		System.out.println("list() 실행됨");
        model.addAttribute("cpg" , cpg);
        model.addAttribute("searchname", searchname);
        model.addAttribute("searchvalue", searchvalue);
        getList.excute(model);
        trashFileDelete.excute();
		return "list";
	}
	
	
	@RequestMapping("/contents")
	public String contents(HttpServletRequest request, HttpServletResponse response, Model model) {
	
		System.out.println("contents() 실행됨");
		
		//조회수 증가 판단
		boolean increaseHit = true;
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("addHit_" + request.getParameter("id")) && cookie.getValue().equals("hit")) {
					increaseHit = false;
					break;
				}
			}
		}

		if(increaseHit) {  //조회수 증가 로직
			model.addAttribute("increaseHit", true);
			Cookie hitCookie = new Cookie("addHit_"+request.getParameter("id"), "hit");
			hitCookie.setMaxAge(60*60); //1시간  24*60*60 하루
			hitCookie.setPath("/"); //모든 루트에서 쿠키가 유효 함.
			response.addCookie(hitCookie); //쿠키 저장
		}else {
			model.addAttribute("increaseHit", false);
		}
		
		model.addAttribute("req", request);
		getContent.excute(model);
		return "contents";
	}
	
	@GetMapping("/write")
	public String write(Model model) {
	    String uploadDir = servletContext.getRealPath("/resources/upload/");
		System.out.println(uploadDir);
	    String imnum = UUID.randomUUID().toString();
	    model.addAttribute("imnum", imnum);

		return "write";
	}
	
	@PostMapping("/write")
	public String writeok(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		System.out.println("writeok() 실행됨");
		model.addAttribute("request", request);
		setContent.excute(model);
		
		return "redirect:list";
	}
	
	@GetMapping("/rewrite")
	public String rewrite(HttpServletRequest request, HttpServletResponse response, Model model ) {
	    String uploadDir = servletContext.getRealPath("/resources/upload/");
		System.out.println(uploadDir);
	    String imnum = UUID.randomUUID().toString();
	    //int id = Integer.parseInt(request.getParameter("id"));
	    int refid = Integer.parseInt(request.getParameter("refid"));
	    int depth = Integer.parseInt(request.getParameter("depth"))+ 1;
	    int renum = Integer.parseInt(request.getParameter("renum"))+ 1;
	    
	    System.out.println("깊이 : " + depth);
	    System.out.println("번호 : " + renum);
	    
	    model.addAttribute("imnum", imnum);
	    //model.addAttribute("id", id);
	    model.addAttribute("refid", refid);
	    model.addAttribute("depth", depth);
	    model.addAttribute("renum", renum);
	    
		return "rewrite";
	}
	
	@PostMapping("/rewrite")
	public String rewritePost(HttpServletRequest request, HttpServletResponse response, Model model ) {
		model.addAttribute("request", request);
				
		setContent.excute(model);

		return "redirect:list";
	}
	
	
	@RequestMapping("/edit")
	public String edit(HttpServletRequest request, HttpServletResponse response, Model model) {
	    //mapper 확인
		//request.getParameter("id") 확인
		//SpGetContentService 실행해서 form 에 값을 넣어줌
		System.out.println("edit() 실행됨");
		
		model.addAttribute("increaseHit", false);
		model.addAttribute("req", request);
		getContent.excute(model);
		
		return "edit";
	}
	
	@PostMapping("/editok")
	public String editok(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println("editPost() 실행됨");

		Map<String, Object> params = new HashMap<>();
		try {
		   params.put("id", Integer.parseInt(request.getParameter("id")));
		   params.put("pass", request.getParameter("pass"));
		}catch(NumberFormatException e) {
			model.addAttribute("error", "에러가 발생했습니다.");
			return "redirect:edit?id="+request.getParameter("id");
		}
		   
		int rs = dao.validatePass(params);
		System.out.println(rs);
		if(rs > 0) {
			//db 수정 로직
			model.addAttribute("request", request);
			setEditContent.excute(model);
			return "redirect:contents?id="+request.getParameter("id");
		}else {
			//경고창
			model.addAttribute("error", "비밀번호가 틀렸습니다.");
			return "redirect:edit?id="+request.getParameter("id");
		}
		
	}
	
	//업로드로직
	@PostMapping("/uploadImage")
	@ResponseBody
	public ResponseEntity<?> handleImageUpload(@RequestParam("upload") MultipartFile uploadFile, @RequestParam("imnum") String imnum){
	  if(!uploadFile.isEmpty()) {
		try {
			
			//파일정보
			String oFilename = uploadFile.getOriginalFilename();
			String ext = oFilename.substring(oFilename.lastIndexOf(".") + 1).toLowerCase(); //확장자 추출
			String nFilename = Long.toString(System.currentTimeMillis() / 1000L)+"." + ext;

			long fileSize = uploadFile.getSize();
			String userId = "guest"; //추후 로그인 연동	
			
			//경로
			String uploadDir = servletContext.getRealPath("/resources/upload/");
			System.out.println(uploadDir);
        
			//저장
			File serverFile = new File(uploadDir + nFilename);
			uploadFile.transferTo(serverFile);
			
			//db 저장
			uploadFileDto.setExt(ext);
			uploadFileDto.setFilesize(fileSize);
			uploadFileDto.setImnum(imnum);
			uploadFileDto.setNfilename(nFilename);
			uploadFileDto.setOfilename(oFilename);
			uploadFileDto.setUserid(userId);
			
		    uploadDao.insertFile(uploadFileDto);
		    return ResponseEntity.ok().body("{\"url\":\"" + "/spboard/res/upload/" + nFilename + "\"}"); // Summernote가 요구하는 JSON 형
		
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("이미지 업로드 에러");
		}
	  }else {
		  return ResponseEntity.badRequest().body("파일없음");
	  }
	}
}
