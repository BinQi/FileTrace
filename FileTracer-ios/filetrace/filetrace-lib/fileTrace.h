//
// Created by JerryWu on 2023/2/24.
//

#ifndef FILETRACE_FILETRACE_H
#define FILETRACE_FILETRACE_H

#include <string>

class FileTrace {
public:
    static std::string getTag(std::string pdfPath);
    static std::string setTag(std::string pdfPath, std::string tag);

    static void setDebug(bool enableDebug);
private:
    static bool debugEnable;
};


#endif //FILETRACE_FILETRACE_H
