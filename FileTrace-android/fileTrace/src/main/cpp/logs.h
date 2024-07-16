//
// Created by JerryWu on 2023/2/23.
//
#pragma once

#define LOG_ERROR(fmt, ...) printf("[ERROR] %s %d " fmt "\n", __FILE__, __LINE__, ##__VA_ARGS__)
#define LOG_WRONG(fmt, ...) printf("[WRONG] %s %d " fmt "\n", __FILE__, __LINE__, ##__VA_ARGS__)
#define LOG_INFO(fmt, ...)  printf("[INFO] %s %d " fmt "\n", __FILE__, __LINE__, ##__VA_ARGS__)
#define LOG_TRACE(fmt, ...) printf("[TRACE] %s %d " fmt "\n", __FILE__, __LINE__, ##__VA_ARGS__)

#ifdef _DEBUG
#define LOG_DEBUG(fmt, ...) printf("[DEBUG] %s %d " fmt "\n", __FILE__, __LINE__, ##__VA_ARGS__)
#else
#define LOG_DEBUG(fmt, ...) ((void)0)
#endif

