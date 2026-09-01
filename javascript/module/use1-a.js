import defualtName from './define.js'
console.log(defualtName) // { key: 'value' }

import { constValue } from './define.js'
console.log(constValue) // 123

// 引入目标的 default，重导出为当前模块的 default。
export { default, constValue } from './define.js'
