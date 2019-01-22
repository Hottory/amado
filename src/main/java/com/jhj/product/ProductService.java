package com.jhj.product;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.jhj.file.FileDAO;
import com.jhj.file.FileDTO;
import com.jhj.util.FileSaver;
import com.jhj.util.Pager;

@Service
public class ProductService {

	@Inject
	private ProductDAO productDAO;
	@Inject
	private FileDAO fileDAO;

	public ModelAndView list(Pager pager) throws Exception {
		pager.makeRow();
		pager.makePage(productDAO.getCount(pager));
		ModelAndView mv = new ModelAndView();
		List<ProductDTO> list = productDAO.list(pager);
		FileDTO fileDTO = new FileDTO();
		for (ProductDTO productDTO : list) {
			fileDTO.setNum(productDTO.getProductNum());
			fileDTO.setKind("p");
			productDTO.setFile(fileDAO.list(fileDTO));
		}
		mv.addObject("pager", pager);
		mv.addObject("list", list);
		return mv;
	}

	public ModelAndView selectOne(int productNum) throws Exception {
		ModelAndView mv = new ModelAndView();
		ProductDTO productDTO = productDAO.selectOne(productNum);
		String[] brand = { "Amado", "Ikea", "Furniture Inc", "The factory", "Artdeco" };
		String[] category = { "chair", "beds", "accesories", "furniture", "homeDeco", "table", "kid" };
		if (productDTO != null) {
			FileDTO fileDTO = new FileDTO();
			fileDTO.setNum(productDTO.getProductNum());
			fileDTO.setKind("p");
			mv.addObject("fileList", fileDAO.list(fileDTO));
			mv.addObject("brand", brand);
			mv.addObject("category", category);
			mv.addObject("productDTO", productDTO);
			mv.setViewName("product/select");
		} else {
			mv.addObject("msg", "존제하지 않는 상품입니다.");
			mv.setViewName("redirect:./shop");
		}
		return mv;
	}

	public ModelAndView insert(ProductDTO productDTO, List<MultipartFile> f1, HttpSession session) throws Exception {
		int seq = productDAO.seqNext();
		productDTO.setProductNum(seq);
		int result = productDAO.insert(productDTO);

		FileSaver fs = new FileSaver();
		String realPath = session.getServletContext().getRealPath("resources/img/product-img");
		System.out.println(realPath);

		if (result > 0) {
			for (MultipartFile data : f1) {
				if (data.isEmpty()) {
					continue;
				}
				FileDTO fileDTO = new FileDTO();
				fileDTO.setNum(productDTO.getProductNum());
				fileDTO.setOname(data.getOriginalFilename());
				fileDTO.setFname(fs.saveFile(realPath, data));
				fileDTO.setKind("p");

				result = fileDAO.insert(fileDTO);

				if (result < 1) {
					throw new SQLException();
				}
			}
		}
		ModelAndView mv = new ModelAndView();
		mv.addObject("msg", "작성 성공");
		return mv;
	}

	public ModelAndView update(ProductDTO productDTO, List<MultipartFile> f1, HttpSession session) throws Exception {
		int result = productDAO.update(productDTO);

		FileSaver fs = new FileSaver();
		String realPath = session.getServletContext().getRealPath("resources/img/product-img");
		System.out.println(realPath);

		if (result > 0) {
			for (MultipartFile data : f1) {
				if (data.isEmpty()) {
					continue;
				}
				FileDTO fileDTO = new FileDTO();
				fileDAO.delete(fileDTO);
				fileDTO.setNum(productDTO.getProductNum());
				fileDTO.setOname(data.getOriginalFilename());
				fileDTO.setFname(fs.saveFile(realPath, data));
				fileDTO.setKind("p");

				result = fileDAO.insert(fileDTO);

				if (result < 1) {
					throw new SQLException();
				}
			}
		}
		ModelAndView mv = new ModelAndView();
		return mv;
	}

	public String delete(int productNum, HttpSession session) throws Exception {
		int result = productDAO.delete(productNum);
		if (result > 0) {

			FileDTO fileDTO = new FileDTO();
			fileDTO.setNum(productNum);
			fileDTO.setKind("p");
			List<FileDTO> ar = fileDAO.list(fileDTO);

			if (ar.size() != 0) {
				result = fileDAO.deleteAll(fileDTO);

				String realPath = session.getServletContext().getRealPath("resources/img/product-img");
				for (FileDTO fileDTO2 : ar) {
					File file = new File(realPath, fileDTO2.getFname());
					file.delete();
				}
			}
		}

		return "삭제 성공";
	}

	@RequestMapping(value = "checkout", method = RequestMethod.GET)
	public ModelAndView checkout() throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("product/checkout");
		return mv;
	}

}
