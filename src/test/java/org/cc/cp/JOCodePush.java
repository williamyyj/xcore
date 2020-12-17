package org.cc.cp;

import org.cc.data.CCData;

import java.io.File;
import java.util.List;

public class JOCodePush {

	public static void main(String[] args) throws Exception {
		File flist = new File("D:\\HHome\\GoogleDrive\\myjob\\resources\\project\\baphiq\\codebase","cp20201026_front.txt");
		List<String> files = CCData.loadList(flist,"UTF-8");
		for(String f : files){
			try {
				//String fsrc = f.replace("${front}", "D:\\HHome\\hyweb\\svn-repos\\java\\baphiq\\admin\\WebContent");
				String fsrc = f.replace("${front}", "D:\\HHome\\hyweb\\svn-repos\\java\\baphiq\\mwork\\target\\mwork-1.0-SNAPSHOT");
				String fdesc = f.replace("${front}", "D:\\HHome\\hyweb\\維護\\農藥\\codepush\\20201026\\front");
				CCData.copy(fsrc, fdesc);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

}
