package com.jhj.util;

import java.io.File;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileSaver {

	public String saveFile(String realPath, MultipartFile multipartFile) throws Exception {
		String fileSystemName = "";
		// 1. 저장할 경로명 realPath
		File file = new File(realPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		// 2. 저장할 파일명
		fileSystemName = UUID.randomUUID().toString();
		String oname = multipartFile.getOriginalFilename();
		oname = oname.substring(oname.lastIndexOf('.'));
		fileSystemName = fileSystemName + oname;
		file = new File(realPath, fileSystemName);

		multipartFile.transferTo(file);

		return fileSystemName;
	}

}
