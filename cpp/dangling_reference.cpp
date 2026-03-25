#include <cstring>
#include <iostream>
#include <string>

#include "dangling_reference.hpp"
#include "forward_reference.hpp"

namespace gitee::com::ivfzhou::cpp {
    // 模拟一个"重量级"中间对象，析构时会清除内部数据
    struct state {
        char* buffer;

        explicit state(const char* text) {
            buffer = new char[64];
            std::strcpy(buffer, text);
        }

        ~state() {
            // 析构时把内存内容清零并释放，让悬挂引用读到的是垃圾
            std::memset(buffer, 0, 64);
            delete[] buffer;
            buffer = nullptr;
        }

        // 移动构造
        state(state&& other) noexcept : buffer(other.buffer) { other.buffer = nullptr; }
        // 禁止拷贝
        state(const state&) = delete;
        state& operator=(const state&) = delete;
    };

    // 轻量句柄，内部仅保存一个指向 HeavyNode::buffer 的指针
    struct result {
        const char* ptr; // 指向 HeavyNode 内部数据

        void print() const {
            if (ptr == nullptr) {
                std::cout << "ptr 为 nullptr!" << std::endl;
            } else if (ptr[0] == '\0') {
                std::cout << "ptr 指向的内容已被清零（悬挂引用！）" << std::endl;
            } else {
                std::cout << "ptr = \"" << ptr << "\"" << std::endl;
            }
        }
    };

    // 模拟查询结果：返回值类型（临时对象）
    struct temporary_type {
        state node;

        explicit temporary_type(const char* text) : node(state(text)) {}

        // 返回一个 Handle，其内部指针指向 this->node.buffer
        // 注意：返回的是值，不是引用——但 Handle 内部引用了 QueryResult 的数据！
        result handle() const { return result{node.buffer}; }
    };

    // 模拟文档类
    struct root {
        // 返回临时 QueryResult（值类型）
        temporary_type get_tmp(const char* xpath) const { return temporary_type(xpath); }
    };

    // 悬挂引用代码。
    void dangling_reference() {
        std::cout << "开始 - 悬挂引用代码" << std::endl;
        try {
            root obj;
            std::cout << "===== 悬挂引用场景 =====" << std::endl;
            const auto& bad = obj.get_tmp("/root/dict").handle();
            bad.print(); // 💥 读取已释放的内存

            std::cout << "===== 悬挂引用场景 =====" << std::endl;
            const auto also_bad = obj.get_tmp("/root/dict").handle();
            also_bad.print(); // 💥 同样是悬挂指针

            std::cout << "===== 真正安全的写法 =====" << std::endl;
            auto result = obj.get_tmp("/root/dict"); // ✅ 保持中间对象存活
            auto good = result.handle(); // Handle 指向仍然存活的 buffer
            good.print(); // ✅ 正常输出
        } catch (const std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "结束 - 悬挂引用代码" << std::endl << std::endl;
    }
}
