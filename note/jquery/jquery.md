# 一、CDN

1. 百度 https://libs.baidu.com/jquery/2.1.1/jquery.min.js
1. 微软 http://ajax.microsoft.com/ajax/jquery/jquery-2.1.1.min.js
1. 微软 http://ajax.aspnetcdn.com/ajax/jquery/jquery-2.1.1.min.js
1. 官方 http://code.jquery.com/jquery-2.1.1.min.js
1. 谷歌 http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js
1. 新浪 http://lib.sinaapp.com/js/jquery/1.6/jquery.min.js
1. 七牛 http://cdn.staticfile.org/jquery/2.1.1/jquery.min.js

# 二、选择器

多个选择表达式中间逗号分割会选择他们的并集，若是接壤着会选择它们的交集。字符串中 \\ 表示 \，\ 可以转义其它特殊字符。  
1. \* 选取所有元素，某些浏览器使用 * 的处理速度缓慢。
1. this 选取当前对象，当前HTML元素。
1. #ID名 选取为该id的元素，某些浏览器使用数字开头的ID名称可能会出问题。
1. .类名 选取为该类名的所有元素，某些浏览器使用数字开头的类名可能会出问题。
1. 元素 选取为该元素的所有元素。
1. 元素>元素 选取所有前元素下直接子元素为后元素的所有元素。
1. 元素 元素 选取所有前元素下子元素为后元素的所有元素。
1. 元素+元素 选取所有前元素下紧接的兄弟元素为后元素的元素。
1. 元素~元素 选取所有前元素下兄弟元素为后元素的所有元素。
1. \[属性名] 选取所有有该属性元素。
1. \[属性名=’属性值’] 选取有该属性且等于该值的所有元素。
1. \[属性名!=’属性值’] 选取有该属性但不等于该值的所有元素。
1. \[属性名^=’属性值’] 选取有该属性且属性值以该值开头的所有元素。
1. \[属性名$=’属性值’] 选取有该属性且属性值以该值结尾的所有元素。
1. \[属性名*=’属性值’] 选取有该属性且属性值中包含该值的所有元素。
1. :first 选取DOM树从根到尾第一个元素。
1. :last 选取DOM树中最后一个元素。
1. :first-child 选取属于其父元素的第一个子元素的所有元素。
1. :last-child 选取属于其父元素的最后一个子元素的所有元素。
1. :only-child 选取属于其父元素唯一子元素的所有元素。
1. :nth-child(数字) 选取属于其父元素第数字个子元素的所有元素，数字从1开始。
1. :nth-last-child(数字) 选取属于其父元素倒数第数字个子元素的所有元素，数字从1开始。
1. :first-of-type 选取属于其父元素第一个该类型的子元素的所有元素。
1. :last-of-type 选取属于其父元素最后一个该类型的子元素的所有元素。
1. :only-of-type 选取属于其父元素唯一一个该类型的子元素的所有元素。
1. :nth-of-type(数字) 选取属于其父元素第数字个该类型的子元素的所有元素，数字从1开始。
1. :nth-last-of-type(数字) 选取属于其父元素倒数第数字个该类型的子元素的所有元素，数字从1开始。
1. :input 选取所有input元素。
1. :text 选取所有type=”text”的input元素。
1. :password 选取所有type=”password”的input元素。
1. :radio 选取所有type=”radio”的input元素。
1. :checkbox 选取所有type=”checkbox”的input元素。
1. :submit 选取所有type=”submit”的input和button元素，如果button标签没有定义类型，大多数浏览器会把该元素当作类型为submit的按钮。
1. :reset 选取所有type=”reset”的input和button元素。
1. :button 选取所有type=”button”的input和button元素。
1. :image 选取所有type=”image”的input元素。
1. :file 选取所有type=”file”的input元素。
1. :enabled 选取所有未禁用的input和button元素。
1. :disabled 选取所有禁用的input和button元素。
1. :selected 选取当前选中的下拉列表元素。
1. :checked 选取所有checked=”checked”的input元素和当前选中的下拉列表元素。
1. :focus 选取当前聚焦的元素。
1. :has(元素) 选取包含指定元素的所有元素。
1. :empty 选取所有空内容的元素，会选取单标签。
1. :parent 选取所有含有子元素或者文本的元素。
1. :hidden 选取所有隐藏了的元素，例如 head、mate、script。
1. :visible 选取所有可见的元素。
    不可见元素：
    - 设置为display:none。
    - type=”hidden”的表单元素。
    - width和height设置为0。
    - 隐藏的父元素（同时隐藏所有子元素）。
