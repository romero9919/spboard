<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
  <script src="res/js/summernote-bs4.js"></script>
  <script src="res/js/lang/summernote-ko-KR.min.js"></script>   
  <script>
  $(function(){
	  //글쓰기 링크
	  $("#contents").summernote({
		  height: 300,
		  tabsize: 2,
		  placeholder: "내용을 입력하세요.",
		  lang: 'ko-KR',
		  callbacks: {
			  onImageUpload: function(files){
                 sendData(files[0]);
			  }
		  }
	  });
	  
	  function sendData(file){
		  const imnum = $("#imnum").val();
		  const data = new FormData();
		  data.append("upload", file);
		  data.append("imnum", imnum);
		  $.ajax({
			  url: 'uploadImage',
			  type: 'POST',
			  data: data,
			  contentType: false,
			  processData: false,
			  success: function(data) {
				  const dt = JSON.parse(data);
				  $("#contents").summernote("insertImage", dt.url);
				  
			  },
              error: function(jqXHR, textStatus, errorThrown) {
                  console.error("Image upload failed: " + textStatus + " " + errorThrown);
              }
		  });
	  }
	  
	 
  });
  </script>
       <div class="write">
              <h2 class="text-center mt-4 mb-5 pb-4">글쓰기</h2>
              <form name="rewriteform" id="writeform" class="writeform row"
                    action="rewrite" method="post">
                  <!-- 게스트일때 적용 -->
                  <div class="col-12 row">
                     <div class="col-6 row form-group">
                        <label class="form-label">이름</label>
                        <input type="text" name="writer" id="writer" class="form-control" placeholder="이름" />
                     </div>
                     <div class="col-6 row form-group">
                        <label class="form-label">비밀번호</label>
                        <input type="password" name="pass" id="password" class="form-control" placeholder="비밀번호" />
                     </div>
                  </div>
                  <div class="col-12 row form-group">
                     <label class="form-label">제목</label>
                     <input type="text" name="title" id="title" class="form-control col-10" placeholder="제목" />
                  </div>
                  <div class="col-12">
                     <textarea name="content" id="contents" class="form-control"></textarea>
                  </div>
                  <!-- /게스트일때 적용-->
                  <div class="col-12 text-center my-5">
                     <a href="list.html" class="btn btn-danger px-5 mx-2">취소</a>
                     <button class="btn btn-primary px-5 mx-2" type="submit">글쓰기</button>
                  </div>
                  <input type="hidden" name="imnum" id="imnum" value="${imnum }" />
                  <!-- <input type="hidden" name="id" id="id" value="${id }" /> -->
                  <input type="hidden" name="refid" id="refid" value="${refid }" />
                  <input type="hidden" name="depth" id="depth" value="${depth }" />
                  <input type="hidden" name="renum" id="renum" value="${renum }" />
                  <input type="hidden" name="rewrite" value="ok" />
               </form>
           </div>