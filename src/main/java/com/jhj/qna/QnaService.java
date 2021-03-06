package com.jhj.qna;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.jhj.board.BoardDTO;
import com.jhj.board.BoardService;
import com.jhj.file.FileService;
import com.jhj.util.Pager;

@Service
public class QnaService implements BoardService {

	@Inject
	private QnaDAO qnaDAO;
	@Inject
	private FileService fileService;

	@Override
	public ModelAndView list(Pager pager) throws Exception {
		int totalCount = qnaDAO.totalCount(pager);
		pager.makePage(totalCount);
		pager.makeRow();

		ModelAndView mv = new ModelAndView();
		mv.addObject("list", qnaDAO.list(pager));
		mv.addObject("pager", pager);
		return mv;
	}

	@Override
	public ModelAndView select(int num) throws Exception {
		ModelAndView mv = new ModelAndView();
		BoardDTO boardDTO = qnaDAO.select(num);
		if (boardDTO != null) {
			qnaDAO.hitUp(num);
			mv.addObject("dto", boardDTO);
			mv.setViewName("board/boardSelect");
		} else {
			mv.addObject("msg", "해당 글이 존재하지않습니다.");
			mv.setViewName("redirect:./qnaList");
		}
		return mv;
	}

	@Override
	public int insert(BoardDTO boardDTO, HttpSession session) throws Exception {
		return qnaDAO.insert(boardDTO);
	}

	@Override
	public int update(BoardDTO boardDTO, HttpSession session) throws Exception {
		return qnaDAO.update(boardDTO);
	}

	@Override
	public int delete(int num, HttpSession session) throws Exception {
		BoardDTO boardDTO = qnaDAO.select(num);

		ArrayList<String> curFiles = new ArrayList<String>();
		String temp = boardDTO.getContents();
		while(temp.indexOf("../resources/img/board/") > 0){
			int first = temp.indexOf("../resources/img/board/")+23;
			int last = temp.indexOf("&#13;&#10;");
			String middel = temp.substring(first, last);
			curFiles.add(middel);
			temp = temp.replace("../resources/img/board/" + middel + "&#13;&#10;", "");
		}
		for (String fname : curFiles) {
			fileService.delete(fname, session);
		}
		return qnaDAO.delete(num);
	}

	public int reply(QnaDTO qnaDTO) throws Exception {
		// 부모의 ref, step, depth
		QnaDTO pQnaDTO = (QnaDTO) qnaDAO.select(qnaDTO.getNum());
		qnaDAO.replyUpdate(pQnaDTO);

		qnaDTO.setRef(pQnaDTO.getRef());
		qnaDTO.setStep(pQnaDTO.getStep() + 1);
		qnaDTO.setDepth(pQnaDTO.getDepth() + 1);
		
		return qnaDAO.reply(qnaDTO);
	}
}