1. :root 选取根元素，html。
1. :lang(语言代号) 选取有属性lang(lang)的元素，包含其子元素。
1. :contains(文本) 选取包含指定文本的元素及其父元素。
1. :target 选取触发a标签目标#id的元素。
1. :even 选取从0开始偶数位的元素，不关注父子关系。
1. :odd 选取从0开始奇数位的元素，不关注父子关系。
1. :eq(数字) 选取元素中第数字位的元素，数字从0开始。
1. :gt(数字) 选取元素中大于数字位的元素，数字从0开始。
1. :lt(数字) 选取元素中小于数字位的元素，数字从0开始。
1. :not(选择器) 选取非选择器选取的元素的元素，不排除子元素。
1. :header 选取标题元素，标题元素为h1-h6。
1. :animated 选取动画元素。

# 三、事件方法

1. bind(（事件名,可选传入函数的JSON格式数据,函数）或者（{事件名:函数,...}）) 向元素添加事件。多个事件名空格分割。同一事件多次添加，只触发最后添加的。
1. unbind(（可选事件名,可选函数名）或者（event）) 移除元素通过jQuery添加的事件。多个事件名空格隔开。不添加参数则移除该元素所有事件。
1. delegate(选择器, 事件名, 可选传入函数的JSON格式数据, 函数) 向元素的子元素添加事件。
1. undelegate(可选选择器, 可选事件名, 可选函数名) 移除通过delegate向子元素添加的事件。无参则移除所有。
1. live(事件名, 可选传入函数JSON格式数据, 函数) 向元素添加事件。
1. die(可选事件名, 可选函数名) 移除元素通过live添加的事件。
1. one(事件名, 传入函数的JSON格式数据, 函数) 向元素添加事件。不过该事件只运行一次。
1. click(可选函数) 触发或添加元素的单击事件。
1. toggle(函数, 函数...) 向元素添加点击事件。事件轮流触发函数。
1. dblclick(可选函数) 触发或添加元素的双击事件。
1. mouseenter(可选函数) 触发或添加元素的鼠标进入（不含子元素）事件。
1. mouseleave(可选函数) 触发或添加元素的鼠标离开（不含子元素）事件。
1. hover(函数1, 函数2) 等价于mouseenter和mouseleave。
1. mouseover(可选函数) 触发或添加元素的鼠标进入事件。
1. mouseout(可选函数) 触发或添加元素的鼠标离开事件。
1. mouesemove(可选函数) 触发或添加元素的鼠标移动事件。每移动一像素触发一次。
1. mouesedown(可选函数) 触发或添加元素的鼠标按下事件。
1. mouseup(可选函数) 触发或添加元素的鼠标松开事件。
1. focus(可选函数) 触发或添加元素的聚焦事件。
1. blur(可选函数) 触发或添加元素的失焦事件。
1. keydown(可选函数) 触发或添加元素的按键按下事件。
1. keyup(可选函数) 触发或添加元素的按键松开事件。
1. keypress(可选函数) 触发或添加元素的按键事件。不会触发所有的键(ALT、CTRL、SHIFT、ESC)。
1. load(函数) 添加元素（带有URL的元素）的加载后事件。
1. unload(函数) 添加window的页面离开事件。
1. change(可选函数) 触发或添加元素的改变（文本域、textarea、select）事件。
1. ready(函数) 添加元素的就绪事件。
1. resize(可选函数) 触发或添加浏览器窗口大小改变事件。
1. scroll(可选函数) 触发或添加可滚动元素的滚动事件。
1. select(可选函数) 触发或添加元素（input文本、textarea文本）的文本被选择事件。
1. submit(可选函数) 触发或添加表单元素的提交事件。
1. error(可选函数) 触发或添加元素的未正常加载事件。
1. trigger(（事件名,可选传入函数参数,...）或者（event）) 触发元素事件。
1. triggerHandler(事件名, 可选传入函数参数, ...) 触发元素事件。不会触发元素默认行为，且该方法返回事件函数的返回值。
1. 事件参数中的函数的参数event对象的属性和方法：
1. which 返回按键的数字值。
1. key
1. data 接收到的额外参数。
1. currentTarget 返回当前元素。
1. delegateTarget 返回当前元素。
1. relatedTarget 返回当鼠标移动时哪个元素进入或退出。
1. target 返回触发该事件元素。
1. namespace 返回当事件被触发时指定的命名空间。
1. pageX 返回相对于元素左边缘的鼠标位置。
1. pageY 返回相对于元素上边缘的鼠标位置。
1. result 返回最后一个事件函数返回值。
1. timeStamp 返回时间触发时间距离1970-1-1的毫秒数。
1. type 返回事件名。
1. metaKey 事件触发时META键是否被按下。
1. preventDefault() 阻止事件的默认行为，例如单击a标签打开链接，阻止对表单的提交。
1. isDefaultPrevented() 返回是否调用了event.preventDefault()。
1. stopImmediatePropagation() 阻止其他事件处理程序被调用。
1. isImmediatePropagationStopped() 返回是否调用了。
1. event.stopImmediatePropagation()。
1. stopPropagation() 阻止事件向上冒泡到DOM树，阻止任何父处理程序被事件通知。
1. isPropagationStopped() 返回是否调用了event.stopPropagation()。

