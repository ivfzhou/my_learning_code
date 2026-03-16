#include <exception>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <ostream>
#include <string>

#include <pugixml.hpp>

#include "forward_reference.hpp"
#include "pugixml.hpp"

namespace gitee::com::ivfzhou::cpp::pugixml {
    constexpr std::string get_file_path() {
#ifdef WINDOWS
        return "../../pugixml/test.xml";
#elif defined LINUX
        return "../pugixml/test.xml";
#else
        return "";
#endif
    }

    // 加载 xml 数据。
    void example_1() {
        std::cout << "[pugixml] 开始 - 加载 xml 数据" << std::endl;
        try {
            pugi::xml_document object;
            auto result = object.load_file(get_file_path().c_str());
            LOG("description: ",
                result.description(),
                "status: ",
                result.status,
                "offset: ",
                result.offset,
                "encoding: ",
                result.encoding);

            result = object.load_string("<hello/>");
            LOG("description: ",
                result.description(),
                "status: ",
                result.status,
                "offset: ",
                result.offset,
                "encoding: ",
                result.encoding);

            constexpr char data[9] = {"<hello/>"};
            result = object.load_buffer_inplace(const_cast<void*>(static_cast<const void*>(data)), 8);
            LOG("description: ",
                result.description(),
                "status: ",
                result.status,
                "offset: ",
                result.offset,
                "encoding: ",
                result.encoding);

            std::ifstream stream(get_file_path().c_str());
            result = object.load(stream);
            stream.close();
            LOG("description: ",
                result.description(),
                "status: ",
                result.status,
                "offset: ",
                result.offset,
                "encoding: ",
                result.encoding);
        } catch (std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "[pugixml] 结束 - 加载 xml 数据" << std::endl << std::endl;
    }

    // 获取 xml 节点数据。
    void example_2() {
        std::cout << "[pugixml] 开始 - 获取 xml 节点数据" << std::endl;
        try {
            pugi::xml_document object;
            auto result = object.load_file(get_file_path().c_str());
            if (!result) LOG("加载 xml 文件失败: ", result.description());

            LOG(object.child("root").child("hello").child_value());
            LOG(object.child("root").child("hello").attribute("stringAttr").value());
            LOG(object.child("root").child("hello").attribute("integerAttr").as_int());
            LOG(object.child("root").child("hello").attribute("booleanAttr").as_bool());
            LOG(object.child("root").child_value("hello2"));

            auto node = object.child("root").find_child_by_attribute("hello", "findAttr", "any");
            LOG(node.attribute("findAttr").value());

            for (auto v = object.child("root").first_child(); v; v = v.next_sibling()) LOG(v.name());

            for (auto& v : object.children("root")) LOG(v.name(), "=", v.child_value());

            for (auto it = object.child("root").begin(); it != object.child("root").end(); ++it) {
                std::cout << it->name() << ": ";
                for (auto it2 = it->attributes_begin(); it2 != it->attributes_end(); ++it2)
                    std::cout << it2->name() << "=" << it2->value() << " ";

                std::cout << std::endl;
            }

            // 深度优先遍历。
            struct traverse : pugi::xml_tree_walker {
                bool for_each(pugi::xml_node& node) override {
                    for (auto i = 0; i < depth(); i++) std::cout << "  ";
                    std::cout << node.name() << std::endl;
                    return true;
                }
            };
            traverse t;
            object.traverse(t);
        } catch (std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "[pugixml] 结束 - 获取 xml 节点数据" << std::endl << std::endl;
    }

    // 使用 xpath 语法获取节点。
    void example_3() {
        std::cout << "[pugixml] 开始 - 使用 xpath 语法获取节点" << std::endl;
        try {
            pugi::xml_document object;
            auto result = object.load_file(get_file_path().c_str());
            if (!result) LOG("加载 xml 文件失败: ", result.description());

            auto node = object.select_node("/root/hello[@integerAttr = 1 and @stringAttr = 'a']");
            LOG(node.node().name());
        } catch (std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "[pugixml] 结束 - 使用 xpath 语法获取节点" << std::endl << std::endl;
    }

    // 修改 xml 节点和属性得值。
    void example_4() {
        std::cout << "[pugixml] 开始 - 修改 xml 节点和属性得值" << std::endl;
        try {
            pugi::xml_document object;
            auto result = object.load_file(get_file_path().c_str(), pugi::parse_default | pugi::parse_comments);
            if (!result) LOG("加载 xml 文件失败: ", result.description());

            auto root = object.child("root");

            // 修改节点名称。
            LOG("node name before changed is", root.child("hello").name());
            if (!root.child("hello").set_name("changed_name")) LOG("改变节点名称失败");
            LOG("node name after changed is", root.child("changed_name").name());

            // 修改注释。
            if (!root.last_child().set_value("comment changed")) LOG("修改注释失败");
            LOG("comment changed is", root.last_child().value());

            // 修改属性的名称。
            auto attr = root.child("changed_name").attribute("stringAttr");
            LOG("node attribute name is", attr.name());
            attr.set_name("attr_changed");
            LOG("node attribute name changed is", attr.name());

            // 修改属性的值。
            attr.set_value(1);
            LOG("node attribute value is", attr.value());
            attr = false;
            LOG("node attribute value is", attr.value());

            object.print(std::cout);
        } catch (std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "[pugixml] 结束 - 修改 xml 节点和属性得值" << std::endl << std::endl;
    }

    // 添加 xml 节点和属性。
    void example_5() {
        std::cout << "[pugixml] 开始 - 添加 xml 节点和属性" << std::endl;
        try {
            pugi::xml_document object;
            auto result = object.load_file(get_file_path().c_str(), pugi::parse_default | pugi::parse_comments);
            if (!result) LOG("加载 xml 文件失败: ", result.description());

            auto root = object.child("root");

            // 添加节点。
            auto hello3Node = root.append_child("hello3");
            hello3Node.append_child(pugi::node_pcdata).set_value("xml value");
            root.insert_child_after("hello4", hello3Node);

            // 添加节点属性。
            auto attr = hello3Node.append_attribute("attr");
            attr = 1.1;
            hello3Node.insert_attribute_after("attr2", attr) = "a";

            object.print(std::cout);
        } catch (std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "[pugixml] 结束 - 添加 xml 节点和属性" << std::endl << std::endl;
    }

    // 删除 xml 节点和属性。
    void example_6() {
        std::cout << "[pugixml] 开始 - 添加 xml 节点和属性" << std::endl;
        try {
            pugi::xml_document object;
            auto result = object.load_file(get_file_path().c_str(), pugi::parse_default | pugi::parse_comments);
            if (!result) LOG("加载 xml 文件失败: ", result.description());

            auto root = object.child("root");

            // 删除节点。
            if (!root.remove_child("hello2")) LOG("删除节点失败");

            // 删除属性。
            if (!root.child("hello").remove_attribute("stringAttr")) LOG("删除属性失败");

            object.print(std::cout);
        } catch (std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "[pugixml] 结束 - 添加 xml 节点和属性" << std::endl << std::endl;
    }

    // 将 xml 数据保存到某地。
    void example_7() {
        std::cout << "[pugixml] 开始 - 将 xml 数据保存到某地" << std::endl;
        try {
            pugi::xml_document object;
            auto result = object.load_file(get_file_path().c_str(), pugi::parse_default | pugi::parse_comments);
            if (!result) LOG("加载 xml 文件失败: ", result.description());

            // 将 xml 数据写入文件。
            const auto& filePath = std::filesystem::temp_directory_path() / "test.xml";
            if (!object.save_file(filePath.c_str())) LOG("写入文件失败");

            // 将 xml 数据写入流。
            object.save(std::cout);

            // 将 xml 数据写入自定义的流。
            struct writer : pugi::xml_writer {
                void write(const void* data, size_t size) override {
                    const auto& string = static_cast<const char*>(data);
                    for (size_t i = 0; i < size; i++) std::cout << string[i];
                }
            };
            writer w;
            object.save(w);
        } catch (std::exception& e) {
            LOG("发生错误：", e.what());
        }
        std::cout << "[pugixml] 结束 - 将 xml 数据保存到某地" << std::endl << std::endl;
    }
}
