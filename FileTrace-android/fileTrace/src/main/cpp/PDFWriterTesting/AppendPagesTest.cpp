/*
   Source File : AppendPagesTest.cpp


   Copyright 2011 Gal Kahana PDFWriter

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   
*/
#include "PDFWriter.h"
#include "testing/TestIO.h"

#include <iostream>

using namespace std;
using namespace PDFHummus;

int AppendPagesTest(int argc, char* argv[])
{
	EStatusCode status;

	do
	{
	 	PDFWriter pdfWriter;

		status = pdfWriter.StartPDF(BuildRelativeOutputPath(argv,"AppendPagesTest.pdf"),ePDFVersion13,LogConfiguration(true,true,BuildRelativeOutputPath(argv,"AppendPagesTestLog.txt")));
		if(status != PDFHummus::eSuccess)
		{
			cout<<"failed to start PDF\n";
			break;
		}	

		EStatusCodeAndObjectIDTypeList result;
		
		result = pdfWriter.AppendPDFPagesFromPDF(BuildRelativeInputPath(argv,"Original.pdf"),PDFPageRange());
		if(result.first != PDFHummus::eSuccess)
		{
			cout<<"failed to append pages from Original.pdf\n";
			status = result.first;
			break;
		}
		result = pdfWriter.AppendPDFPagesFromPDF(BuildRelativeInputPath(argv,"XObjectContent.pdf"),PDFPageRange());
		if(result.first != PDFHummus::eSuccess)
		{
			cout<<"failed to append pages from XObjectContent.pdf\n";
			status = result.first;
			break;
		}

		result = pdfWriter.AppendPDFPagesFromPDF(BuildRelativeInputPath(argv,"BasicTIFFImagesTest.pdf"),PDFPageRange());
		if(result.first != PDFHummus::eSuccess)
		{
			cout<<"failed to append pages from BasicTIFFImagesTest.pdf\n";
			status = result.first;
			break;
		}
		status = pdfWriter.EndPDF();
		if(status != PDFHummus::eSuccess)
		{
			cout<<"failed in end PDF\n";
			break;
		}

	}while(false);

	return status == eSuccess ? 0:1;
}