# 四、动画方法

1. hide(可选速度, 可选回调函数) 隐藏元素。速度单位毫秒。默认0，可选normal slow fast。
1. show(可选速度, 可选回调函数) 显示元素。速度单位毫秒。默认0，可选normal slow fast。
1. toggle(（可选速度,可选回调函数）或者（布尔值）) 显示或隐藏元素。速度单位毫秒默认0，可选 slow fast normal。
1. fadeOut(可选速度, 可选回调函数) 隐藏元素。速度默认normal，可选solw fast，单位毫秒。
1. fadeIn(可选速度, 可选回调函数) 显示元素。速度默认normal，可选solw fast，单位毫秒。
1. fadeTo(可选速度, 透明度, 可选回调函数) 调准元素的透明度。速度默认normal，可选solw fast，单位毫秒。透明度值在0.00-1.00之间。
1. slideUp(可选速度, 可选回调函数) 滑动隐藏元素。速度默认normal，可选solw fast，单位毫秒。
1. slideDown(可选速度, 可选回调函数) 滑动显示元素。速度默认normal，可选solw fast，单位毫秒。
1. slideToggle(可选速度, 可选回调函数) 滑动显示或隐藏元素。速度默认normal，可选solw fast，单位毫秒。
1. animate(CSS JSON 格式字符串, 可选速度, 可选easing函数, 可选回调函数) 改变元素CSS样式。速度默认normel，可选slow fast，单位毫秒，内置easing函数有linear、swing。
1. animate(CSS JSON格式字符串, 参数) 同上。参数：{speed: easing: callback: step(每一步动画执行函数): queue(是否放入动画队列): specialEasing(指定前面 css 参数中每个属性对应的 easing函数): }。
1. stop(是否停止所有动画, 是否允许完成当前动画) 停止元素的动画函数。
1. clearQueue(可选队列名) 清除元素还未开始的动画。队列名默认标准队列fx。
1. delay(时间) 对元素动画队列中还未运行的函数设置延迟时间。
1. dequeue(可选队列名) 删除元素的下一个动画函数并执行。队列名默认标准队列fx。
1. queue(（可选队列名,可选新队列数组）或者（函数）) 显示元素动画队列状态或者设置元素队列。或者添加运行队列函数。
1. queue().length 返回队列长度。
1. jQuery.queue(元素, 可选队列名, 可选队列数组) 获取添加元素的队列。

