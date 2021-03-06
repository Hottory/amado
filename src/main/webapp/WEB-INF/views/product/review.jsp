<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="col-12">
	<c:choose>
		<c:when test="${empty reviewList}">
			<div class="review-noti">이 상품의 첫 번째 리뷰를 작성해 주세요.</div>
		</c:when>
		<c:otherwise>
			<span style="float: right;">(${reviewCount})</span>
			<c:forEach items="${reviewList}" var="dto">
				<div class="review-summary">
					<div data-reviewid="${dto.num}"></div>
					<div class="review-left">
						<span class="review-star"> 
							<c:forEach begin="1" end="${dto.score}">
								<img src="${pageContext.request.contextPath}/resources/img/core-img/iconfinder_star_active.png">
							</c:forEach>
						</span>
						<span class="review-title">${dto.title}</span>
						<p class="mt-15">${dto.contents}</p>
					</div>
					<div class="review-rihgt">
						<div class="user" data-writer="${dto.name}">
							<span></span>
						</div>
						<p>${dto.reg_date}</p>
						<c:if test="${dto.id eq member.id}">
							<button class="btn review-delete">삭제</button>
						</c:if>
					</div>
				</div>
			</c:forEach>
			<c:if test="${next}">
				<a class="btn-more-review">리뷰 더 보기</a>
			</c:if>
		</c:otherwise>
	</c:choose>
</div>
<script type="text/javascript">
$(function() {
	message = "${param.reviewResult}";
	if (message != "") {
		alert(message);
	}
})
	$(".user").each(function() {
		var name = $(this).attr("data-writer");
		var firstName = name.substring(0, 1);
		var lastName = name.substring(name.length-1, name.length);
		if(name.length <= 2){
			lastName = "";
		}
		var blind = "●";
		for(i = 2; i < name.length-1; i++){
			blind += "●";
		}
		
		$(this).children().text(firstName + blind + lastName);
	});

	$(function() {
		message = "${param.msg}";
		if(message != ""){
			alert(message);
		}
	})
</script>