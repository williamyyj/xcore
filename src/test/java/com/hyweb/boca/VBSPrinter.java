package com.hyweb.boca;

public class VBSPrinter {
    public static void main(String[] args){
        String vbsCode =
                "DIM objWord\r\n"
                        + "SET objWord = CreateObject(\"Word.Application\")\r\n"
                        + "objWord.ActivePrinter = \"" + "${}"+ "\"\r\n"
                        + "objWord.Documents.Open(\"" + "${}" + "\")\r\n"
                        + "objWord.Visible = False\r\n"
                        + "objWord.ScreenUpdating = False\r\n"
                        + "objWord.ActiveDocument.PrintOut False\r\n"
                ;
/*
		if(this.pageWidth != null) {
			vbsCode +=
					"objWord.ActiveDocument.PageSetup.Pagewidth=objWord.CentimetersToPoints(" + this.pageWidth + ")\r\n"
					+ "objWord.ActiveDocument.PageSetup.PageHeight=objWord.CentimetersToPoints(" + this.pageHeight + ")\r\n"
					+ "objWord.ActiveDocument.PageSetup.TopMargin=0\r\n"
					+ "objWord.ActiveDocument.PageSetup.BottomMargin=0\r\n";
		}
*/

        vbsCode +=
                "objWord.ActiveDocument.Close 0\r\n"
                        + "objWord.Quit\r\n"
                        + "SET objWord = Nothing\r\n";
        System.out.println(vbsCode);
    }
}