# 五、元素方法

1. addClass(（类名）或者（函数(选择器元素位置(从0开始),原类名)）) 向元素添加类名。多个类名中间空格隔开。
1. removeClass(（类名）或者（函数(选择器元素位置(从0开始),原类名)）) 移除元素的类名。
1. hasClass(类名) 检查元素是否拥有指定的类名。
1. toggleClass(（类名,可选添加还是移除）或者（函数(可选选择器元素位置,可选当前类名),可选添加还是移除）) 切换元素的类名。
1. after({字符串}或者{函数(选择器元素位置(从0开始))}) 向元素后添加内容。可解析标签。
1. before(（字符串）或者（函数(选择器元素位置(从0开始))）) 向元素前添加内容。可解析标签。
1. append(（字符串）或者（函数(选择器元素位置(从0开始),原元素内容)）) 向元素追加内容。可解析标签。
1. prepend(（字符串）或者（函数(选择器元素位置(从0开始),原元素内容)）) 向元素开头插入内容。可解析标签。
1. replaceWith(内容或者选择器或者函数) 替换元素内容。
1. $(内容).appendTo(选择器) 向元素追加内容。可解析标签。
1. $(内容).prependTo(选择器) 向元素开头插入内容。可解析标签。
1. $(内容或者选择器).insertAfter(选择器) 向元素后添加内容或者将其它元素挪到其后。可解析标签。
1. $(内容或者选择器).insertBefore(选择器) 向元素前添加内容或者将其它元素挪到其前。可解析标签。
1. $(内容).replaceAll(选择器) 替换元素内容。
1. html(可选（内容或者函数(选择器元素位置(从0开始),原元素内容)）) 返回或设置元素的内容。返回内容为第一个元素的。
1. text(可选（内容或者函数(选择器元素位置(从0开始),原元素文本)）) 设置或返回元素的文本内容。会返回所有元素的组合文本内容。
1. val(可选（值或者函数(选择器元素位置(从0开始),原值)）) 设置或返回元素的值。返回内容为第一个元素的。
1. attr(（属性名）或者（属性名,属性值）或者（属性名,函数(选择器元素位置(从0开始),原值)）或者（JSON格式属性键值对）) 返回或设置元素的属性。
1. removeAttr(属性名) 移除元素属性。
1. clone(可选是否复制事件) 返回复制元素。
1. detach() 移除元素。但会保留事件和附加数据。
1. remove() 移除元素。该元素还保留在jQuery对象中。
1. empty() 将元素内容至空。
1. unwrap() 删除元素的父元素。
1. wrapAll(父元素字符串或者选择器或者元素对象) 将元素继承父元素。
1. wrapinner(元素字符串或者选择器或者函数) 将元素的内容用指定的元素包裹。
1. get(选择器元素位置(从0开始)) 返回元素对象。
1. index(可选选择器) 返回元素在集合中的位置。从0开始。
1. size() 返回元素个数。
1. toArray() 返回元素数组形式。
1. add(（选择器,可选位置(从0开始)）或者（元素对象）) 添加元素。
1. not(选择器或者函数(下标)或者元素对象) 返回删除了符合选择器或者元素对象或者函数返回 true 的元素集合。
1. filter(选择器或者函数(下标)) 返回符合选择器或函数返回true的元素。
1. has(选择器) 返回元素后代中有符合选择器的元素。
1. slice(开始下标, 可选结束下标) 返回元素从开始到结束下标的元素。下标从0开始，负数表示从尾部偏移量。
1. eq(下标) 返回指定位置的元素。负数表示从尾部偏移量。
1. find(选择器或者元素对象) 查找元素后代中符合选择器或者元素对象的元素。
1. andSelf() 经查找后再将元素添加进来。
1. end() 返回上一次的查找前的元素。
1. children(可选选择器) 返回元素符合选择器的直接子元素。
1. contents() 返回元素的直接子元素，包括文本和注释。
1. first() 返回第一个元素。
1. last() 返回最后一个元素。
1. siblings(可选选择器) 返回元素符合选择器的同胞元素。
1. next(可选选择器) 返回元素符合选择器的下一个直接同胞元素。
1. nextUntil(选择器1或者元素对象, 选择器2) 返回元素以下到符合选择器1或者元素对象且符合选择器2的同胞元素。
1. nextAll(可选选择器) 返回从元素以下符合选择器的同胞元素。
1. prev(可选选择器) 返回元素符合选择器上一个直接同胞元素。
1. prevUntil(可选选择器1或者元素对象, 选择器2) 返回元素以上到符合选择器1或者元素对象且符合选择器 2 的同胞元素。
1. prevAll(可选选择器) 返回从元素符以上符合选择器的同胞元素。
1. offsetParent() 返回元素最近定位的祖先元素。
1. parent(可选选择器) 返回元素符合选择器的直接父元素。
1. parents(可选选择器) 返回元素符合选择器的父元素。
1. parentsUntil(可选选择器1或者元素对象, 选择器2) 返回元素到符合选择器1或者元素对象且符合选择器2的父元素。
1. closest(选择器, 可选上下文) 从元素开始往DOM树上找到符合选择器，也在上下文中的元素。
1. each(函数(选择器元素位置(从0开始),元素)) 为每个元素执行函数。
1. map(函数) 返回由函数返回值组成的元素。
1. is(选择器或者函数) 元素符合选择器或者函数返回true，则返回true。

