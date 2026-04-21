import {createApp, ref} from 'vue'
import ComponentA from './ComponentA.js'
import ComponentB from './ComponentB.js'

createApp({
    setup() {
        const name = ref('ComponentA')

        return {
            name
        }
    },
    components: {ComponentA, ComponentB},
    template: `
<div>
    <div>
        {{ name }}
        <br/>
        输入模板名：
        <input type="text" v-model="name"/>
    </div>
    <component :is="name"></component>
</div>
`,
}).mount('#app')
