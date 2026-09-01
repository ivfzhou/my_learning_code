const obj = {
    a: 'a',
    b: 'b'
}

// 对象解构 + 重命名。
function fn({ a: aa }) {
    console.log(aa)
}

fn(obj)