# 六、CSS 方法

1. css(（属性名,可选（属性值或者函数(可选选择器元素位置(从0开始),可选原属性值)））或者（JSON格式键值对对象）) 返回或设置元素样式。返回第一个匹配的元素。返回属性值时属性名不是简写属性名。
1. height(可选（高度值或者函数(可选选择器元素位置(从0开始),可选原高度)）) 返回第一个元素高度或设置元素高度。默认以像素为单位。
1. width(可选（高度值或者可选函数(可选选择器元素位置(从0开始),可选原高度)）) 返回第一个元素高度或设置元素高度。默认以像素为单位。
1. scrollLeft(可选距离) 返回或设置水平滚动条距离左边缘的距离。以像素计。
1. scrollTop(可选距离) 返回或设置垂直滚动条距离顶部的距离。以像素计。
1. offset(可选（{top:left}或者函数(可选选择器元素位置(从0开始),可选原位置对象)）) 返回第一个元素的位置对象或设置元素的位置。只对可见元素有效。
1. offsetParent() 返回最近定位的父元素。
1. position() 返回第一个匹配元素距离父元素的位置对象。以像素计。只对可见元素有效。

# 七、AJAX 方法

1. serialize() 返回表单元素URL序列化的字符串。禁用、没有name属性、提交按钮、文件按钮的数据将会忽略。
1. serializeArray() 返回将表单元素转化成的JSON对象数组。形如：[{name:"",value:""}...]。禁用、没有name属性、提交按钮、文件按钮的数据将会忽略。
1. load(地址, 可选发送参数, 可选函数(接受数据体,状态,xhr对象)) 发送AJAX请求将返回的数据替换元素内内容。发送参数为对象时为POST请求。地址隔空格后写选择器，选择部分数据替换元素内内容。
1. \$.get(地址, 可选发送参数，JSON对象或url字符串, 可选函数(接受数据体,状态,xhr对象), 可选接受数据类型) 发起GET类型AJAX请求。
1. \$.post(地址, 可选发送参数，JSON对象或url字符串, 可选函数(接受数据体,状态,xhr对象),可选接受数据类型) 发送POST类型AJAX请求。
1. \$.getJSON(地址, 可选发送参数，JSON对象或url字符串, 可选函数(接受数据体,状态,xhr对象)) 发起GET类型接受JSON数据的AJAX请求。发送参数中使用?表示使用jsonp格式调用异域数据。
1. \$.getScript(地址, 可选函数(接受数据体,状态,xhr对象)) 发起GET类型AJAX请求，可跨域接收脚本数据并执行。
1. \$.param(对象或数组, 是否浅层序列化) 返回将对象或数组URL序列化的字符串。
1. \$.ajax(可选JSON格式设置参数) 发起AJAX请求。返回xhr对象。
1. \$.ajaxSetup() 返回AJAX设置json对象。
1. url 请求地址。
1. data 发送的参数。
1. type 请求类型，默认GET。
1. dataType 接受数据类型。
1. async 是否异步请求，默认true。
1. contentType 请求内容编码类型，默认application/x-www-form-urlencoded。
1. username 设置请求用户名。
1. password 指定请求密码。
1. processData 是否转化发送参数，默认true。
1. traditional 是否浅层转化发送参数。默认flase。
1. timeout 设置超时时间，默认不超时，单位毫秒。
1. context 设置success中的this，默认是$.ajaxSetup()。
1. ifModified 是否服务器数据改变才发送请求，默认false。
1. cache 是否缓存，默认true。
1. jsonp 指定jsonp回调函数名，默认callback。
1. jsonpCallback 指定jsonp回调函数，默认自动生成。
1. scriptCharset 脚本字符设置。
1. xhr 函数返回值指定xhr对象。
1. beforeSend(xhr 对象) 发送前执行函数，返回false取消发送。
1. complete(xhr对象,请求类型) 请求发送后执行函数，无论请求失败还是成功都会执行。
1. dataFilter(接受到数据,请求类型) 成功接受到数据执行函数过滤数据。
1. success(接受数据体,状态,xhr) 请求成功回调函数。
1. error(xhr,失败信息,异常对象) 请求失败时执行函数。

