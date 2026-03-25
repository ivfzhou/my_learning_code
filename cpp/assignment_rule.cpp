#include <exception>
#include <iostream>
#include <ostream>
#include <utility>

#include "assignment_rule.hpp"
#include "forward_reference.hpp"

namespace gitee::com::ivfzhou::cpp {
    std::ostream& operator<<(std::ostream& out, const type& t) {
        out << t.x << std::endl;
        return out;
    }

    type::type(const int& x) : x(x) {}

    type::type(const type& t) {
        this->x = t.x;
        std::cout << "复制构造：x = " << this->x << std::endl;
    }

    type::type(type&& t) noexcept {
        this->x = t.x;
        t.x = 0;
        std::cout << "移动构造：x = " << this->x << std::endl;
    }

    type& type::operator=(const type& t) {
        this->x = t.x;
        std::cout << "复制赋值：x = " << this->x << std::endl;
        return *this;
    }

    type& type::operator=(type&& t) noexcept {
        this->x = t.x;
        t.x = 0;
        std::cout << "移动赋值：x = " << this->x << std::endl;
        return *this;
    }

    // 测试各种赋值效果表现。
    // 核心规则
    // 初始化（声明时用 =） 和 赋值（已有对象用 =） 是完全不同的：
    // 初始化：调用的是构造函数（复制构造 / 移动构造）
    // 赋值：调用的是 operator=（复制赋值 / 移动赋值）
    void assignment_rule() {
        std::cout << "开始 - 测试各种赋值效果表现" << std::endl;
        try {
            {
                std::cout << "初始化场景" << std::endl;

                // 左值
                type t0(1);
                type t1 = t0; // 复制构造

                // const 左值
                const type t2(1);
                type t3 = t2; // 复制构造

                // 左值引用
                type& t4 = t0;
                type t5 = t4; // 复制构造

                // const 左值引用
                const type& t6 = t0;
                type t7 = t6; // 复制构造

                // 右值（临时对象）
                type t8 = type(1); // 移动构造（但通常被 NRVO/拷贝消除 优化掉，直接普通构造）

                // 右值引用变量
                type&& t9 = type(1);
                type t10 = t9; // 复制构造（⚠️ 具名右值引用是左值！）

                // const 右值引用变量
                const type&& t11 = type(1);
                type t12 = t11; // 复制构造

                type t13 = std::move(t0); // 移动构造

                type t14 = std::move(t2); // 复制构造（const 右值只能匹配 const type&）
            }

            {
                std::cout << "赋值场景" << std::endl;
                // 左值
                type t0(1);
                type t1(1);
                t1 = t0; // 复制赋值 operator=(const type&)

                // const 左值
                const type t2(1);
                t1 = t2; // 复制赋值

                // 左值引用
                type& t3 = t1;
                t1 = t3; // 复制赋值

                // const 左值引用
                const type& t4 = t0;
                t1 = t4; // 复制赋值

                // 右值（临时对象）
                t1 = type(3); // 复制赋值（如果你没定义移动赋值运算符） 如果有 operator=(type&&) → 移动赋值

                // 右值引用变量
                type&& t5 = type(1);
                t1 = t5; // 复制赋值（具名右值引用是左值）

                // const 右值引用变量
                type&& t6 = type(1);
                t1 = t6; // 复制赋值

                // std::move(x)
                t1 = std::move(t0); // 复制赋值（如果没有移动赋值运算符）如果有 operator=(type&&) → 移动赋值

                // std::move(cx)
                t0 = std::move(t2); // 复制赋值
            }

            {
                std::cout << "引用绑定场景" << std::endl;

                // 引用绑定 不触发任何构造或赋值运算符，只是建立别名：

                // 非const左值
                type t0(1);
                type& t1 = t0; // 无触发

                // 左值/右值/const都可
                const type& t2 = t0; // 无触发，但会延长临时对象生命周期

                // 右值
                type&& t4 = type(1); // 无触发，但会延长临时对象生命周期

                // 右值
                const type&& t5 = type(1); // 无触发
            }
        } catch (const std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "结束 - 测试各种赋值效果表现" << std::endl << std::endl;
    }
}
