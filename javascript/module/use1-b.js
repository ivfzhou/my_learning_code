import moduleName from './use1-a.js'
console.log(moduleName) // { key: 'value' }

import { constValue } from './use1-a.js'
console.log(constValue) // 123

console.log(import.meta)

let abc = 'abc'
console.log(this) // undefined
console.log(globalThis)
console.log(globalThis.abc) // undefined