# 八、其它方法

1. 使用方法链use chaining，浏览器不必多次查找元素。$、jQuery代表jQuery对象。
1. $.fn.jquery 返回jQuery版本号 3.5.1。
1. $(函数); 上面的简写。与 window.onload=function(){}; 的区别：后者是等到所有内容，包括外部图片之类的文件加载完后，才会执行且只执行一次，下次覆盖上次，前者则相反。
1. noConflict() 返回jQuery对象。
1. data(（可选数据名,可选数据值）或者（数据对象）) 添加获取元素自定义数据。
1. removeData(可选数据名) 移除元素的自定义数据。
1. jQuery.data(元素对象, 数据名, 数据值) 添加元素自定义数据。
1. jQuery.removeData(元素对象, 数据名) 移除元素自定义数据。
1. jQuery.hasData(元素对象) 如果元素有自定义数据，返回true。
1. jQuery(（选择器,可选上下文）或者（元素对象）或者（html字符串,JSON格式属性）或者（函数）)选择元素，创建元素，执行就绪函数。
1. context 返回上下文。
1. jquery 返回版本号。
1. length 返回元素个数。
1. jQuery.fx.interval 设置返回毫秒每帧的动画速度。默认13。
1. jQuery.fx.off 禁启用动画。
1. jQuery.support.属性 测试该属性是否支持。
1. ajax
1. boxModel
1. changeBubbles
1. checkClone
1. checkOn
1. cors
1. cssFloat
1. hrefNormalized
1. htmlSerialize
1. leadingWhitespace
1. noCloneChecked
1. noCloneEvent
1. opacity
1. optDisabled
1. optSelected
1. scriptEval()
1. style
1. submitBubbles
1. tbody
