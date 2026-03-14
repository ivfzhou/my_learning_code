#ifndef CPP_CONVERT_BY_CUSTOM_TYPE_HPP
#define CPP_CONVERT_BY_CUSTOM_TYPE_HPP

#include <map>
#include <string>
#include <vector>

#include <yaml-cpp/yaml.h>

namespace gitee::com::ivfzhou::cpp::yaml_cpp {

    // 自定义数据类与 yaml 节点的互相转换。
    void example_9();

    class custom_type final {
      public:
        int intValue;

        float floatValue;

        std::string stringValue;

        std::vector<int> vectorValue;

        std::map<std::string, std::string> mapValue;
    };
}

namespace YAML {
    using namespace gitee::com::ivfzhou::cpp::yaml_cpp;

    template<>
    struct convert<custom_type> {
        static Node encode(const custom_type&);
        static bool decode(const Node&, custom_type&);
    };
}

#endif
