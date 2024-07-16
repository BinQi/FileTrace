//
// Created by JerryWu on 2023/2/24.
//

#include <string>
#include "PDFWriter/PDFWriter.h"
#include "PDFWriter/PDFPage.h"
#include "PDFWriter/PageContentContext.h"
#include "PDFWriter/InfoDictionary.h"
#include "PDFWriter/TrailerInformation.h"
#include "PDFWriter/ParsedPrimitiveHelper.h"
#include "PDFWriter/PDFObjectCast.h"
#include "PDFWriter/PDFDictionary.h"
#include "AbstractContentContext.h"
#include "fileTrace.h"

static const std::string ADDITIONAL_INFO_KEY = "ctgFTag";
static const std::string PDF_INFO_Key = "Info";
static const std::string KEY_SPLITTER = "+";
static const std::string SEG_SPLITTER = "||";

bool FileTrace::debugEnable = false;

void FileTrace::setDebug(bool enableDebug) {
  debugEnable = enableDebug;
}

std::string FileTrace::setTag(std::string pdfPath, std::string tag) {
  std::string hello = pdfPath + SEG_SPLITTER;

  // modify pdf
  PDFWriter pdfWriter;
  EStatusCode status = pdfWriter.ModifyPDF(pdfPath, ePDFVersionUndefined, "");
  if(status != PDFHummus::eSuccess) {
    return "pdfWriter modify open pdf file fail";
  }
  InfoDictionary& infoDictionary = pdfWriter.GetDocumentContext().GetTrailerInformation().GetInfo();
  infoDictionary.AddAdditionalInfoEntry(ADDITIONAL_INFO_KEY, PDFTextString(tag));

  status = pdfWriter.EndPDF();
  if(status != PDFHummus::eSuccess) {
    return "pdfWriter end pdf fail";
  }
  return "success";
}

std::string FileTrace::getTag(std::string pdfPath) {
  std::string tag = "";
  std::string debugInfo = pdfPath + SEG_SPLITTER;
  // parse pdf
  PDFParser parser;
  InputFile pdfFile;
  EStatusCode status = pdfFile.OpenFile(pdfPath);
  if(status != PDFHummus::eSuccess) {
    return "open pdf file fail";
  }
  status = parser.StartPDFParsing(pdfFile.GetInputStream());
  if(status != PDFHummus::eSuccess) {
    return "parse pdf file fail, unable to parse input file";
  }
  RefCountPtr<PDFDictionary> theTrailerSmartPtr;
  theTrailerSmartPtr = parser.GetTrailer();
  MapIterator<PDFNameToPDFObjectMap> iterator = theTrailerSmartPtr->GetIterator();
  if (FileTrace::debugEnable) {
    while (!iterator.IsFinished() && iterator.MoveNext()) {
      debugInfo += iterator.GetKey()->GetValue() + KEY_SPLITTER;
    }
  }
  if (theTrailerSmartPtr->Exists(PDF_INFO_Key)) {
    debugInfo += SEG_SPLITTER;
    RefCountPtr<PDFDictionary> infoDict;
    infoDict = (PDFDictionary *) parser.QueryDictionaryObject(
        parser.GetTrailer(),
        PDF_INFO_Key);
    if (debugEnable) {
      iterator = infoDict->GetIterator();
      while (!iterator.IsFinished() && iterator.MoveNext()) {
        debugInfo += iterator.GetKey()->GetValue() + KEY_SPLITTER;
      }
    }
    if (infoDict->Exists(ADDITIONAL_INFO_KEY)) {
      tag = ParsedPrimitiveHelper(
          infoDict->QueryDirectObject(ADDITIONAL_INFO_KEY)).ToString();
    }
  }
  parser.ResetParser();
  pdfFile.CloseFile();

  if (debugEnable) {
//        tag += SEG_SPLITTER + debugInfo;
  }

  return tag;
}