# 一、声明

\<!DOCTYPE *根元素* [*元素声明*]>

# 二、元素

1. \<!ELEMENT *元素名称* EMPTY>
1. \<!ELEMENT *元素名称* (#PCDATA)>
1. \<!ELEMENT *元素名称* ANY>
1. \<!ELEMENT *元素名称* (*子元素名称1*, *子元素名称2*, ...)>
1. \<!ELEMENT *元素名称* (*子元素名称*+)>：声明最少出现一次的元素。
1. \<!ELEMENT *元素名称* (*子元素名称*\*)>：声明出现零次或多次的元素。
1. \<!ELEMENT *元素名称* (*子元素名称*?)>：声明出现零次或一次的元素。
1. \<!ELEMENT *元素名称* (to,from,header,(message|body))>：声明“非...既...”类型的内容。
1. \<!ELEMENT *元素名称* (#PCDATA|to|from|header|message)*>：声明混合型的内容。

# 三、属性

\<!ATTLIST *元素名称* *属性名称* *属性类型* *默认值*>

# 四、属性类型

1. CDATA：值为字符数据 。
1. (*en1*|*en2*|...)：此值是枚举列表中的一个值。
1. ID：值为唯一的 ID。
1. IDREF：值为另外一个元素的 ID。
1. IDREFS：值为其他 ID 的列表。
1. NMTOKEN：值为合法的 XML 名称。
1. NMTOKENS：值为合法的 XML 名称的列表。
1. ENTITY：值是一个实体。
1. ENTITIES：值是一个实体列表。
1. NOTATION：此值是符号的名称。
1. xml：值是一个预定义的 XML 值。

# 五、默认值

1. [*值*]：属性的默认值。
1. #REQUIRED：属性值是必需的。
1. #IMPLIED：属性不是必需的。
1. #FIXED [*value*]：属性值是固定的。

# 六、实体

一个实体由三部分构成：一个和号 `&`，一个实体名称，以及一个分号`;`  
\<!ENTITY *实体名称* "*实体的值 URL*">
